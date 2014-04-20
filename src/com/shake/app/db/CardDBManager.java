package com.shake.app.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shake.app.model.Card;

/**
 * 名片录数据库操作类
 * @author Felix
 *
 */
public class CardDBManager {

	private DBHelper helper;
	
	private SQLiteDatabase db;
	
	public final static String TABLE_NAME = "cards";
	
	public CardDBManager (Context context)
	{
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}
	
	/**
	 * 往数据库cards插入一张新的名片信息
	 */
	public long add(Card card)
	{
		ContentValues cv = new ContentValues();
		cv.put("name", card.name);
		cv.put("profile", card.profile);
		cv.put("mobile",card.mobile);
		cv.put("mail",card.mail);
		cv.put("birthday",card.birthday);
		cv.put("homelink",card.homelink);
		cv.put("avatar", card.avatar);
		return db.insert(TABLE_NAME, null, cv);		
	}
	
	/**
	 * 查出cards数据表中的所有名片信息
	 * @return
	 */
	public ArrayList<Card> queryAll()
	{
		String sql = "SELECT * FROM "+TABLE_NAME+" ORDER BY _id DESC";
		ArrayList<Card> result = new ArrayList<Card>();		
		Cursor c = db.rawQuery(sql,null);
		while (c.moveToNext()) 
		{  
            Card card = new Card();  
            card.name = c.getString(c.getColumnIndex("name"));
            card.profile = c.getString(c.getColumnIndex("profile"));
            card.mobile = c.getString(c.getColumnIndex("mobile"));
            card.mail = c.getString(c.getColumnIndex("mail"));
            card.birthday = c.getString(c.getColumnIndex("birthday"));
            card.homelink = c.getString(c.getColumnIndex("homelink"));
            card.avatar = c.getString(c.getColumnIndex("avatar"));
            result.add(card);
        }  
        c.close();  
		return result;
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDB()
	{
		db.close();
	}
	
	/**
	 * 删除数据表所有记录
	 */
	public void clearAll()
	{
		db.delete(TABLE_NAME, null, null);
	}

}
