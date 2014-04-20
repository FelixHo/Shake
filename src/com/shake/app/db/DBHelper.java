package com.shake.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库类
 * @author Felix
 */
public class DBHelper extends SQLiteOpenHelper {  
	  
    private static final String DATABASE_NAME = "shake.db";  
    
    private static final int DATABASE_VERSION = 2;  
      
    public DBHelper(Context context) {  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS "+CardDBManager.TABLE_NAME+  
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR, " +
                "profile VARCHAR, " +
                "mobile VARCHAR, " +
                "mail VARCHAR, " +
                "birthday VARCHAR, " +
                "homelink VARCHAR, " +
                "avatar VARCHAR) ");  
    }  
  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
    	db.execSQL("DROP TABLE IF EXISTS "+CardDBManager.TABLE_NAME);
		this.onCreate(db);
    }  
} 