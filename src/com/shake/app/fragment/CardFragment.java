package com.shake.app.fragment;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shake.app.Define;
import com.shake.app.R;
import com.shake.app.activity.MainActivity;
import com.shake.app.activity.SetinfoActivity;
import com.shake.app.adapter.CardAdapter;
import com.shake.app.model.Card;
import com.shake.app.task.InitCardsTask;
import com.shake.app.task.InitCardsTask.OnTaskListener;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.LocationTools;
import com.shake.app.utils.LocationTools.MyLocationListener;
import com.shake.app.utils.MySharedPreferences;
import com.shake.app.utils.MyToast;
import com.shake.app.view.ShakeEventDetector;
import com.shake.app.view.ShakeEventDetector.OnShakeListener;

public class CardFragment extends Fragment {
	
	public View layout;
	
	private TextView name;
	
	private TextView profile;
	
	private TextView mobile;
	
	private TextView mail;
	
	private TextView birthday;
	
	private TextView homelink;
	
	private ImageView avatar;
	
	private String name_str;
	
	private String profile_str;
	
	private String mobile_str;
	
	private String mail_str;
	
	private String birthday_str;
	
	private String homelink_str;
	
	private String avatar_path;
	
	private Button menuBtn;
	
	private Button editBtn;
	
	private Button shareBtn;
	
	private ListView listView;
	
	private CardAdapter adapter;
	
	private ArrayList<Card> mCards = new ArrayList<Card>();
	
	private View dialogLayout;
	
	private TextView dialog_profile;
	
	private TextView dialog_mobile;
	
	private TextView dialog_mail;
	
	private TextView dialog_birthday;
	
	private TextView dialog_home;
	
	private ImageView dialog_avatar;
	
	public static final int EDIT_REQUEST_CODE = 0x999;
	
	public static final String EDIT_REQUEST_KEY = "EDIT_REQUEST";
	
	private InitCardsTask task;
	
	public CardFragment()
	{
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		layout = inflater.inflate(R.layout.fragment_card,null);
		
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupView();
		initUserInfo();
		initCardList();
	}
	
	private void setupView()
	{
		name = (TextView)layout.findViewById(R.id.card_frag_userinfo_name);
		profile = (TextView)layout.findViewById(R.id.card_frag_userinfo_profile);
		mobile = (TextView)layout.findViewById(R.id.card_frag_userinfo_mobile);
		mail = (TextView)layout.findViewById(R.id.card_frag_userinfo_mail);
		birthday = (TextView)layout.findViewById(R.id.card_frag_userinfo_birthday);
		homelink = (TextView)layout.findViewById(R.id.card_frag_userinfo_homelink);
		avatar = (ImageView)layout.findViewById(R.id.card_frag_userinfo_avatar);
		menuBtn = (Button)layout.findViewById(R.id.card_frag_menu_button);
		editBtn = (Button)layout.findViewById(R.id.card_frag_userinfo_edit_button);
		shareBtn = (Button)layout.findViewById(R.id.card_frag_userinfo_share_button);
		listView = (ListView)layout.findViewById(R.id.card_frag_card_listview);
	}
	
