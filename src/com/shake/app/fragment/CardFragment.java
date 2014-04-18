package com.shake.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shake.app.Define;
import com.shake.app.R;
import com.shake.app.activity.MainActivity;
import com.shake.app.activity.SetinfoActivity;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.MySharedPreferences;

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
	
	public static final int EDIT_REQUEST_CODE = 0x999;
	
	public static final String EDIT_REQUEST_KEY = "EDIT_REQUEST";
	
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
	}
	
	private void initUserInfo()
	{
		name_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_NAME_KEY, false);
		profile_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_PROFILE_KEY, false);
		mobile_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_MOBILE_KEY, false);
		mail_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_EMAIL_KEY, false);
		birthday_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_BIRTH_KEY, false);
		homelink_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_HOMESITE_KEY, false);
		avatar_path = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_AVATAR_KEY, false);
		
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
		ImageLoader.getInstance().displayImage(FileUtil.getUriStringByPath(avatar_path), avatar);
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
}
