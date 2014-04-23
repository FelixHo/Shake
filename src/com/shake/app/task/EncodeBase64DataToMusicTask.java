package com.shake.app.task;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.shake.app.HomeApp;
import com.shake.app.utils.FileUtil;

public class EncodeBase64DataToMusicTask extends AsyncTask<String, Void, String> {
	public interface onTaskListener
	{
		public void onStart();
		public void onFinish(String musicPath);
	}
	private onTaskListener listener = null;
	
	public void setOnTaskListener (onTaskListener l)
	{
		this.listener = l;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.listener.onStart();
	}

	@Override
	protected String doInBackground(String... params) {
		String base64Data = params[0];
		String path = params[1];
		
		try 
		{
			FileUtil.decoderBase64File(base64Data, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ContentValues values = new ContentValues();  
		 values.put(MediaStore.MediaColumns.DATA, path);  
		 values.put(MediaStore.MediaColumns.TITLE, "exampletitle");  
		 values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");  
		 values.put(MediaStore.Audio.Media.ARTIST, "cssounds ");  
		 values.put(MediaStore.Audio.Media.IS_RINGTONE, true);  
		 values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);  
		 values.put(MediaStore.Audio.Media.IS_ALARM, true);  
		 values.put(MediaStore.Audio.Media.IS_MUSIC, true); 
		 HomeApp.getMyApplication().getApplicationContext().getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(path), values);
		
		 return path;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		this.listener.onFinish(result);
	}


}
