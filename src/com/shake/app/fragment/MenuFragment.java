package com.shake.app.fragment;


import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shake.app.Define;
import com.shake.app.R;
import com.shake.app.activity.MainActivity;
import com.shake.app.utils.MySharedPreferences;

public class MenuFragment extends Fragment {
	
	private View layout;
	
	private TextView name;
	
	private TextView mobile;
	
	private TextView email;
	
	private ImageView avatar;
	
	private ListView listView;
	
	private String[] menus = {"名片","联系人","图片","音乐","微博"};
	
	private MenuAdapter adapter;
	
	private Fragment frag;
	
	public MenuFragment() 
	{
		
	}	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		layout = inflater.inflate(R.layout.fragment_slidingmenu,null);
		
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		settupView();
		initView();
	}

	private void settupView()
	{
		name = (TextView)layout.findViewById(R.id.slidingmenu_textview_name);
		mobile = (TextView)layout.findViewById(R.id.slidingmenu_textview_mobile);
		email = (TextView)layout.findViewById(R.id.slidingmenu_textview_email);
		avatar = (ImageView)layout.findViewById(R.id.slidingmenu_imageview_avatar);
		listView = (ListView)layout.findViewById(R.id.slidingmenu_listview);
	}
	private void initView()
	{
		String _name = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_NAME_KEY, false);
		if(!_name.equals(""))
		{
			name.setText(_name);
		}
		String _mobile = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_MOBILE_KEY, false);
		if(!_mobile.equals(""))
		{
			mobile.setText(_mobile);
		}
		String _email = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_EMAIL_KEY, false);
		if(!_email.equals(""))
		{
			email.setText(_email);
		}
		String _avatarpath = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_AVATAR_KEY, false);
		if(!_avatarpath.equals(""))
		{
			ImageLoader.getInstance().displayImage(Uri.fromFile(new File(_avatarpath)).toString(), avatar);
		}
		adapter = new MenuAdapter(getActivity(), menus);
		listView.setAdapter(adapter);	
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				switch(position)
				{
					case 0: frag = new CardFragment(); break;
					case 1: frag = new ContactFragment(); break;
					case 2: frag = new ImageFragment();break;
					default: frag = new CardFragment();break;
				}
				switchFragment(frag);				
			}
		});
	}	
	/**
	 * 切换MainActivity的内容
	 * @param fragment
	 * @author Felix
	 */
	public void switchFragment(Fragment fragment) {
		
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainActivity) {
			MainActivity ma = (MainActivity) getActivity();
			ma.switchContent(fragment);
		}
	}
	
	/**
	 * 菜单项适配器
	 * @author Felix
	 *
	 */
	class MenuAdapter extends BaseAdapter
	{
		private Context context;
		
		private String[] menus;
		
		private LayoutInflater mInflater;
		
		public MenuAdapter(Context c,String[] data)
		{
			this.context = c;
			this.menus = data;
			this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			
			return menus.length;
		}

		@Override
		public Object getItem(int position) {
			
			return null;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View layout = mInflater.inflate(R.layout.slidingmenu_item, null);
			
			TextView itemName = (TextView)layout.findViewById(R.id.slidingmenu_item_name);
			
			itemName.setText(menus[position]);
			
			return layout;
		}
		
	}

}
