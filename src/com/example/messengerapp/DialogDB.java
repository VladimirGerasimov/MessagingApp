package com.example.messengerapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DialogDB extends SQLiteOpenHelper {
	  private static final int DB_VERSION = 1;
	  private static final String DB_NAME = "messAcc.db3";

	  public static final String TABLE_NAME = "contacts";
	  public static final String USER = "user";
	  public static final String OWNER = "owner";
	  public static final String DATE_CHANGED = "date_changed";
	  public static final String DATE_CREATED = "date_created";
	  private static final String CREATE_TABLE = 
			  			"create table " + 
					  TABLE_NAME + " ( _id integer primary key autoincrement, " + 
					  USER + " INT, " + 
					  OWNER + " INT, " + 
					  DATE_CHANGED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + 
					  DATE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

	  	  
	  public DialogDB(Context context) {
	    super(context, DB_NAME, null,DB_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase sqLiteDatabase) {
	    sqLiteDatabase.execSQL(CREATE_TABLE);
	  }
	  
	  @Override
	  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
	  }
}
