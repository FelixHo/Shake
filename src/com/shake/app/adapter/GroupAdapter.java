package com.shake.app.adapter;

import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.shake.app.R;
import com.shake.app.model.ImageBean;
import com.shake.app.utils.FileUtil;
import com.shake.app.utils.ImageTools;

/**
 * 分目录相册适配器
 * @author Felix
 */
public class GroupAdapter extends BaseAdapter{
	
	private List<ImageBean> list;
	
	protected LayoutInflater mInflater;
	
	public void refresh(List<ImageBean> newData)
	{
		this.list = newData;
		notifyDataSetChanged();		
	}
	@Override
	public int getCount() {
		
		if(list!=null&&list.size()>0)
		{
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		
		if(list!=null&&list.size()>0)
		{
			return list.get(position);
		}
		
		return null;		
	}


	@Override
	public long getItemId(int position) {
		
		return position;
	}
	
	public GroupAdapter(Context context, List<ImageBean> list){
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final ViewHolder holder;
		
		ImageBean mImageBean = list.get(position);
		
		final String path = mImageBean.getTopImagePath();
		
		if(convertView == null){
			
			holder = new ViewHolder();
			
			convertView = mInflater.inflate(R.layout.grid_group_item, null);
			
			holder.mImageView = (ImageView) convertView.findViewById(R.id.image_frag_grid_group_image);
			
			holder.mFolderName = (TextView) convertView.findViewById(R.id.image_frag_grid_group_title);
			
			holder.mCounts = (TextView) convertView.findViewById(R.id.image_frag_grid_group_count);
				
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
			
		}
		
		holder.mFolderName.setText(mImageBean.getFolderName());
		
		holder.mCounts.setText(Integer.toString(mImageBean.getImageCounts()));
		
		ImageLoader.getInstance().displayImage(FileUtil.getUriStringByPath(path), holder.mImageView,new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {}
			
			@Override
			public void onLoadingFailed(String imageUri, View view,	FailReason failReason) {}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) 
			{
				/**
				 * 调整图片方向
				 */
				Bitmap bp  =ImageTools.getCorrectOrientationBitmap(path, loadedImage);
				((ImageView)view).setImageBitmap(bp);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {}
		});
		
		return convertView;
	}
	
	public static class ViewHolder{
		
		public ImageView mImageView;
		
		public TextView mFolderName;
		
		public TextView mCounts;
	}
}
