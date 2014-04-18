package com.shake.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.shake.app.R;
import com.shake.app.model.Song;

/**
 * 音乐列表适配器
 * @author Felix
 *
 */
public class MusicAdapter extends BaseAdapter {

	private Context mContext;
	
	private ArrayList<Song> mSongs;
	
	private LayoutInflater mInflater;

	public MusicAdapter (Context c,ArrayList<Song> songs)
	{
		this.mContext = c;
		this.mSongs = songs;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	public void refreshData(ArrayList<Song> data)
	{
		this.mSongs = data;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		
		if(mSongs!=null&&mSongs.size()>0)
		{
			return mSongs.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		
		if(mSongs!=null && mSongs.size()>0)
		{
			return mSongs.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		ViewHolder holder = null;
		final Song song = mSongs.get(position);
		if(convertView==null)
		{
			holder =  new ViewHolder();
			convertView = mInflater.inflate(R.layout.music_listview_item,null);
			holder.name = (TextView)convertView.findViewById(R.id.music_listview_item_name);
			holder.artist = (TextView)convertView.findViewById(R.id.music_listviw_item_artist);
			holder.time = (TextView)convertView.findViewById(R.id.music_listview_item_time);
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.name.setText(song.getName());
		holder.artist.setText(song.getArtist());
		holder.time.setText(song.getTime());
		holder.name.setSelected(true);
		holder.artist.setSelected(true);
		
		return convertView;
	}
	
	private static class ViewHolder
	{
		TextView name;
		TextView artist;
		TextView time;
	}

}
