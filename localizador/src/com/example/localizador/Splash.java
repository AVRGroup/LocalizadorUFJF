package com.example.localizador;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity implements Runnable {
	private final int DELAY = 3000;
	//private final int DELAY = 500;

	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);

		setContentView(R.layout.splash_layout);
		//getActionBar().hide();

		Handler handler = new Handler();
		handler.postDelayed(this, DELAY);
	}

	public void run() {

		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);

		finish();

	}
}
