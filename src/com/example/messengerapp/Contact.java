package com.example.messengerapp;

import com.example.messengerapp.util.SystemUiHider;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Contact extends Activity {
	int userid = -1;
	DataBase sqh;
	SQLiteDatabase sqdb;
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contact);

		userid = (int) getIntent().getIntExtra("userid", -1);
		sqh = new DataBase(this);
		sqdb = sqh.getWritableDatabase();
		  
		String query = "SELECT " + DataBase.FRIENDS 
				+ " FROM " + DataBase.TABLE_NAME 
				+ " WHERE " + DataBase.ID 
				+ " = " + userid;
		Cursor cursor2 = sqdb.rawQuery(query, null);
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hm;
		String TITLE = "one";
		String DESCRIPTION = "two";
		while (cursor2.moveToNext()) {
			String fr = cursor2.getString(cursor2
					.getColumnIndex(DataBase.FRIENDS));
			if(fr != null ){
				Log.w("fr=",fr);
				String [] friends = fr.split("\\[del\\]");
				for(int i = 0; i < friends.length; i++){
					Log.w("friends=",friends[i]);
					
					if(friends[i] != null){
						String [] fields = friends[i].split(":");
						// server-id:name:date
						hm = new HashMap<String, Object>();
						hm.put(TITLE, fields[1]); 
						hm.put(DESCRIPTION, "id:"+fields[0]); 
						list.add(hm);
					}
				}
			}
		}
		
		final ListView l = (ListView)findViewById(R.id.cantactslist);
		SimpleAdapter adapter = new SimpleAdapter(Contact.this, list,
				R.layout.list_item, new String[] { TITLE, DESCRIPTION},
				new int[] { R.id.text1, R.id.text2 });

		l.setAdapter(adapter);
		l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
					long id) {
				Intent intent = new Intent(Contact.this, EditContact.class);
				intent.putExtra("userid", userid);
				intent.putExtra("thecontact", l.getAdapter().getItem(position).toString());
				startActivity(intent);
			}
		});
		cursor2.close();
		sqdb.close();
		sqh.close();
        final View contentView = findViewById(R.id.fullscreen_content);
		
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(1);
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
	
	public void findfriend(final View v){
		final EditText a = new EditText(Contact.this);
		AlertDialog.Builder builder = new AlertDialog.Builder(Contact.this);
		builder.setTitle("Поиск по имени")
				.setView(a)
				.setIcon(R.drawable.ic_launcher)
				.setCancelable(false)
				.setPositiveButton("Искать",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String query = a.getText().toString();
								String addr = getString(R.string.server_adr)
										+"?do=search&name="
										+ query
										+ "&user=" 
										+ userid;
								Server serv = new Server();
								serv.execute(addr);
								try{
									String response = serv.get();
									if(response.length() > 0){
										String [] pairs = response.split(":");
										if(pairs[0].equals("0")){
											final String [] names = new String[pairs.length-1];
											final int [] ids = new int[pairs.length-1];
											
											String [] temp = new String[3];
											
											int i = 0;

											for(String key : pairs){
												if(!key.equals("0")){
													temp = key.split("\\$");
													names[i] = temp[1];
													ids[i] = Integer.valueOf(temp[0]);
													i++;
												}
											}
												
											ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
											HashMap<String, Object> hm;
											String TITLE = "one";
											String DESCRIPTION = "two";
											for(int k = 0; k < i; k++){
												hm = new HashMap<String, Object>();
												hm.put(TITLE, names[k]); 
												hm.put(DESCRIPTION, "id:"+ids[k]); 
												list.add(hm);
											}
											ListView l = (ListView)findViewById(R.id.cantactslist);
											SimpleAdapter adapter = new SimpleAdapter(Contact.this, list,
													R.layout.list_item, new String[] { TITLE, DESCRIPTION},
													new int[] { R.id.text1, R.id.text2 });

											l.setAdapter(adapter);
											l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
												@Override
												public void onItemClick(AdapterView<?> parent, View itemClicked, final int position,
														long id) {
														AlertDialog.Builder builder = new AlertDialog.Builder(Contact.this);
														builder.setTitle("Подтвердите действие")
																.setMessage("Добавить пользователя?")
																.setIcon(R.drawable.ic_launcher)
																.setCancelable(false)
																.setPositiveButton("Добавить", 
																		new DialogInterface.OnClickListener() {
																			public void onClick(DialogInterface dialog, int id) {
																				///
																				int number = ids[position];
																				
																				Server s = new Server();
																				String addr = getString(R.string.server_adr)
																						+"?do=adduser&id="
																						+ number
																						+ "&user=" 
																						+ userid;
																				s.execute(addr);
																				try{
																					String response = s.get();
																					if(response.equals("0")){
																						sqh = new DataBase(Contact.this);
																						sqdb = sqh.getWritableDatabase();
																						//SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
																						//String date = sdf.format(new Date());
																						String select = "SELECT " + DataBase.FRIENDS 
																								+ " FROM " + DataBase.TABLE_NAME
																								+ " WHERE " + DataBase.ID + "="+userid;
																						String friends = "";
																						Cursor res = sqdb.rawQuery(select, null);
																						
																						while(res.moveToNext()){
																							friends = res.getString(res.getColumnIndex(DataBase.FRIENDS));
																						}
																						res.moveToFirst();
																						res.close();
																						if(friends == null){
																							friends = "";
																						}
																						friends +=	ids[position]+":"+names[position]+"[del]";	
																						
																						String update = "UPDATE " + DataBase.TABLE_NAME
																								+ " SET " + DataBase.FRIENDS + "='" + friends
																								+ "' WHERE " +DataBase.ID +"="+userid;
																						sqdb.execSQL(update);
																						
																						sqdb.close();
																						sqh.close();
																						getBack(v);
																					} else {
																						if(response.equals("1")){
																							new MyAlertDialog("Внимание!", "Пользователь уже добавлен", "Отмена", Contact.this);
																						}
																						if(response.equals("2")){
																							new MyAlertDialog("Внимание!", "Критическая ошибка сервера", "Отмена", Contact.this);
																						}
																					}
																				} catch(Exception e){
																					
																				}
																				
																			}
																		})
																.setNegativeButton("Отмена",
																		new DialogInterface.OnClickListener() {
																			public void onClick(DialogInterface dialog, int id) {
																				dialog.cancel();
																			}
																		});
														AlertDialog alert = builder.create();
														alert.show();
													}
												
											});
										}
									} else {
										
									}
								} catch(Exception e){
									
								}
							}
						})
				.setNegativeButton("Отмена", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						dialog.cancel();
					}
					
				});
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void getBack(View v) {
		Intent intent = new Intent(Contact.this, Contact.class);
		intent.putExtra("userid", userid);
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
