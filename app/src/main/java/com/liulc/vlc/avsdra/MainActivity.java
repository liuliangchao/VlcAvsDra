package com.liulc.vlc.avsdra;


import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {
	private VlcPlayerControl mVlcPlayerControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		VlcPlayerView vlcPlayerView = (VlcPlayerView) findViewById(R.id.vlcPlayerView);
		mVlcPlayerControl = new VlcPlayerControl(this);

		mVlcPlayerControl.attachView(vlcPlayerView);
//		vlcPlayerControl.load("http://192.168.9.222/dra.ts");
		mVlcPlayerControl.load("file:///" + Environment.getExternalStorageDirectory().getPath() +"/dra.ts");
		mVlcPlayerControl.play();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		mVlcPlayerControl.release();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				break;
		}

		return super.onKeyDown(keyCode, event);
	}
}
