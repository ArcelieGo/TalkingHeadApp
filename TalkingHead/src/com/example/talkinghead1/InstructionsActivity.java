package com.example.talkinghead1;

import java.util.HashSet;
import java.util.Timer;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.os.Build;

public class InstructionsActivity extends ActionBarActivity {
	private final Handler h = new Handler();
	private static HashSet<MediaPlayer> mpSet = new HashSet<MediaPlayer>();
	private static final String TAG = "TalkingHeadApp";
	private MediaPlayer mediaPlayer;
	private MediaPlayer mPlayer;
	private MyRunnable playMyAudio;
	
	public void enterApp(View v){
		Intent i = new Intent(this,MainActivity.class);
		this.startActivity(i);
	}
	
	public class MyRunnable implements Runnable {
		  private int responseId;
		  public MyRunnable(int _responseId) {
		    this.responseId = _responseId;
		  }
		  
		@Override
		public void run() {
			mPlayer = MediaPlayer.create(InstructionsActivity.this,responseId );
			mPlayer.start();
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					 mpSet.remove(mp);
					 mp.stop();
					 mp.release();
				}
			});
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
			
			
   			playMyAudio = new MyRunnable(R.raw.welcome_message);
   			h.postDelayed(playMyAudio, 500);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.instructions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_instructions,
					container, false);
			return rootView;
		}
	}

}
