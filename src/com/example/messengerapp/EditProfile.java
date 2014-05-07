package com.example.messengerapp;

import java.util.concurrent.ExecutionException;

import com.example.messengerapp.util.SystemUiHider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditProfile extends Activity {
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	DataBase sqh;
	SQLiteDatabase sqdb;
	int serveruserid = 0;
	int userid = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit_profile);

		userid = (int) getIntent().getIntExtra("userid", 0);

		sqh = new DataBase(this);
		sqdb = sqh.getReadableDatabase();
		TextView t = (TextView) findViewById(R.id.fullscreen_content);
		String query = "SELECT " + DataBase.NAME + ", " + DataBase.ID
				+ " FROM " + DataBase.TABLE_NAME + " WHERE _id = " + userid;
		Cursor cursor = sqdb.rawQuery(query, null);
		if (cursor.getCount() > 0) {
			String s = "";
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor
						.getColumnIndex(DataBase.NAME));
				s = t.getText() + "\n" + name;
				serveruserid = cursor
						.getInt(cursor.getColumnIndex(DataBase.ID));
			}
			t.setText(s);
		}

		final View contentView = t;

		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(1);
	}

	public void login(View v) {
		final EditText login = new EditText(this);
		final EditText password = new EditText(this);

		LinearLayout l = new LinearLayout(this);
		l.setOrientation(LinearLayout.VERTICAL);
		login.setHint("Введите логин");
		password.setHint("Введите пароль");
		l.addView(login);
		l.addView(password);

		password.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Авторизация")
				.setCancelable(false)
				.setView(l)
				.setPositiveButton("Вход",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								Server serv = new Server();
								String a = getString(R.string.server_adr)
										+ "?do=login&login="
										+ login.getText().toString() 
										+ "&pass="
										+ password.getText().toString();
								serv.execute(a);
								try {
									String result = serv.get();
									if (result.equals("0")) {
										// login success

										String updateQuery = "UPDATE "
												+ DataBase.TABLE_NAME + " SET "
												+ DataBase.LOGGED_IN
												+ " = 1 WHERE _id=" + userid;
										sqdb.execSQL(updateQuery);

										Intent refresh = new Intent(
												EditProfile.this, Main.class);
										startActivity(refresh);
									} else if (result.equals("1")) {
										new MyAlertDialog("Внимание!",
												"Пароль неверный!", "Изменить",
												EditProfile.this);
									} else if (result.equals("2")) {
										new MyAlertDialog("Внимание!",
												"Пользователь не найден!",
												"Изменить", EditProfile.this);
									} else {
										new MyAlertDialog(
												"Внимание!",
												"Произошла ошибка на стороне сервера!",
												"ОК :(", EditProfile.this);
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								} catch (ExecutionException e) {
									e.printStackTrace();
								}
							}
						})

				.setNegativeButton("Отмена",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.show();

	}

	public void deleteRequest(View v) {
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Введите пароль аккаунта")
				.setCancelable(false)
				.setView(input)
				.setPositiveButton("Удалить",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								Server serv = new Server();
								String a = getString(R.string.server_adr)
										+ "?do=delete&id="
										+ String.valueOf(serveruserid)
										+ "&pass=" + input.getText().toString();
								serv.execute(a);
								try {
									String result = serv.get();
									if (result.equals("0")) {
										// delete success
										sqdb.delete(DataBase.TABLE_NAME, "_id="
												+ userid, null);
										Intent refresh = new Intent(
												EditProfile.this,
												ProfilesAct.class);
										startActivity(refresh);
									} else if (result.equals("1")) {
										new MyAlertDialog("Внимание!",
												"Пароль неверный!", "Изменить",
												EditProfile.this);
									} else {
										new MyAlertDialog(
												"Внимание!",
												"Произошла ошибка на стороне сервера!",
												"ОК :(", EditProfile.this);
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								} catch (ExecutionException e) {
									e.printStackTrace();
								}
							}
						})

				.setNegativeButton("Отмена",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.show();
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

	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
