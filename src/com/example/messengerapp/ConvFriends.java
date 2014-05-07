package com.example.messengerapp;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.messengerapp.util.SystemUiHider;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ConvFriends extends Activity {
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	int userid = 0;
	DialogDB sqh;
	SQLiteDatabase sqdb;
	DataBase sqh1;
	SQLiteDatabase sqdb1;
	int [] friends_ids;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conv_friends);
		TextView t = (TextView) findViewById(R.id.fullscreen_content);
		final View contentView = t;

		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		sqh1 = new DataBase(this);
		sqdb1 = sqh1.getWritableDatabase();

		userid = (int) getIntent().getIntExtra("userid", 0);

		String query = "SELECT " + DataBase.FRIENDS
					+ " FROM " + DataBase.TABLE_NAME
					+ " WHERE " + DataBase.ID + "=" + userid;
		Cursor cu = sqdb1.rawQuery(query, null);
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hm;
		String TITLE = "one";
		String DESCRIPTION = "two";
		while (cu.moveToNext()) {
			String fr = cu.getString(cu.getColumnIndex(DataBase.FRIENDS));
			if(fr != null ){
				String [] friends = fr.split("\\[del\\]");
				friends_ids = new int[friends.length];
				for(int i = 0; i < friends.length; i++){
					Log.w("friends=",friends[i]);
					if(friends[i] != null){
						String [] fields = friends[i].split(":");
						// server-id:name:date
						hm = new HashMap<String, Object>();
						hm.put(TITLE, fields[1]); 
						hm.put(DESCRIPTION, "id:"+fields[0]); 
						list.add(hm);
						friends_ids[i] = Integer.valueOf(fields[0]);
					}
				}
			}
		}
		
		final ListView l = (ListView)findViewById(R.id.cantactslist);
		SimpleAdapter adapter = new SimpleAdapter(ConvFriends.this, list,
				R.layout.list_item, new String[] { TITLE, DESCRIPTION},
				new int[] { R.id.text1, R.id.text2 });

		l.setAdapter(adapter);
		l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
					long id) {
				sqh = new DialogDB(ConvFriends.this);
				sqdb = sqh.getWritableDatabase();
				
				String select = "SELECT * FROM " + DialogDB.TABLE_NAME
						+ " WHERE " + DialogDB.OWNER + "=" + userid
						+ " AND " + DialogDB.USER + "=" + friends_ids[position];
				Cursor cu = sqdb.rawQuery(select, null);
				if(cu.getCount() != 0){
					int x = 0;
					while(cu.moveToNext()){
						x = cu.getInt(cu.getColumnIndex("_id"));
					}
					cu.close();
					dialogActivityOpen(x);
					
					///open existing dialog
				} else{
					cu.close();
					Log.e("to see", "test");
					
					String insert = "INSERT INTO " + DialogDB.TABLE_NAME
							+ "(" + DialogDB.OWNER + ", " + DialogDB.USER + ") "
							+ "VALUES (" + userid + ", " + friends_ids[position] +")";
					sqdb.execSQL(insert);
					select = "SELECT _id FROM " + DialogDB.TABLE_NAME
							+ " WHERE " + DialogDB.OWNER + "=" + userid
							+ " AND " + DialogDB.USER + "=" + friends_ids[position];
					cu = sqdb.rawQuery(select, null);
					int x = 0;
					while(cu.moveToNext()){
						x = cu.getInt(cu.getColumnIndex("_id"));
					}
					cu.close();
					sqdb.close();
					sqh.close();
					dialogActivityOpen(x);
					
				}
			}
		});
	}
	
	private void dialogActivityOpen(int id){
		Intent intent = new Intent(ConvFriends.this, TheDialog.class);
		intent.putExtra("userid", userid);
		intent.putExtra("dialog", id);
		startActivity(intent);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(1);
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
