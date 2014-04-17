package com.shake.app.task;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

import com.shake.app.HomeApp;
import com.shake.app.model.Contact;

public class InitContactsTask extends AsyncTask<Context, Void, ArrayList<Contact>> {
	
	/**获取库Phone表字段**/  
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME,Photo.PHOTO_ID,Phone.CONTACT_ID,Phone.SORT_KEY_PRIMARY};  
    
    /**排序key{汉字按照拼音进行}**/
    private static final String SORT_KEY = "sort_key";
     
    /**联系人显示名称**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    
    /**头像ID**/  
    private static final int PHONES_PHOTO_ID_INDEX = 1;  
     
    /**联系人的ID**/  
    private static final int PHONES_CONTACT_ID_INDEX = 2;  
    
    /**联系SORT_KEY**/
    private static final int PHONES_SORT_KEY_ID_INDEX = 3;

	public onTaskListener fineshedListener = null;
	
	private ArrayList<Contact> mContacts = new ArrayList<Contact>();
	
	private Context mContext  = null;
	
	public interface onTaskListener
	{
		public void onTaskStart();
		
		public void onTaskFinished(ArrayList<Contact> list);
		
	}
	
	/**
	 * 任务完成监听
	 * @param listener
	 */
	public void setOnTaskListener( onTaskListener listener)
	{
		this.fineshedListener = listener;
	}
	
	
	/**
	 * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
	 * 
	 * @param sortKeyString
	 *            数据库中读取出的sort key
	 * @return 英文字母或者#
	 */
	private String getSortKey(String sortKeyString) {
		
		String key = sortKeyString.substring(0, 1).toUpperCase();
		
		if (key.matches("[A-Z]")) {
			
			return key;
		}
		return "#";
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.fineshedListener.onTaskStart();
	}


	/** 得到手机通讯录联系人信息 **/
	@Override
	protected ArrayList<Contact> doInBackground(Context... params) {
		
		mContext = params[0];
		
			if(HomeApp.getContactsList()!=null)
			{
				mContacts = HomeApp.getContactsList();
				
				return mContacts;
			}
			ContentResolver resolver = mContext.getContentResolver();

			// 获取手机联系人
			Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null,SORT_KEY);

			if (phoneCursor != null) {
				while (phoneCursor.moveToNext()) {

					// 得到联系人名称
					String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
					
					if (TextUtils.isEmpty(contactName))
						continue;			

					// 得到联系人ID
					Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

					// 得到联系人头像ID
					Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
					
					//得到sortkey
					
					String sortKey = phoneCursor.getString(PHONES_SORT_KEY_ID_INDEX);

					// 得到联系人头像Bitamp
					Bitmap contactPhoto = null;

					// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
					if (photoid > 0) {
						
						Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
						
						InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
						
						contactPhoto = BitmapFactory.decodeStream(input);
					} 
					else 
					{
						contactPhoto = null;
					}
					
					/**
					 * 找出该联系人所有的电话号码
					 */
					ArrayList<String> phoneNumbers = null;
					
					String[] projection = {Phone.NUMBER};
					
					Cursor phones = resolver.query(Phone.CONTENT_URI,projection, Phone.CONTACT_ID+"="+contactid, null,null);
					
					if(phones!=null)
					{
						phoneNumbers = new ArrayList<String>();
						
						while(phones.moveToNext())
						{
							phoneNumbers.add(phones.getString(0));
						}
					}
					
					phones.close();
					
					Contact contact = new Contact();
					
					contact.name = contactName;
					
					contact.numbers = phoneNumbers;
					
					contact.avatar = contactPhoto;
					
					contact.sortKey = getSortKey(sortKey);
					
					mContacts.add(contact);
				}
				
				phoneCursor.close();
				
				HomeApp.setContactsList(mContacts);
			}
			
		return mContacts;
	}

	@Override
	protected void onPostExecute(ArrayList<Contact> result) {
		super.onPostExecute(result);
		this.fineshedListener.onTaskFinished(result);
	}


}
