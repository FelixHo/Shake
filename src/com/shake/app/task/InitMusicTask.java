package com.shake.app.task;

import java.util.ArrayList;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import com.shake.app.HomeApp;
import com.shake.app.model.Song;

/**
 * 读取手机所有音乐文件
 * @author Felix
 *
 */
public class InitMusicTask extends AsyncTask<Context, String, ArrayList<Song>> {

	private boolean isNewData = false;
	
	public interface OnTaskListener
	{
		public void onTaskStart();
		
		public void onTaskFinished(ArrayList<Song> songs);
		
		public void onTaskFail(String reason);
	}
	
	private OnTaskListener listener;
	
	private Context mContext;
	
	private ArrayList<Song> mSongs = new ArrayList<Song>();
	
	private final static String[] PROJECTION = {Media.ARTIST,Media.TITLE,Media.DURATION,Media.DATA,Media.SIZE};
	
	private final static int SONG_ARTIST_INDEX = 0;
	
	private final static int SONG_NAME_INDEX = 1;
	
	private final static int SONG_TIME_INDEX = 2;
	
	private final static int SONG_URL_INDEX = 3;
	
	private final static int SONG_SIZE_INDEX = 4;	
	
	private final static String SELECTION = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "+ MediaStore.Audio.Media.DURATION+" > 15000";
	
	private final static String ORDER = Media.DEFAULT_SORT_ORDER;
	
	public void setOnTaskListener (OnTaskListener l)
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
	protected ArrayList<Song> doInBackground(Context... params) {
		
		if(HomeApp.getSongList()!=null)
		{
			mSongs = HomeApp.getSongList();
			
			return mSongs;
		}
		if(!HomeApp.getMyApplication().isSDCardMounted()){
			
			publishProgress("请插入存储卡!");
			
			return null;
		}
		
		mContext = params[0];
		
		Uri mSongUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		
		ContentResolver mContentResolver = mContext.getContentResolver();
		
		Cursor mCursor = mContentResolver.query(mSongUri, PROJECTION, SELECTION, null, ORDER);
		
		while(mCursor.moveToNext())
		{
			Song song =  new Song();
			//过滤 "<"标签,常见于<unknown> <undefined>等
			if((mCursor.getString(SONG_ARTIST_INDEX).charAt(0)+"").equals("<"))
			{
				song.setArtist("");
			}
			else
			{
				song.setArtist(mCursor.getString(SONG_ARTIST_INDEX));
			}
			song.setName(mCursor.getString(SONG_NAME_INDEX));
			song.setSize(mCursor.getString(SONG_SIZE_INDEX));
			song.setTime(toTime(Integer.valueOf(mCursor.getString(SONG_TIME_INDEX))));
			song.setUrl(mCursor.getString(SONG_URL_INDEX));
			
			mSongs.add(song);
		}
		mCursor.close();
		
		HomeApp.setSongList(mSongs);
		isNewData = true;
		
		return mSongs;
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		if(listener!=null)
		this.listener.onTaskFail(values[0]);
	}
	
	@Override
	protected void onPostExecute(ArrayList<Song> result) {
		super.onPostExecute(result);
		
		if(isNewData)//这里延迟仅仅为了使画面更顺畅
		{
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if(listener!=null)
					listener.onTaskFinished(mSongs);
				}
			}, 500);
		}
		else
		{
			if(listener!=null)
			listener.onTaskFinished(result);
		}
	}
	
	/**
	 * 时间格式转换
	 * 
	 * @param time xx:xx
	 * @return
	 */
	public String toTime(int time) {
		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}
}
