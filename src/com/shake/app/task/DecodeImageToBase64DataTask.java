package com.shake.app.task;

import com.shake.app.utils.FileUtil;

import android.os.AsyncTask;

public class DecodeImageToBase64DataTask extends AsyncTask<String,String,String> {

	public interface onTaskListener
	{
		public void onStart();
		public void onFinish(String base64Data);
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
		String path = params[0];
		
		return FileUtil.imageFileToBase64WithTargetSize(path, 256);
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		this.listener.onFinish(result);
	}

}
