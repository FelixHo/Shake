package com.shake.app.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMsg;
import org.zeromq.ZMQ.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.shake.app.Define;
import com.shake.app.R;
import com.shake.app.adapter.ChildAdapter;
import com.shake.app.task.DecodeImageToBase64DataTask;
import com.shake.app.task.DecodeImageToBase64DataTask.onTaskListener;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.ImageTools;
import com.shake.app.utils.LocationTools;
import com.shake.app.utils.MyJsonCreator;
import com.shake.app.utils.MySharedPreferences;
import com.shake.app.utils.MyToast;
import com.shake.app.utils.MyVibrator;
import com.shake.app.utils.ShakeEventDetector;
import com.shake.app.utils.ViewUtil;
import com.shake.app.utils.ZMQConnection;
import com.shake.app.utils.LocationTools.MyLocationListener;
import com.shake.app.utils.ShakeEventDetector.OnShakeListener;
import com.shake.app.utils.ZMQConnection.ZMQConnectionLisener;

public class ShowPhotosActivity extends Activity {

	/**
	 * Intent传递参数KEY
	 */
	public final static String SHOW_PHOTO_DATA_KEY = "SHOW_PHOTO_DATA_KEY";
	
	private List<String> photoPaths;
	
	private GridView grid;
	
	private ChildAdapter adapter;
	
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_photo);
		photoPaths = getIntent().getStringArrayListExtra(SHOW_PHOTO_DATA_KEY);
		this.mContext = this;
		setupView();		
		initView();
		
	}
	
	private void setupView()
	{
		grid = (GridView)findViewById(R.id.show_photo_child_gridview);
	}
	
	private void initView()
	{
		adapter = new ChildAdapter(this, photoPaths);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, final int position,long id) {
				
				ImageView v = (ImageView)view.findViewById(R.id.show_photo_grid_child_image);
				Drawable icon =ImageTools.resizeDrawable(mContext, v.getDrawable(),ViewUtil.dp(60),ViewUtil.dp(60));
				new AlertDialog.Builder(mContext)
				.setTitle(FileUtil.getFileName((String)adapter.getItem(position)))
				.setIcon(icon)
				.setItems(new String[] {"分享给朋友","查看大图"},
				 new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,	int which) {
					
					dialog.dismiss();
					
					switch(which)
					{
					case 0:
						{
							final ProgressDialog progressDialog = new ProgressDialog(mContext);
							final ProgressDialog loadingDialog = new ProgressDialog(mContext);
							progressDialog.setCancelable(false);
							
							MyToast.alert("请与要接收的手机进行一次轻碰");
							
							ShakeEventDetector.start(new OnShakeListener() {
								
								@Override
								public void onShake() {
									progressDialog.setMessage("正在建立连接...");
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
												jso.put("name",MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_NAME_KEY, false));
												jso.put("lat", location[0]);//纬度
												jso.put("lon",location[1]);//经度
												String data = jso.toString();
												
												String connectREQ = MyJsonCreator.createJsonToServer("3","1",data,null);						
											
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
											                			final String target = new JSONObject(jso.getString("data")).getString("id");
											                			if(progressDialog.isShowing())
												                		{
												                			progressDialog.dismiss();
												                		}
											                			
											                			final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
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
																			
																				DecodeImageToBase64DataTask dtask = new DecodeImageToBase64DataTask();
																				dtask.setOnTaskListener(new onTaskListener() {
																					
																					@Override
																					public void onStart() {
																						
																						progressDialog.setMessage("正在读取图片...");
																						progressDialog.show();
																					}
																					
																					@Override
																					public void onFinish(String base64Data) {
																						progressDialog.dismiss();
																						loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
																						loadingDialog.setIndeterminate(true);
																						loadingDialog.setProgressNumberFormat(null);
																						loadingDialog.setProgressPercentFormat(null);
																						loadingDialog.setMessage("正在发送...");
																						loadingDialog.show();
																						final String sendDataREQ = MyJsonCreator.createJsonToServer("3","3",base64Data,target);
																						zmq.send(sendDataREQ, false);
																						zmq.setTimeout(30*1000);
																						
																					}
																				});
																				dtask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String)adapter.getItem(position));
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
											                		if(loadingDialog.isShowing())
											                		{
											                			loadingDialog.dismiss();
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
													if(loadingDialog.isShowing())
							                		{
							                			loadingDialog.dismiss();
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
							break;
						}
					case 1:
						{
							Intent intent = new Intent(mContext,CheckPhotoActivity.class);
							intent.putExtra(CheckPhotoActivity.IMAGE_PATH, (String)adapter.getItem(position));
							mContext.startActivity(intent);
							((Activity)mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						}
							
					}
									
				}
				}).setNegativeButton("取消",null).show();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(0,R.anim.scale_out);
	}	
	
}