	/**
	 * 初始化自身名片信息
	 */
	private void initUserInfo()
	{
		name_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_NAME_KEY, false);
		profile_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_PROFILE_KEY, false);
		mobile_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_MOBILE_KEY, false);
		mail_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_EMAIL_KEY, false);
		birthday_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_BIRTH_KEY, false);
		homelink_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_HOMESITE_KEY, false);
		avatar_path = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_AVATAR_KEY, false);
		Log.i("头像地址", avatar_path);
		birthday_str = birthday_str.equals("")?"保密":birthday_str;
		homelink_str = homelink_str.equals("")?"<暂无个人主页信息>":homelink_str;
		profile_str  = profile_str.equals("")?"<暂无职业信息>":profile_str;
		
		name.setText(name_str);
		name.setSelected(true);
		profile.setText(profile_str);
		profile.setSelected(true);
		mobile.setText(mobile_str);
		mobile.setSelected(true);
		mail.setText(mail_str);
		mail.setSelected(true);
		birthday.setText(birthday_str);
		birthday.setSelected(true);
		homelink.setText(homelink_str);
		homelink.setSelected(true);
		if(avatar_path!=null&&!avatar_path.equals(""))
		{
			ImageLoader.getInstance().displayImage(FileUtil.getUriStringByPath(avatar_path), avatar);
		}
		menuBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				((MainActivity)getActivity()).toggleMenu();
			}
		});
		
		editBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),SetinfoActivity.class);
				intent.putExtra("EDIT_REQUEST", true);
				startActivityForResult(intent, EDIT_REQUEST_CODE);
			}
		});
		
		shareBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyToast.alert("请碰接另一台设备");
				ShakeEventDetector.start(new OnShakeListener() {
					
					@Override
					public void onShake() {
						MyToast.alert("正在定位...");
						LocationTools.toGetLocation(new MyLocationListener() {
							
							@Override
							public void onReceive(String[] location) 
							{
								MyToast.alert("纬度:"+location[0]+"\n经度:"+location[1]);
								ShakeEventDetector.stop();
							}
						});
					}
				});
				
			}
		});
	}
	
	/**
	 * 初始化名片列表
	 */
	private void initCardList()
	{		
		adapter = new CardAdapter(getActivity(), mCards);
		task = new InitCardsTask();
		task.setOnTaskListener(new OnTaskListener() {
			
			@Override
			public void onTaskFinished(ArrayList<Card> cards) {
				
				mCards = cards;
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						adapter.refreshData(mCards);
					}
				},400);
				
			}
		});
		task.execute(getActivity());
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
			AlertDialog dialog =new AlertDialog.Builder(getActivity()).create();
			dialog.show();
			dialogLayout = LayoutInflater.from(getActivity()).inflate(R.layout.card_detail,null);
			
			dialog_profile = (TextView)dialogLayout.findViewById(R.id.card_detail_profile);
			dialog_mobile = (TextView)dialogLayout.findViewById(R.id.card_detail_mobile);
			dialog_mail = (TextView)dialogLayout.findViewById(R.id.card_detail_mail);
			dialog_birthday = (TextView)dialogLayout.findViewById(R.id.card_detail_birthday);
			dialog_home = (TextView)dialogLayout.findViewById(R.id.card_detail_home);
			dialog_avatar = (ImageView)dialogLayout.findViewById(R.id.card_detail_avatar);
			
			Card card = (Card) adapter.getItem(position);
			
			dialog_profile.setText(card.getProfile());
			dialog_mobile.setText(card.getMobile());
			dialog_mail.setText(card.getMail());
			dialog_birthday.setText(card.getBirthday());
			dialog_home.setText(card.getHomelink());
			if(card.getAvatar().equals(""))
			{
				dialog_avatar.setImageResource(R.drawable.card_noavatar);
			}
			else
			{
				ImageLoader.getInstance().displayImage(FileUtil.getUriStringByPath(card.getAvatar()), dialog_avatar);
			}
			
			dialog.setContentView(dialogLayout);
			
			 WindowManager.LayoutParams lp = dialog.getWindow().getAttributes(); 
			 lp.width = Define.WIDTH_PX-50;
			 lp.height= LayoutParams.WRAP_CONTENT;
			 dialog.getWindow().setAttributes(lp);
			
			 dialog.setCanceledOnTouchOutside(true);
			 
			}
		});
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==EDIT_REQUEST_CODE)
		{
			if(resultCode==Activity.RESULT_OK)
			{
				initUserInfo();
				((MenuFragment)getActivity().getSupportFragmentManager()
				.findFragmentByTag("mMenuFragment"))
				.refreshMenu();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		
	    super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
