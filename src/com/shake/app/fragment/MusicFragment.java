package com.shake.app.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.shake.app.Define;
import com.shake.app.HomeApp;
import com.shake.app.R;
import com.shake.app.activity.MainActivity;
import com.shake.app.adapter.MusicAdapter;
import com.shake.app.model.Song;
import com.shake.app.task.InitMusicTask;
import com.shake.app.task.InitMusicTask.OnTaskListener;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.LocationTools;
import com.shake.app.utils.LocationTools.MyLocationListener;
import com.shake.app.utils.MyJsonCreator;
import com.shake.app.utils.MySharedPreferences;
import com.shake.app.utils.MyToast;
import com.shake.app.utils.MyVibrator;
import com.shake.app.utils.ShakeEventDetector;
import com.shake.app.utils.ShakeEventDetector.OnShakeListener;
import com.shake.app.utils.ZMQConnection;
import com.shake.app.utils.ZMQConnection.ZMQConnectionLisener;

/**
 * 我的音乐
 * @author Felix
 */
public class MusicFragment extends Fragment {
	

	private View layout;
	
	private InitMusicTask task;
	
	private ProgressDialog dialog;
	
	private ArrayList<Song> mSongs;
	
	private ListView listView;
	
	private MusicAdapter adapter;
	
	private MediaPlayer player ;
	
	private Button menuBtn;
	
	private Button connBtn;
	
	private Handler refreshHandelr;
	
	public MusicFragment() {
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		refreshHandelr = new Handler()
		{
			public void handleMessage(Message msg)
			{
				HomeApp.setSongList(null);
				getMusic();
			}
		};
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.fragment_music, container, false);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setupView();
		initView();
		getMusic();
		
	}	
	
	private void getMusic()
	{
		task = new InitMusicTask();
		
		task.setOnTaskListener(new OnTaskListener() {
			
			@Override
			public void onTaskStart() {
				if(HomeApp.getSongList()==null)
				{
					dialog = ProgressDialog.show(getActivity(),null,"正在读取音乐库...", true,false);
					dialog.show();
				}
			}
			
			@Override
			public void onTaskFinished(ArrayList<Song> songs) {
				
				if(songs!=null)
				{
					mSongs = songs;
					adapter.refreshData(mSongs);
				}
				
				if(dialog!=null&&dialog.isShowing())
				{
					dialog.dismiss();
				}
			}
			
			@Override
			public void onTaskFail(String reason) {
				MyToast.alert(reason);
			}
		});		
		
		task.execute(getActivity());
	}
	private void setupView()
	{
		listView = (ListView)layout.findViewById(R.id.music_frag_listview);
		menuBtn = (Button)layout.findViewById(R.id.music_frag_menu_button);
		connBtn = (Button)layout.findViewById(R.id.music_frag_connect_button);
	}
	
	private void initView()
	{
		adapter = new MusicAdapter(getActivity(), mSongs);
		listView.setAdapter(adapter);	
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {

				final Song song = (Song) adapter.getItem(position);
					String size = FileUtil.fileLength(Long.valueOf(song.getSize()));
					
					final AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setTitle(song.getName())
					.setIcon(android.R.drawable.ic_media_play)				
					.setPositiveButton("分享给朋友 ("+size+")", null)
					.setNeutralButton("试听",null)
					.show();
					dialog.setCanceledOnTouchOutside(true);
					dialog.setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface dialog) {
							if(player!=null && player.isPlaying())
							{
								player.stop();
							}
							if(player!=null)
							{
								player.release();
								player = null;
							}
						}
					});
					
					final Button pos = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
					final Button neu = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
					
					neu.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(player!=null && player.isPlaying())
							{
								neu.setText("试听");
								dialog.setTitle(song.getName());
								player.stop();
								player.release();
								player = null;
							}
							else
							{
								player = new MediaPlayer();
								try 
								{
									player.setDataSource(song.getUrl());
									player.prepare();
									player.start();
									neu.setText("停止");
									dialog.setTitle("正在播放..."+song.getName());
									
								} catch (IllegalStateException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
								
							}
						}
					});
					
					pos.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							/*****************************************************/
							if(dialog.isShowing())
							{
								dialog.dismiss();
							}
							
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
												jso.put("name",MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_NAME_KEY, false));
												jso.put("lat", location[0]);//纬度
												jso.put("lon",location[1]);//经度
												String data = jso.toString();
												
												String connectREQ = MyJsonCreator.createJsonToServer("4","1",data,null);						
											
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
																			
																				String data = song.getJSonString();
																				String sendDataREQ = MyJsonCreator.createJsonToServer("4","3",data,target);
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
							
							/***********************************************************/
						}
					});
			}
		});
		menuBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				((MainActivity)getActivity()).toggleMenu();
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
									jso.put("name",MySharedPreferences.getShared(Define.CONFINFO, Define.USER_INFO_NAME_KEY, false));
									jso.put("lat", location[0]);//纬度
									jso.put("lon",location[1]);//经度
									String data = jso.toString();
									
									String connectREQ = MyJsonCreator.createJsonToServer("4","2",data,null);						
								
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
								                		String name = data.getString("name");
								                		String base64 =data.getString("data");
								                		String path = HomeApp.getMyApplication().getMusicPath()+name;
								                		
								                		FileUtil.decoderBase64File(base64, path);
								                		getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+path)));  
								                		 Log.d("ZMQTask","音乐路径:"+"file://"+path);
								                		ContentValues values = new ContentValues();  
								                		 values.put(MediaStore.MediaColumns.DATA, path);  
								                		 values.put(MediaStore.MediaColumns.TITLE, "exampletitle");  
								                		 values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");  
								                		 values.put(MediaStore.Audio.Media.ARTIST, "cssounds ");  
								                		 values.put(MediaStore.Audio.Media.IS_RINGTONE, true);  
								                		 values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);  
								                		 values.put(MediaStore.Audio.Media.IS_ALARM, true);  
								                		 values.put(MediaStore.Audio.Media.IS_MUSIC, true); 
								                		 getActivity().getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(path), values);
								                		 MyVibrator.doVibration(500);
								                		 progressDialog.setMessage("接收完成,正在更新音乐库...");
								                		 MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null, new OnScanCompletedListener() {
															
															@Override
															public void onScanCompleted(String path, Uri uri) {
																
																if(progressDialog.isShowing())
										                		{
										                			progressDialog.dismiss();
										                		}
																refreshHandelr.sendEmptyMessage(0);
																
															}
														});
								                		 zmq.close();
								                		
								                		
								                		getMusic();
								                		break;
								                		
								                	}
								                }
											}
							                catch( JSONException e)
							                {
							                	e.printStackTrace();
							                } catch (Exception e) {
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
				/********************************************************************/
				
			}
		});
	}
}
