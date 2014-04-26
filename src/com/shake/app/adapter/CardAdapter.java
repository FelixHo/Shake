package com.shake.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shake.app.R;
import com.shake.app.model.Card;
import com.shake.app.utils.FileUtil;

/**
 * 名片目录适配器
 * @author Felix
 */
public class CardAdapter extends BaseAdapter {

	private Context mContext;
	
	private ArrayList<Card> mCards;
	
	private LayoutInflater mInflater;
	
	public CardAdapter(Context c ,ArrayList<Card> cards)
	{
		this.mCards = cards;
		this.mContext = c;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	public void refreshData(ArrayList<Card> newData)
	{
		this.mCards = newData;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		
		return mCards.size();
	}

	@Override
	public Object getItem(int position) {
		
		return mCards.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Card card = mCards.get(position);
		if(convertView==null)
		{
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.card_listview_item,null);
			holder.avatar = (ImageView)convertView.findViewById(R.id.card_listview_item_avatar);
			holder.name = (TextView)convertView.findViewById(R.id.card_listview_item_name);
			holder.profile = (TextView)convertView.findViewById(R.id.card_listview_item_profile);
			
			convertView.setTag(holder);
		}
		else
		{
			holder= (ViewHolder)convertView.getTag();
		}
		
		if(card.getAvatar()==null||card.getAvatar().equals(""))
		{
			holder.avatar.setImageResource(R.drawable.card_noavatar);
		}
		else
		{
//			try {
//				String base64 = new JSONObject(card.getJsonString()).getString("avatar");
//				
//				holder.avatar.setImageBitmap(FileUtil.base64ToBitmap(base64));
//			} catch (JSONException e) {
//				// TODO 自动生成的 catch 块
//				e.printStackTrace();
//			}
			ImageLoader.getInstance().displayImage(FileUtil.getUriStringByPath(card.getAvatar()), holder.avatar);
		}
		holder.name.setText(card.getName());
		holder.profile.setText(card.getProfile());
		holder.profile.setSelected(true);
		holder.name.setSelected(true);
		
		return convertView;
	}

	private static class ViewHolder
	{
		ImageView avatar;
		TextView name;
		TextView profile;
	}

}
