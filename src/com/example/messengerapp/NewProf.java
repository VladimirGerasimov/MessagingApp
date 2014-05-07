package com.example.messengerapp;

import java.util.concurrent.ExecutionException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

public class NewProf extends Activity {

	DataBase sqh;
	SQLiteDatabase sqdb;

	String name_st;
	String login_st;
	String pass_st;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_prof);

	}

	public void serverReg(final View v) {
		EditText name = (EditText) findViewById(R.id.editText1);
		EditText login = (EditText) findViewById(R.id.editText2);
		EditText pass = (EditText) findViewById(R.id.editText3);
		name_st = name.getText().toString();
		login_st = login.getText().toString();
		pass_st = pass.getText().toString();

		sqh = new DataBase(this);
		sqdb = sqh.getWritableDatabase();
		if (!(name_st.length() < 3 || login_st.length() < 6 || pass_st.length() < 8)) {
			String addr = getString(R.string.server_adr) + "?do=register&name="
					+ name_st + "&login=" + login_st + "&pass=" + pass_st;
			Server serv = new Server();
			serv.execute(addr);
			try {
				String result = serv.get();
				String[] parts = result.split(":");
				if (parts[0].equals("0")) {
					// / response code 0 - register ok
					String id = parts[1].split("=")[1];
					String _name = parts[2].split("=")[1];
					
					String updateQuery = "UPDATE " + DataBase.TABLE_NAME
							+ " SET " + DataBase.LOGGED_IN + " = 0";
					sqdb.execSQL(updateQuery);
					
					String insertQuery = "INSERT INTO " + DataBase.TABLE_NAME
							+ " (" + DataBase.ID + ", " + DataBase.NAME + ", "
							+ DataBase.LOGGED_IN + ") VALUES (" + id + ", '"
							+ _name + "', 1)";
					sqdb.execSQL(insertQuery);
					////////////////////////
					AlertDialog.Builder builder = new AlertDialog.Builder(
							NewProf.this);
					builder.setTitle("��������!")
							.setMessage(
									"�� ������� ����������������! ������ �� ������������� ������� � �������")
							.setIcon(R.drawable.ic_launcher)
							.setCancelable(false)
							.setPositiveButton("����������",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
											returnHome(v); //have to leave it here because need to call returnHome() 
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
					/////////////////////////////////
				} else if (parts[0].equals("1")) {
					// / error code 1 - login taken
					new MyAlertDialog(
							"��������!",
							"����� ������������ ��� ���������� � �������! ������� ���������� �����.",
							"��������", NewProf.this);
					
				} else if (parts[0].equals("1")) {
					new MyAlertDialog(
							"��������!",
							"��������� ��������� ������! ���������� ���������� �����������.",
							"��������", NewProf.this);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			String invalid = "";
			if (name_st.length() < 3) {
				invalid += "��� ������ ���� �� ������ 3 ������. ";
			}
			if (login_st.length() < 6) {
				invalid += "����� ������ ���� �� ������ 6 ������. ";
			}
			if (pass_st.length() < 4) {
				invalid += "������ ������ ���� �� ������ 4 ������. ";
			}
			new MyAlertDialog("��������!", invalid, "��������", NewProf.this);

		}
		sqdb.close();
		sqh.close();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		delayedHide(100);
	}

	public void returnHome(View v) {
		Intent intent = new Intent(NewProf.this, Main.class);
		startActivity(intent);
	}

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			// mSystemUiHider.hide();
		}
	};

	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
