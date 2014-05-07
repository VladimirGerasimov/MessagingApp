package com.example.messengerapp;

import com.example.messengerapp.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ProfilesAct extends Activity {
	private SystemUiHider mSystemUiHider;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	DataBase sqh;
	SQLiteDatabase sqdb;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profiles);
		ListView l = (ListView)findViewById(R.id.cantactslist);
		mSystemUiHider = SystemUiHider.getInstance(this, findViewById(R.id.fullscreen_content),
				HIDER_FLAGS);
		mSystemUiHider.setup();
		
		sqh = new DataBase(this);
		sqdb = sqh.getWritableDatabase();
		
		String query = "SELECT " + 
				DataBase.NAME + ", " + 
				DataBase.LOGGED_IN + ", _id FROM " + 
				DataBase.TABLE_NAME;
		Cursor cursor2 = sqdb.rawQuery(query, null);
		String [] items = new String[100];
		final int [] ids = new int[100];
		int i = 0;
		while (cursor2.moveToNext()) {
			String name = cursor2.getString(cursor2
					.getColumnIndex(DataBase.NAME));
			ids[i] = cursor2.getInt(cursor2
					.getColumnIndex("_id"));
			
			items[i] = name;
			i++;
		}
		String [] nitems = new String[i];
		for(int k = 0; k < i; k++){
			nitems[k] = items[k];
			
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nitems);
		l.setAdapter(adapter);
		cursor2.close();


		l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
					long id) {
				Intent intent = new Intent(ProfilesAct.this, EditProfile.class);
				intent.putExtra("userid", ids[position]);
				startActivity(intent);
			}
		});
		
		sqdb.close();
		sqh.close();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(1);
	}
	public void activityCreateProfile(View v) {
		Intent intent = new Intent(ProfilesAct.this, NewProf.class);
		startActivity(intent);
	}
	
	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

}
