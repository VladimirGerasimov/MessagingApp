package com.example.messengerapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessagesDB extends SQLiteOpenHelper {
	  private static final int DB_VERSION = 1;
	  private static final String DB_NAME = "messAcc.dba";

	  public static final String TABLE_NAME = "messages";
	  public static final String TEXT = "thetext";
	  public static final String SENDER = "sender";
	  public static final String RECIEVER = "reciever";
	  public static final String OWNER = "owner";
	  public static final String DATE = "date_written";
	  public static final String NEW = "ifnew"; 
	  private static final String CREATE_TABLE = 
			  			"create table " + 
					  TABLE_NAME + " ( _id integer primary key autoincrement, " + 
			  			TEXT + " TEXT, " +
					  SENDER + " INT, " + 
					  RECIEVER + " INT, " + 
					  OWNER + " INT, " + 
					  DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "+
					  NEW + " INT)";

	  	  
	  public MessagesDB(Context context) {
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
