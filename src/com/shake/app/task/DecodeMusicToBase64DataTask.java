package com.shake.app.task;

import android.os.AsyncTask;

import com.shake.app.model.Song;

public class DecodeMusicToBase64DataTask extends AsyncTask<Song, Void, String> {

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
	protected String doInBackground(Song... params) {
		Song song = params[0];
		
		return song.getJSonString();
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		this.listener.onFinish(result);
	}

}
