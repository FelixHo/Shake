package com.shake.app.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.shake.app.HomeApp;
import com.shake.app.R;
import com.shake.app.activity.MainActivity;
import com.shake.app.activity.SetinfoActivity;
import com.shake.app.adapter.CardAdapter;
import com.shake.app.db.CardDBManager;
import com.shake.app.model.Card;
import com.shake.app.task.InitCardsTask;
import com.shake.app.task.InitCardsTask.OnTaskListener;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.ImageTools;
import com.shake.app.utils.LocationTools;
import com.shake.app.utils.LocationTools.MyLocationListener;
import com.shake.app.utils.ShakeEventDetector.OnShakeListener;
import com.shake.app.utils.MyJsonCreator;
import com.shake.app.utils.MySharedPreferences;
import com.shake.app.utils.MyToast;
import com.shake.app.utils.MyVibrator;
import com.shake.app.utils.ShakeEventDetector;
import com.shake.app.utils.ZMQConnection;
import com.shake.app.utils.ZMQConnection.ZMQConnectionLisener;

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
	
	private Button connBtn;
	
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
		connBtn = (Button)layout.findViewById(R.id.card_frag_connect_button);
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
				
				final ProgressDialog progressDialog = new ProgressDialog(getActivity());
				progressDialog.setCancelable(false);
				
				MyToast.alert("请与要接收的手机进行一次轻碰");
				
				ShakeEventDetector.start(new OnShakeListener() {
					
					@Override
					public void onShake() {
						progressDialog.setMessage("正在定位...");
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
									jso.put("name",name_str);
									jso.put("lat", location[0]);//纬度
									jso.put("lon",location[1]);//经度
									String data = jso.toString();
									Log.d("ZMQJSON", data);
									String connectREQ = MyJsonCreator.createJsonToServer("1","1",data,null);						
								
									Log.d("发出的JSON:", connectREQ);								
								
								ShakeEventDetector.stop();
								
								final ZMQConnection zmq = ZMQConnection.getInstance(Define.SERVER_URL, Define.MAC_ADDRESS);
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
//								                			String name = jso.getJSONObject("data").getString("name");
								                			final String target =  new JSONObject(jso.getString("data")).getString("id");
								                			if(progressDialog.isShowing())
									                		{
									                			progressDialog.dismiss();
									                		}
								                			
								                			final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
								                			.setMessage("匹配到  "+name+" 的手机,是否继续?")
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
																	JSONObject jso = new JSONObject();
																	try 
																	{
																		jso.put("name",name_str);
																		jso.put("profile",profile_str);
																		jso.put("mobile",mobile_str);
																		jso.put("mail",mail_str);
																		jso.put("birthday",birthday_str);
																		jso.put("homelink",homelink_str);
																		if(avatar_path==null||avatar_path.equals(""))
																		{
																			jso.put("avatar","");
																		}
																		else
																		{
																			jso.put("avatar",FileUtil.fileToBase64(avatar_path));
																		}
																	} catch (JSONException e) {
																		e.printStackTrace();
																	}
																	String data = jso.toString();
																	String sendDataREQ = MyJsonCreator.createJsonToServer("1","3",data,target);
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
								                		zmq.close();
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
										mConnection.close();
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
			}
		});
		
		connBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final ProgressDialog progressDialog = new ProgressDialog(getActivity());
				progressDialog.setCancelable(false);
				
				MyToast.alert("请与要获取信息的手机进行一次轻碰");
				ShakeEventDetector.start(new OnShakeListener() {
					
					@Override
					public void onShake() {
						
						progressDialog.setMessage("正在定位...");
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
									jso.put("name",name_str);
									jso.put("lat", location[0]);//纬度
									jso.put("lon",location[1]);//经度
									String data = jso.toString();
									
									String connectREQ = MyJsonCreator.createJsonToServer("1","2",data,null);						
								
									Log.d("发出的JSON:", connectREQ);								
								
								ShakeEventDetector.stop();
								final ZMQConnection zmq = ZMQConnection.getInstance(Define.SERVER_URL, Define.MAC_ADDRESS);
								
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
								                		zmq.close();
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
								                		zmq.close();
								                		break;
								                	}
								                	case 300://接收成功
								                	{
								                		JSONObject data = new JSONObject(jso.getString("data"));
								                		Card card = new Card();
								                		card.setName(data.getString("name"));
								                		card.setProfile(data.getString("profile"));
								                		card.setMobile(data.getString("mobile"));
								                		card.setMail(data.getString("mail"));
								                		card.setHomelink(data.getString("homelink"));
								                		card.setBirthday(data.getString("birthday"));
								                		String base64 = data.getString("avatar");
								                		if(base64.equals(""))
								                		{
								                			card.setAvatar("");
								                		}
								                		else
								                		{
								                			Bitmap  bmp =FileUtil.base64ToBitmap(base64);
								                			String path =ImageTools.savePhotoToSDCard(bmp, HomeApp.getMyApplication().getPicPath() ,"card_avatar_"+String.valueOf(System.currentTimeMillis())+".png", 150);
									                		bmp=null;
									                		card.setAvatar(path);
								                		}
								                		CardDBManager dbM = new CardDBManager(getActivity());
								                		dbM.add(card);
								                		dbM.closeDB();
								                		zmq.close();
								                		if(progressDialog.isShowing())
								                		{
								                			progressDialog.dismiss();
								                		}
								                		MyVibrator.doVibration(500);
								                		MyToast.alert("接收完成");
								                		HomeApp.setCardList(null);
								                		initCardList();
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
										mConnection.close();
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
