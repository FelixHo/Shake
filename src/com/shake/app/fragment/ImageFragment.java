package com.shake.app.fragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.shake.app.Define;
import com.shake.app.HomeApp;
import com.shake.app.R;
import com.shake.app.activity.MainActivity;
import com.shake.app.activity.ShowPhotosActivity;
import com.shake.app.adapter.GroupAdapter;
import com.shake.app.model.ImageBean;
import com.shake.app.task.InitImageGroupTask;
import com.shake.app.task.InitImageGroupTask.OnTaskListener;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.ImageTools;
import com.shake.app.utils.LocationTools;
import com.shake.app.utils.LocationTools.MyLocationListener;
import com.shake.app.utils.MyDateUtils;
import com.shake.app.utils.MyJsonCreator;
import com.shake.app.utils.MySharedPreferences;
import com.shake.app.utils.MyToast;
import com.shake.app.utils.MyVibrator;
import com.shake.app.utils.ShakeEventDetector;
import com.shake.app.utils.ShakeEventDetector.OnShakeListener;
import com.shake.app.utils.ZMQConnection;
import com.shake.app.utils.ZMQConnection.ZMQConnectionLisener;

public class ImageFragment extends Fragment {
	

	private InitImageGroupTask task;
	
	private GridView groupGrid;
	
	private View layout; 
	
	private ProgressDialog dialog;
	
	private HashMap<String,List<String>> mGroupMap;
	
	private List<ImageBean> groupList ;
	
	private GroupAdapter adapter;
	
	private Button menuBtn;
	
	private Button connBtn;
	
	private Handler refreshHandelr;
	
	public ImageFragment() {
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		refreshHandelr = new Handler()
		{
			public void handleMessage(Message msg)
			{
				HomeApp.setImageGroupMap(null);
				getPicGroup();
			}
		};
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		layout = inflater.inflate(R.layout.fragment_image, null);
		
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setupView();
		initView();
		getPicGroup();
		
	}

	private void getPicGroup()
	{
		task = new InitImageGroupTask();
		
		task.setOnTaskListener(new OnTaskListener() {
			
			@Override
			public void onTaskStart() {
				if(HomeApp.getImageGroupMap()==null)
				{
					dialog = ProgressDialog.show(getActivity(),null,"正在读取相册...", true,false);
					dialog.show();
				}
				
			}
			
			@Override
			public void onTaskFinished(HashMap<String, List<String>> group) {
				
				if(group!=null)
				{
					mGroupMap = group;
					groupList = getGroupData(mGroupMap);
					adapter.refresh(groupList);	
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
		groupGrid = (GridView)layout.findViewById(R.id.image_frag_gridview);
		menuBtn = (Button)layout.findViewById(R.id.image_frag_menu_button);
		connBtn = (Button)layout.findViewById(R.id.image_frag_connect_button);
	}
	
	private void initView()
	{
		adapter = new GroupAdapter(getActivity(), groupList);
		groupGrid.setAdapter(adapter);
		groupGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				List<String> photoPaths = mGroupMap.get(groupList.get(position).getFolderName());
				Intent intent = new Intent(getActivity(), ShowPhotosActivity.class);
				intent.putStringArrayListExtra(ShowPhotosActivity.SHOW_PHOTO_DATA_KEY, (ArrayList<String>)photoPaths);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.scale_in, android.R.anim.fade_out);
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
									
									String connectREQ = MyJsonCreator.createJsonToServer("3","2",data,null);						
								
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
								                		
								                		String base64_IMG = jso.getString("data");
								                		String pic_name = "IMG_"+MyDateUtils.getCurrentDate(null)+".png";
								                		String pic_path = HomeApp.getMyApplication().getPicPath()+"shake_pic/";
								                		String path =ImageTools.savePhotoToSDCard(FileUtil.base64ToBitmap(base64_IMG),pic_path, pic_name, 1024);
								                		MyVibrator.doVibration(500);
								                		progressDialog.setMessage("接收完成,正在更新相册...");
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
//								                
								                		zmq.close();
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
				/********************************************************************/
				
			}
		});
	}
	
	/**
	 * 获得GroupView的数据源，以文件夹分类的图片册
	 * @param mGroupMap 通过InitImageGroupTask获得的GroupMap
	 * @return 数据源List
	 */
	private List<ImageBean> getGroupData(HashMap<String, List<String>> mGroupMap){
		
		if(mGroupMap==null||mGroupMap.size() == 0){
			
			return null;
		}
		
		List<ImageBean> list = new ArrayList<ImageBean>();
		
		Iterator<Map.Entry<String, List<String>>> it = mGroupMap.entrySet().iterator();
		
		while (it.hasNext()) {
			
			Map.Entry<String, List<String>> entry = it.next();
			
			ImageBean mImageBean = new ImageBean();
			
			String key = entry.getKey();
			
			List<String> value = entry.getValue();
			
			mImageBean.setFolderName(key);
			
			mImageBean.setImageCounts(value.size());
			
			mImageBean.setTopImagePath(value.get(0));//以第一张图为封面
			
			list.add(mImageBean);
		}
		
		return list;		
	}
}
