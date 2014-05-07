package com.example.messengerapp;

import com.example.messengerapp.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class Conv extends Activity {
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	public boolean visible = false;
	public View controlsView;
	private SystemUiHider mSystemUiHider;
	int userid = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userid = (int) getIntent().getIntExtra("userid", 0);

		setContentView(R.layout.activity_conv);
		controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		hideit(contentView);
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				hideit(view);
			}
		});
		findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void hideit(View v){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			controlsView
				.animate()
				.translationY(visible ? 0 : controlsView.getHeight())
				.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
		} else {
			controlsView.setVisibility(visible ? View.VISIBLE
					: View.GONE);
		}
		visible = !visible;
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
	
	public void startNewDialog(View v){
		Intent intent = new Intent(Conv.this, ConvFriends.class);
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
