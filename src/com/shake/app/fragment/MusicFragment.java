package com.shake.app.fragment;

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.shake.app.HomeApp;
import com.shake.app.R;
import com.shake.app.activity.MainActivity;
import com.shake.app.adapter.MusicAdapter;
import com.shake.app.model.Song;
import com.shake.app.task.InitMusicTask;
import com.shake.app.task.InitMusicTask.OnTaskListener;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.MyToast;

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
	
	public MusicFragment() {
		
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
							MyToast.alert("Coming soon...");
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
	}
}
