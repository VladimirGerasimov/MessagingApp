package com.example.messengerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.messengerapp.util.SystemUiHider;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity {
	DataBase sqh;
	SQLiteDatabase sqdb;
	ThreadNewData a;
	
	int logged_in_user = -1;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final View contentView = findViewById(R.id.fullscreen_content);
		Button messages = (Button)findViewById(R.id.profiledelete);
		Button contacts = (Button)findViewById(R.id.profileedit);
		sqh = new DataBase(this);
		sqdb = sqh.getWritableDatabase();
		TextView user = (TextView)findViewById(R.id.textView2);
		
		String query = "SELECT " + DataBase.NAME + ", " + DataBase.ID + " FROM " + DataBase.TABLE_NAME + " WHERE " + DataBase.LOGGED_IN + " = 1";
		Cursor cursor = sqdb.rawQuery(query, null);
		if(cursor.getCount() > 0){
			while(cursor.moveToNext()){
				String name = cursor.getString(cursor.getColumnIndex(DataBase.NAME));
				user.setText("Вы вошли как " + name);
				logged_in_user = cursor.getInt(cursor.getColumnIndex(DataBase.ID));
			}
			a = new ThreadNewData(Main.this);
			a.execute(logged_in_user);
		} else {
			messages.setEnabled(false);
			contacts.setEnabled(false);
			
			user.setText("Вы не авторизованы");
		}
		
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(1);
	}
/*	@Override
	protected void onStop(){
		if(a != null){
			a.cancel(false);
		}
	}
*/
	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};
	
	public void openSettings(View v) {
		Intent intent = new Intent(Main.this, Settings.class);
		intent.putExtra("userid", logged_in_user);
		startActivity(intent);
	}

	public void openContact(View v) {
		Intent intent = new Intent(Main.this, Contact.class);
		intent.putExtra("userid", logged_in_user);
		startActivity(intent);
	}

	public void openConv(View v) {
		Intent intent = new Intent(Main.this, Conv.class);
		intent.putExtra("userid", logged_in_user);
		startActivity(intent);
	}
	
	public OnClickListener onUserClick(View v) {
		new MyAlertDialog("ымывм", String.valueOf(logged_in_user), "sdvsdvsd", Main.this);
		if(logged_in_user >= 1000){
			Intent intent = new Intent(Main.this, Contact.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(Main.this, Contact.class);
			startActivity(intent);
		}
		return null;
	}
	
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
