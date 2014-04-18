package com.shake.app.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.shake.app.R;
import com.shake.app.model.Contact;

	/**
	 * 联系人适配器
	 * @author Felix
	 */
public	class ContactAdapter extends BaseAdapter implements SectionIndexer
{
		private Context context;
		
		private ArrayList<Contact> mContacts = new ArrayList<Contact>();
		
		public ContactAdapter(Context c,ArrayList<Contact> contacts)
		{
			this.context=c;
			this.mContacts = contacts;			
		}
		
		public void setContacts(ArrayList<Contact> datas)
		{
			this.mContacts = datas;
			this.notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			
			int count = 0;
			
			if(mContacts!=null)
			{
				count = mContacts.size();
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			
			if(mContacts.size()>0)
			{
				return mContacts.get(position).getSortKey();
			}
			else
			{
				return '#';
			}
		}
		
		public Contact getCurrentContact(int position)
		{
			return mContacts.get(position);
		}

		@Override
		public long getItemId(int position) {			
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			
			if(convertView ==null)
			{
				holder = new ViewHolder();
				
				LayoutInflater mInflater = LayoutInflater.from(context);
				
				convertView = mInflater.inflate(R.layout.contact_listview_item, null);
				
				holder.avatar = (ImageView)convertView.findViewById(R.id.contact_listview_item_avatar);
				
				holder.name = (TextView)convertView.findViewById(R.id.contact_listview_item_name);
				
				holder.number = (TextView)convertView.findViewById(R.id.contact_listview_item_mobile);
				
				holder.divider =(LinearLayout)convertView.findViewById(R.id.contact_listview_divider_layout);
				
				holder.keyLabel = (TextView)convertView.findViewById(R.id.contact_listview_divider_label);
				
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			if(mContacts.get(position).avatar!=null)
			{
				holder.avatar.setImageBitmap(mContacts.get(position).avatar);
			}
			else
			{
				holder.avatar.setImageResource(R.drawable.contact_noavatar);
			}
			
			holder.name.setText(mContacts.get(position).name);
			
			holder.number.setText(mContacts.get(position).numbers.get(0));
			
			if(position == getPositionForSection(mContacts.get(position).sortKey.charAt(0)))
			{
				holder.keyLabel.setText(mContacts.get(position).sortKey);
				
				holder.divider.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.divider.setVisibility(View.GONE);
			}
			
			return convertView;
		}	
		
		@Override
		public int getPositionForSection(int section) {
			
			if (section == 35) {//# ascii=35

				return 0;
			}
			for (int i = 0; i < mContacts.size(); i++) {

				String l = mContacts.get(i).getSortKey();

				char firstChar = l.toUpperCase().charAt(0);

				if (firstChar == section) {

					return i;

				}
			}
			return -1;
		}
		@Override
		public int getSectionForPosition(int position) {
			
			if(mContacts.size()>0)
			{
				return mContacts.get(position).getSortKey().charAt(0);
			}
			else
			{
				return '#';
			}
			
		}
		@Override
		public Object[] getSections() {
			
			return null;
		}
		
		/**
		 * ViewHolder pattern
		 * @author Felix
		 *
		 */
		private static class ViewHolder
	    {
	        ImageView avatar;
	        
	        TextView name;
	        
	        TextView number;
	        
	        LinearLayout divider;
	        
	        TextView keyLabel;
	    }
}
	
	