package com.example.messengerapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class ThreadNewData extends AsyncTask<Integer, Void, Void> {
	MessagesDB sqh;
	SQLiteDatabase sqdb;
	private Context mContext;
	private int userid;
	
    public ThreadNewData(Context context) {
          mContext = context;
    } 
	@Override
	protected Void doInBackground(Integer... params) {
		userid = Integer.valueOf(params[0]);
		String response = "";
		while(!isCancelled()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}
	    	DefaultHttpClient client = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet("http://gerasimov-design.ru/server/server.php?do=looper&user="+params[0]);
	        try {
	            HttpResponse execute = client.execute(httpGet);
	            InputStream content = execute.getEntity().getContent();
	            BufferedReader buffer = new BufferedReader(
	                    new InputStreamReader(content));
	            String s = "";
	            response = "";
	            while ((s = buffer.readLine()) != null) {
	                response += s;
	            }
				Log.w("from thread", "dkvjds"+response);
	            
	            if(response.length() > 0){
	            	String [] fields = response.split("\\|");
	            	for(int i = 0; i < fields.length; i++){
	            		String the_field = fields[i];
	            		String [] pieces = the_field.split(":");
	            		if(pieces[0].equals("0")){
	            			Server server = new Server();
	            			String q = "http://gerasimov-design.ru/server/server.php?do=getmessage&id="+pieces[1];
	            			server.execute(q);
	            			String message = server.get();
	            			
	            			String [] parts = message.split(":");
	            			// 0 - from, 1 - to, 2 - time, 3 - text
	            			
	            			sqh = new MessagesDB(mContext);
	            			sqdb = sqh.getWritableDatabase();
	            			String query = "INSERT INTO " 
	            			+ MessagesDB.TABLE_NAME + " ("
	            			+ MessagesDB.TEXT + ", "
	            			+ MessagesDB.OWNER + ", " 
	            			+ MessagesDB.RECIEVER + ", " 
	            			+ MessagesDB.SENDER + ", " 
	            			+ MessagesDB.NEW + ") VALUES ('"
	            			+ parts[5] + "', "
	            			+ userid + ", " 
	            			+ parts[1] + ", " 
	            			+ parts[0] + ","
	            			+ 1 + ") ";
	            			sqdb.execSQL(query);
	            			sqdb.close();
	            			sqh.close();
	            		}
	            	}
	            }
	            
	        } catch (Exception e) {
	        	Log.w("MyApp", "error");
	            e.printStackTrace();
	        }
	    }
		return null;
	}
}
