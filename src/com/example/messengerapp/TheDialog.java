package com.example.messengerapp;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
public class TheDialog extends Activity {
	int userid = -2;
	int dialog = -1;
	DialogDB sqh;
	SQLiteDatabase sqdb;
	MessagesDB sqh1;
	SQLiteDatabase sqdb1;
	int visavis = -1;
	Check checker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_the_dialog);
		LinearLayout ll = (LinearLayout)findViewById(R.id.messages_field);
		checker = new Check(this, ll);
		userid = (int) getIntent().getIntExtra("userid", 0);
		dialog = (int) getIntent().getIntExtra("dialog", 0);
				sqh = new DialogDB(this);
		sqdb = sqh.getWritableDatabase();
		
		String select = "SELECT " + DialogDB.USER + " FROM " + DialogDB.TABLE_NAME + " WHERE _id=" + dialog;
		Cursor cu = sqdb.rawQuery(select, null);
		while(cu.moveToNext()){
			visavis = cu.getInt(cu.getColumnIndex(DialogDB.USER));
		}
		cu.moveToFirst();
		cu.close();
		sqdb.close();
		sqh.close();
		checker.execute(userid, visavis);
	}
	@Override
	protected void onStop(){
		checker.cancel(true);
	}
	@Override
	protected void onResume(){
		checker.execute(userid, visavis);
	}
	public void sendMessage(View v){
		EditText e = (EditText)findViewById(R.id.editText1);
		String text = e.getText().toString();
		if(text.length() > 0){
			Server s = new Server();
			String text1 = text.replaceAll(" ", "%20");
			String send = getString(R.string.server_adr) +
					"?do=message&sender="+userid+"&reciever="+visavis+"&text="+text1;
			s.execute(send);
			try{
				String response = s.get();
				if(response.equals("0")){
					sqh1 = new MessagesDB(this);
					sqdb1 = sqh1.getWritableDatabase();
					String query = "INSERT INTO " 
	            			+ MessagesDB.TABLE_NAME + " ("
	            			+ MessagesDB.TEXT + ", "
	            			+ MessagesDB.OWNER + ", " 
	            			+ MessagesDB.RECIEVER + ", " 
	            			+ MessagesDB.SENDER + ", " 
	            			+ MessagesDB.NEW + ") VALUES ('" 
	    	            	+ text + "', "
	            			+ userid + ", " 
	            			+ visavis + ", " 
	            			+ userid + ", "
	            			+ 1 + ") ";
	            			sqdb1.execSQL(query);
	            			sqdb1.close();
	            			sqh1.close();
				}
			} catch(Exception fuck){
				
			}
		} 
		
	}
	
}
class Check extends AsyncTask<Integer, Cursor, Void>{
	Context con;
	LinearLayout ll;
	Cursor cu = null;
	Check(Context c, LinearLayout act){
		con = c;
		ll = act;
	}
	@Override
	protected Void doInBackground(Integer... params) {
		while(!isCancelled()){
			try{
				Thread.sleep(400);
				MessagesDB sqh = new MessagesDB(con);
				SQLiteDatabase sqdb = sqh.getWritableDatabase();
				String q = "SELECT * FROM " + MessagesDB.TABLE_NAME 
						+ " WHERE " + MessagesDB.OWNER + "=" + params[0]+ " AND ("
						+ MessagesDB.RECIEVER + "=" + params[1] + " OR "
						+ MessagesDB.RECIEVER + "=" + params[0]
						+ ") AND (" 
						+ MessagesDB.SENDER + "=" + params[1] + " OR "
						+ MessagesDB.SENDER + "=" + params[0] + ") AND "												
						+ MessagesDB.NEW + "=1";
				cu = sqdb.rawQuery(q, null);
				publishProgress(cu);
				q = "UPDATE " + MessagesDB.TABLE_NAME
						+ " SET " + MessagesDB.NEW + "=0" 
						+ " WHERE " + MessagesDB.OWNER 
						+ "=" + params[0];
				sqdb.execSQL(q);
				sqdb.close();
				sqh.close();
			} catch (Exception e){
				
			}
		}
		return null;
	}
	@Override
	protected void onProgressUpdate(Cursor... values) {
		super.onProgressUpdate(values);
		Cursor cu = values[0]; 
		Log.w("cursor length", String.valueOf(cu.getCount()));
		while(cu.moveToNext()){
			Log.w("while", "check");
			TextView t = new TextView(con);
			if(cu.getInt(cu.getColumnIndex(MessagesDB.SENDER)) == cu.getInt(cu.getColumnIndex(MessagesDB.OWNER))){
				t.setGravity(Gravity.RIGHT);
			}
			t.setText(cu.getString(cu.getColumnIndex(MessagesDB.TEXT)));
			ll.addView(t);
		}
		cu.moveToFirst();
		cu.close();
	}
}
