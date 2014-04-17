package com.shake.app.task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import com.shake.app.HomeApp;

/**
 * 初始化手机内所有图片，按照文件夹分类，封装到一个Group里面，以HashMap存储
 * @author Felix
 *
 */
public class InitImageGroupTask extends AsyncTask<Context, String, HashMap<String,List<String>>> 
{


	public interface OnTaskListener
	{
		public void onTaskStart();
		
		public void onTaskFinished(HashMap<String,List<String>> group);
		
		public void onTaskFail(String reason);
	}
	
	private OnTaskListener listener;

	/**
	 * mGroup <文件夹名称 ,文件夹下所有图片的路径>
	 */
	private HashMap<String, List<String>> mGroupMap = new HashMap<String, List<String>>();
	
	private Context mContext;
	
	private static String[] IMAGE_PROJECTION = {Media.DATA};
	
	private static int IMAGE_DATA_INDEX = 0;
	
	/**
	 * 过滤图片不显示低于30kb的图片
	 */
	private static int MINI_IMAGE_SIZE = 30*1024;
	
	public void setOnTaskListener(OnTaskListener l)
	{
		this.listener = l;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(listener!=null)
		this.listener.onTaskStart();
	}

	@Override
	protected HashMap<String, List<String>> doInBackground(Context... params) {
	
		
		if(HomeApp.getImageGroupMap()!=null)
		{
			mGroupMap = HomeApp.getImageGroupMap();
			
			return mGroupMap;
		}
		
		if(!HomeApp.getMyApplication().isSDCardMounted()){
			
			publishProgress("请插入存储卡!");
			
			return null;
		}
		
		mContext = params[0];
		
		Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		
		ContentResolver mContentResolver = mContext.getContentResolver();

		//只查询jpeg和png的图片
		Cursor mCursor = mContentResolver.query(mImageUri, IMAGE_PROJECTION,
				"("+Media.MIME_TYPE + "=? or "+ Media.MIME_TYPE + "=?"+") and ("+Media.SIZE+" >?)",
				new String[] { "image/jpeg", "image/png",Integer.toString(MINI_IMAGE_SIZE)}, Media.DATE_MODIFIED+" DESC");
		
		while (mCursor.moveToNext()) {
			//获取图片的路径
			String path = mCursor.getString(IMAGE_DATA_INDEX);
			
			//获取该图片的父路径名
			String parentName = new File(path).getParentFile().getName();

			
			//根据父路径名将图片放入到mGruopMap中
			//封装成以文件夹为Key，文件夹下所有图片路径的表为Value的HashMap
			if (!mGroupMap.containsKey(parentName)) {
				
				List<String> chileList = new ArrayList<String>();
				
				chileList.add(path);
				
				mGroupMap.put(parentName, chileList);
				
			} else {
				mGroupMap.get(parentName).add(path);
			}
		}
		
		mCursor.close();
		
		HomeApp.setImageGroupMap(mGroupMap);
		
		return mGroupMap;
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		if(listener!=null)
		this.listener.onTaskFail(values[0]);
	}

	@Override
	protected void onPostExecute(HashMap<String, List<String>> result) {
		super.onPostExecute(result);
		if(listener!=null)
		this.listener.onTaskFinished(mGroupMap);
	}



}
