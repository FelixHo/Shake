package com.shake.app.fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shake.app.HomeApp;
import com.shake.app.R;
import com.shake.app.activity.MainActivity;
import com.shake.app.adapter.ContactAdapter;
import com.shake.app.model.Contact;
import com.shake.app.task.InitContactsTask;
import com.shake.app.task.InitContactsTask.onTaskListener;
import com.shake.app.utils.ImageTools;
import com.shake.app.utils.MyToast;
import com.shake.app.utils.ViewUtil;

public class ContactFragment extends Fragment {
	
    /**联系人**/
    private ArrayList<Contact> mContacts = new ArrayList<Contact>();
    
    private ListView listView = null;
    
    private ContactAdapter adapter = null;
    
    private View layout;
    
    private LinearLayout titleLayout;
    
    private TextView title;
    
    private InitContactsTask task;
    
    private ProgressDialog dialog;
    
    private Button menuBtn;
    
    /**
	 * 上次第一个可见元素，用于滚动时记录标识。
	 */
	private int lastFirstVisibleItem = -1;
    
	public ContactFragment() 
	{
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		layout = inflater.inflate(R.layout.fragment_contact, container, false);
		
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		task = new InitContactsTask();//读取联系人信息异步任务
		
		task.setOnTaskListener(new onTaskListener() {
			
			@Override
			public void onTaskStart() {
				
				if(HomeApp.getContactsList()==null)
				{
					dialog = ProgressDialog.show(getActivity(), "联系人","正在加载通讯录...", true,false);
					dialog.show();
				}
				
			}
			
			@Override
			public void onTaskFinished(ArrayList<Contact> list) {
				mContacts = list;
				adapter.setContacts(mContacts);
				if(dialog!=null&&dialog.isShowing())
				{
					dialog.dismiss();
				}
			}
		});
		
		task.execute(getActivity());
		
		setupView();
		
		initView();
	}
	
	private void setupView()
	{
		listView = (ListView)layout.findViewById(R.id.contact_frag_listview);
		titleLayout = (LinearLayout)layout.findViewById(R.id.contact_listview_topbar);
		title = (TextView)layout.findViewById(R.id.contact_listview_topbar_label);
		menuBtn = (Button)layout.findViewById(R.id.contact_frag_menu_button);
	}
	
	private void initView()
	{
		adapter = new ContactAdapter(getActivity(),mContacts);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) 
			{
				int section = adapter.getSectionForPosition(firstVisibleItem);
				int nextSecPosition = adapter.getPositionForSection(section + 1);
				if (firstVisibleItem != lastFirstVisibleItem) 
				{
					MarginLayoutParams params = (MarginLayoutParams) titleLayout.getLayoutParams();
					params.topMargin = 0;
					titleLayout.setLayoutParams(params);
					title.setText(String.valueOf(adapter.getItem(firstVisibleItem)));
				}
				
				//两个字母栏相碰时
				if (nextSecPosition == firstVisibleItem + 1)
				{
					View childView = view.getChildAt(0);
					if (childView != null) 
					{
						int titleHeight = titleLayout.getHeight();
						int bottom = childView.getBottom();
						MarginLayoutParams params = (MarginLayoutParams) titleLayout
								.getLayoutParams();
						if (bottom < titleHeight)//挤压动画效果的实现 
						{
							float pushedDistance = bottom - titleHeight;
							params.topMargin = (int) pushedDistance;
							titleLayout.setLayoutParams(params);
						} 
						else 
						{
							if (params.topMargin != 0) 
							{
								params.topMargin = 0;
								titleLayout.setLayoutParams(params);
							}
						}
					}
				}
				lastFirstVisibleItem = firstVisibleItem;
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
				
				final Contact contact = adapter.getCurrentContact(position);
				
				final String[] numbers = contact.getNumbers().toArray(new String[contact.getNumbers().size()]);
				
				Drawable icon;
				if(contact.getAvatar()!=null)
				{
					icon = new BitmapDrawable(getActivity().getResources(),contact.getAvatar());
				}
				else
				{
					icon = getResources().getDrawable(R.drawable.contact_noavatar);
				}
				icon = ImageTools.resizeDrawable(getActivity(), icon, ViewUtil.dp(60),ViewUtil.dp(60));
				
				new AlertDialog.Builder(getActivity())
				.setTitle(contact.getName())
				.setIcon(icon)
				.setItems(numbers, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						final String number = numbers[which];
						final String name = contact.getName();
						
						new AlertDialog.Builder(getActivity())
						.setMessage("是否拨打 "+number+"("+name+")?")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent call = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number));
								startActivity(call);
							}
						})
						.setNegativeButton("取消", null)
						.show()
						.setCanceledOnTouchOutside(true);						
					}
				})
				.setNegativeButton("取消", null)
				.setPositiveButton("传给朋友", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MyToast.alert("Coming soon");
					}
				} )
				.show()
				.setCanceledOnTouchOutside(true);
			}
		});
		
		menuBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				((MainActivity)getActivity()).toggleMenu();
			}
		});
	}	
}
