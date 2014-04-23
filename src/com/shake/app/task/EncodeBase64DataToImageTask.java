package com.shake.app.task;

import android.os.AsyncTask;

import com.shake.app.utils.FileUtil;
import com.shake.app.utils.ImageTools;

public class EncodeBase64DataToImageTask extends AsyncTask<String, Void, String> {
	public interface onTaskListener
	{
		public void onStart();
		public void onFinish(String imagePath);
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
		String pic_path = params[1];
		String pic_name = params[2];
		return ImageTools.savePhotoToSDCard(FileUtil.base64ToBitmap(base64Data),pic_path, pic_name, 1024);
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		this.listener.onFinish(result);
	}

}
