package com.example.messengerapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {
	  private static final int DB_VERSION = 1;
	  private static final String DB_NAME = "messAcc.db2";

	  public static final String TABLE_NAME = "accounts";
	  public static final String NAME = "name";
	  public static final String ID = "id_serv";
	  public static final String FRIENDS = "friends";
	  public static final String LOGGED_IN = "logged_in";
	  private static final String CREATE_TABLE = 
			  			"create table " + 
					  TABLE_NAME + " ( _id integer primary key autoincrement, " + 
					  NAME + " TEXT, " + 
					  FRIENDS + " TEXT, " + 
					  ID + " INT, " + 
					  LOGGED_IN + " INT)";

	  	  
	  public DataBase(Context context) {
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
