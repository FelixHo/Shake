package com.shake.app.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
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

import com.shake.app.Define;
import com.shake.app.HomeApp;
import com.shake.app.R;
import com.shake.app.activity.MainActivity;
import com.shake.app.adapter.ContactAdapter;
import com.shake.app.model.Contact;
import com.shake.app.task.InitContactsTask;
import com.shake.app.task.InitContactsTask.onTaskListener;
import com.shake.app.utils.CommonUtils;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.ImageTools;
import com.shake.app.utils.LocationTools;
import com.shake.app.utils.LocationTools.MyLocationListener;
import com.shake.app.utils.MyJsonCreator;
import com.shake.app.utils.MySharedPreferences;
import com.shake.app.utils.MyToast;
import com.shake.app.utils.MyVibrator;
import com.shake.app.utils.ShakeEventDetector;
import com.shake.app.utils.ShakeEventDetector.OnShakeListener;
import com.shake.app.utils.ViewUtil;
import com.shake.app.utils.ZMQConnection;
import com.shake.app.utils.ZMQConnection.ZMQConnectionLisener;

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
    
    private Button connBtn;
    
    private ZMQConnection zmq = null;
    
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
		
		getContacts();
		
		setupView();
		
		initView();
	}
	
	private void getContacts()
	{
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
	}
	private void setupView()
	{
		listView = (ListView)layout.findViewById(R.id.contact_frag_listview);
		titleLayout = (LinearLayout)layout.findViewById(R.id.contact_listview_topbar);
		title = (TextView)layout.findViewById(R.id.contact_listview_topbar_label);
		menuBtn = (Button)layout.findViewById(R.id.contact_frag_menu_button);
		connBtn = (Button)layout.findViewById(R.id.contact_frag_connect_button);
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
				.setNeutralButton("删除",new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								boolean isSuccess = false;
								for(String number:numbers)
								{
									isSuccess = CommonUtils.deleteContact(getActivity(), number, contact.getName());
								}
								if(isSuccess)
								{
									MyToast.alert("已从通讯录移除 "+contact.getName()+" 联系人");
									HomeApp.setContactsList(null);
									getContacts();
								}
								else
								{
									MyToast.alert("操作失败!");
								}
							}
						})
				.setNegativeButton("取消", null)
				.setPositiveButton("传给朋友", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						/*****************************************************/
						
						final ProgressDialog progressDialog = new ProgressDialog(getActivity());
						progressDialog.setCancelable(false);
						
						MyToast.alert("请与要接收的手机进行一次轻碰");
						
						ShakeEventDetector.start(new OnShakeListener() {
							
							@Override
							public void onShake() {
								progressDialog.setMessage("正在建立连接...");
								progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										LocationTools.stop();
										if(zmq!=null)
										{
											zmq.closeSocket();
											Log.d("ZMQTask","cancel request....###########");
										}
									}
								});
								if(!progressDialog.isShowing())
								{
									progressDialog.show();
								}
								else
								{
									return;
								}
								LocationTools.toGetLocation(new MyLocationListener() {
									
									@Override
									public void onReceive(String[] location) 
									{
										progressDialog.setMessage("正在匹配...");
										MyVibrator.doVibration(300);
										JSONObject jso = new JSONObject();
										try 
										{
											String name_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_NAME_KEY, false);
											String avatar_path = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_AVATAR_KEY, false);
											jso.put("name",name_str);
											jso.put("lat", location[0]);//纬度
											jso.put("lon",location[1]);//经度
											if(avatar_path==null||avatar_path.equals(""))
											{
												jso.put("avatar","");
											}
											else
											{
												Bitmap avatar_small = ImageTools.decodeBitmapFromFileInSampleSize(avatar_path, ViewUtil.dp(120),ViewUtil.dp(120));
												jso.put("avatar",FileUtil.bitmapToBase64(avatar_small));
											}											
											String data = jso.toString();
											
											String connectREQ = MyJsonCreator.createJsonToServer("2","1",data,null);						
										
											Log.d("发出的JSON:", connectREQ);								
										
										ShakeEventDetector.stop();
										
										zmq = ZMQConnection.getInstance(Define.SERVER_URL, Define.MAC_ADDRESS);
										zmq.setConnectionListener(new ZMQConnectionLisener() {
											
											@Override
											public void onMessage(ZMQConnection mConnection, Socket socket, ZMsg resvMsg) {
												try{
														MyVibrator.doVibration(500);
										                String result = new String(resvMsg.getLast().getData());
										                Log.d("ZMQTask","onMEssage:     "+result);
										                resvMsg.destroy();
										                JSONObject jso = new JSONObject(result);
										                
										                int state = jso.getInt("state");
										                switch(state)
										                {
										                	case 200://匹配成功
										                		{
										                			String name = new JSONObject(jso.getString("data")).getString("name");//匹配者名字
										                			Bitmap match_avatar = FileUtil.base64ToBitmap(new JSONObject(jso.getString("data")).getString("avatar")); 
										                			Drawable icon = ImageTools.bitmapToDrawable(getActivity(), match_avatar);
										                			         icon = ImageTools.resizeDrawable(getActivity(), icon, ViewUtil.dp(60), ViewUtil.dp(60));
										                			final String target =  new JSONObject(jso.getString("data")).getString("id");
										                			if(progressDialog.isShowing())
											                		{
											                			progressDialog.dismiss();
											                		}
										                			
										                			final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
										                			.setTitle("匹配到 "+name+" 的手机")
										                			.setIcon(icon)
										                			.setMessage("是否继续?")
										                			.setNegativeButton("否", new DialogInterface.OnClickListener() {
																		
																		@Override
																		public void onClick(DialogInterface dialog, int which) {
																			
																			String cancelREQ = MyJsonCreator.createJsonToServer(null,"4",null, target);
																			zmq.send(cancelREQ,true);
																			MyToast.alert("请求已取消.");
																		}
																	})
																	.setPositiveButton("是", new DialogInterface.OnClickListener() {
																		
																		@Override
																		public void onClick(DialogInterface dialog, int which) {
																		
																			String data = contact.getJsonString();
																			String sendDataREQ = MyJsonCreator.createJsonToServer("2","3",data,target);
																			zmq.send(sendDataREQ, false);
																			progressDialog.show();
																			progressDialog.setMessage("正在发送...");
																		}
																	}).create();
										                			alertDialog.setCancelable(false);
										                			alertDialog.show();	
										                			break;
										                		}
										                	case 404://匹配失败
										                	{
										                		if(progressDialog.isShowing())
										                		{
										                			progressDialog.dismiss();
										                		}
										                		MyVibrator.doVibration(500);
										                		MyToast.alert("匹配失败:(");
										                		zmq.closeSocket();
										                		break;
										                	}
										                	case 301://发送成功
										                	{
										                		if(progressDialog.isShowing())
										                		{
										                			progressDialog.dismiss();
										                		}
										                		MyVibrator.doVibration(500);
										                		MyToast.alert("发送完成!");
										                		break;
										                	}
										                }
												}
								                catch( JSONException e)
								                {
								                	e.printStackTrace();
								                }
											}

											@Override
											public void onSendTimeOut(ZMQConnection mConnection) {
												
												if(progressDialog.isShowing())
												{
													progressDialog.dismiss();
												}
												mConnection.closeSocket();
												MyToast.alert("请求超时.");
											}
											
										});
										zmq.open();
										zmq.send(connectREQ,false);								
										
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});
							}
						});	
						
						/***********************************************************/
					}
				} )
				.show()
				.setCanceledOnTouchOutside(true);
			}
		});
		connBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/********************************************************************/
				final ProgressDialog progressDialog = new ProgressDialog(getActivity());
				progressDialog.setCancelable(false);
				
				MyToast.alert("请与要获取信息的手机进行一次轻碰");
				ShakeEventDetector.start(new OnShakeListener() {
					
					@Override
					public void onShake() {
						
						progressDialog.setMessage("正在建立连接...");
						progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								LocationTools.stop();
								if(zmq!=null)
								{
									zmq.closeSocket();
									Log.d("ZMQTask","cancel request....###########");
								}
							}
						});
						if(!progressDialog.isShowing())
						{
							progressDialog.show();
						}
						else
						{
							return;
						}
						LocationTools.toGetLocation(new MyLocationListener() {
							
							@Override
							public void onReceive(String[] location) {
								
								progressDialog.setMessage("正在匹配...");
								MyVibrator.doVibration(300);
								JSONObject jso = new JSONObject();
								try 
								{
									String name_str = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_NAME_KEY, false);
									String avatar_path = MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_AVATAR_KEY, false);
									jso.put("name",name_str);
									jso.put("lat", location[0]);//纬度
									jso.put("lon",location[1]);//经度
									if(avatar_path==null||avatar_path.equals(""))
									{
										jso.put("avatar","");
									}
									else
									{
										Bitmap avatar_small = ImageTools.decodeBitmapFromFileInSampleSize(avatar_path, ViewUtil.dp(120),ViewUtil.dp(120));
										jso.put("avatar",FileUtil.bitmapToBase64(avatar_small));
									}
									String data = jso.toString();
									
									String connectREQ = MyJsonCreator.createJsonToServer("2","2",data,null);						
								
									Log.d("发出的JSON:", connectREQ);								
								
								ShakeEventDetector.stop();
								zmq = ZMQConnection.getInstance(Define.SERVER_URL, Define.MAC_ADDRESS);
								zmq.setConnectionListener(new ZMQConnectionLisener() {
									
									@Override
									public void onMessage(ZMQConnection mConnection, Socket socket, ZMsg resvMsg) {
										try{
												MyVibrator.doVibration(500);
								                String result = new String(resvMsg.getLast().getData());
								                Log.d("ZMQTask","onMEssage:     "+result);
								                resvMsg.destroy();
								                JSONObject jso = new JSONObject(result);
								                
								                int state = jso.getInt("state");
								                
								                switch(state)
								                {
								                	case 200://匹配成功
								                	{
								                		MyVibrator.doVibration(500);
								                		progressDialog.setMessage("匹配成功,正在接收...");
								                		ZMQConnection.hasReturn = false;
								                		ZMQConnection.lastActTime = System.currentTimeMillis();
								                		break;
								                	}
								                	case 404://匹配失败
								                	{
								                		if(progressDialog.isShowing())
								                		{
								                			progressDialog.dismiss();
								                		}
								                		MyVibrator.doVibration(500);
								                		MyToast.alert("匹配失败:(");
								                		zmq.closeSocket();
								                		break;
								                	}
								                	case 999://连接取消
								                	{
								                		if(progressDialog.isShowing())
								                		{
								                			progressDialog.dismiss();
								                		}
								                		MyVibrator.doVibration(500);
								                		MyToast.alert("本次连接已被取消.");
								                		zmq.closeSocket();
								                		break;
								                	}
								                	case 300://接收成功
								                	{
								                		
								                		JSONObject data = new JSONObject(jso.getString("data"));
								                		JSONArray jsa = new JSONArray(data.getString("numbers"));
						                        		String[] numbers = new String[jsa.length()]; 
										                for(int i=0;i<jsa.length();i++)
										                {
										            	  numbers[i] = jsa.getString(i);
										                }
											            CommonUtils.insetContact(getActivity(),data.getString("name") , numbers, Base64.decode(data.getString("avatar"), Base64.DEFAULT));
								                		zmq.closeSocket();
								                		if(progressDialog.isShowing())
								                		{
								                			progressDialog.dismiss();
								                		}
								                		MyVibrator.doVibration(500);
								                		MyToast.alert("接收完成");
								                		HomeApp.setContactsList(null);
								                		getContacts();
								                		break;
								                		
								                	}
								                }
											}
							                catch( JSONException e)
							                {
							                	e.printStackTrace();
											}
									}

									@Override
									public void onSendTimeOut(ZMQConnection mConnection) {
										
										if(progressDialog.isShowing())
										{
											progressDialog.dismiss();
										}
										mConnection.closeSocket();
										MyToast.alert("请求超时.");
									}
								});
								
								zmq.open();
								zmq.send(connectREQ,false);	
								
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
				/********************************************************************/
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
