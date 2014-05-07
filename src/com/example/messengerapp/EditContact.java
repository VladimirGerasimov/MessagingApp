package com.example.messengerapp;

import com.example.messengerapp.util.SystemUiHider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
public class EditContact extends Activity {
	DataBase sqh;
	SQLiteDatabase sqdb;
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	private int userid;
	private int contactid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userid = (int) getIntent().getIntExtra("userid", -1);
		String contact;
		if (savedInstanceState == null) {
		    Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		        contact= null;
		    } else {
		        contact= extras.getString("thecontact");
		    }
		} else {
			contact= (String) savedInstanceState.getSerializable("thecontact");
		}
		
		Log.w("contact", contact);
		setContentView(R.layout.activity_edit_contact);
		
		TextView t = (TextView) findViewById(R.id.fullscreen_content);
		
		String ar[] = contact.split(",");
		ar[0] = ar[0].replace("{two=id:", "");
		ar[0] = ar[0].replace("}", "");
		contactid = Integer.valueOf(ar[0]);
		
		ar[1] = ar[1].replace(" one=", "");
		ar[1] = ar[1].replace("}", "");
		contact = ar[1];
		
		t.setText(contact);
		mSystemUiHider = SystemUiHider.getInstance(this, t,
				HIDER_FLAGS);
		mSystemUiHider.setup();
	
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(1);
	}
	public void openDialog(View v){
		
	}
	public void deleteRequest(final View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(EditContact.this);
		builder.setTitle("Удаление")
				.setMessage("Действительно удалить пользователя?")
				.setIcon(R.drawable.ic_launcher)
				.setCancelable(false)
				.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				.setPositiveButton("Удалить",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Server server = new Server();
								String request = getString(R.string.server_adr)
										+"?do=deletecontact&owner="+userid+"&contactid="+contactid;
								server.execute(request);
								try{
									String response = server.get();
									if(response.equals("0")){
										sqh = new DataBase(EditContact.this);
										sqdb = sqh.getWritableDatabase();
										String newFriendString = "";
										String select = "SELECT " + DataBase.FRIENDS 
												+ " FROM " + DataBase.TABLE_NAME 
												+ " WHERE " + DataBase.ID
												+ "=" + userid;
										Cursor cur = sqdb.rawQuery(select, null);
										while(cur.moveToNext()){
											newFriendString = cur.getString(cur.getColumnIndex(DataBase.FRIENDS));
										}
										cur.close();
										String generated = "";
										if(newFriendString != null){
											String [] friendsArray = newFriendString.split("\\[del\\]");
											for(int i = 0; i < friendsArray.length; i++){
												if(friendsArray[i] != null){
													String [] a = friendsArray[i].split(":");
													if(Integer.valueOf(a[0]) != contactid ){
														generated += a[0] + ":" + a[1] + "[del]";
													}
												}
											}
											String query = "UPDATE " + DataBase.TABLE_NAME
													+ " SET " + DataBase.FRIENDS 
													+ "='" + generated
													+ "' WHERE " + DataBase.ID
													+ "=" + userid;
											sqdb.execSQL(query);
											sqdb.close();
											sqh.close();
											getBack(v);
										}
									} else {
										new MyAlertDialog("Внимание!", "Произошла ошибка! Не удалось удалить.","Продолжить", EditContact.this);
									}
								} catch(Exception e){
									
								}
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void getBack(View v) {
		Intent intent = new Intent(EditContact.this, Contact.class);
		intent.putExtra("userid", userid);
		startActivity(intent);
	}
	
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
