package com.example.messengerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MyAlertDialog {

	MyAlertDialog(String header, String body, String butText, Context cont){
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setTitle(header)
				.setMessage(body)
				.setIcon(R.drawable.ic_launcher)
				.setCancelable(false)
				.setPositiveButton(butText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
