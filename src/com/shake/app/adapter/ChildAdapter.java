package com.shake.app.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.shake.app.R;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.ImageTools;
/**
 * 图片详情展示适配器
 * @author Felix
 */
public class ChildAdapter extends BaseAdapter {
	
	private List<String> list;
	
	protected LayoutInflater mInflater;
	
	public ChildAdapter(Context context, List<String> list) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ViewHolder holder;
		
		final String path = list.get(position);
		
		if(convertView == null){
						
			convertView = mInflater.inflate(R.layout.grid_child_item, null);
			
			holder = new ViewHolder();
			
			holder.mImageView = (ImageView) convertView.findViewById(R.id.show_photo_grid_child_image);
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		ImageLoader.getInstance().displayImage(FileUtil.getUriStringByPath(path),holder.mImageView,new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {}
			
			@Override
			public void onLoadingFailed(String imageUri, View view,	FailReason failReason) {}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				
				Bitmap bp  =ImageTools.getCorrectOrientationBitmap(path, loadedImage);
				loadedImage = null;
				((ImageView)view).setImageBitmap(bp);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {}
		});
		
		return convertView;
	}

	
	static class ViewHolder{	 
	 
		public ImageView mImageView;
	}
}
