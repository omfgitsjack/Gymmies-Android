package com.example.gymmies;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class Start extends Activity {

	MediaPlayer ourSong;

	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.start);
		super.onCreate(savedInstanceState); 
		
		ourSong = MediaPlayer.create(Start.this, R.raw.whistle2);
		ourSong.start();
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(990);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent openLogin = new Intent("com.example.gymmies.Login");
					startActivity(openLogin);
				}
			}
		};
		timer.start();
	}

	protected void onPause() {
		super.onPause();
		ourSong.stop();
		finish();

	}
}
