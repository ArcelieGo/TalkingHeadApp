package com.example.talkinghead1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {
	private final Handler h = new Handler();
	private static HashSet<MediaPlayer> mpSet = new HashSet<MediaPlayer>();
	
	private SpeechRecognizer sr;
	private static final String TAG = "TalkingHeadApp";
	private MediaPlayer mediaPlayer;
	private MediaPlayer mPlayer;
	private MyRunnable playMyAudio;

	private int _index;
	private boolean _faceAnim;
	private ImageView _imagView;
	private Timer _timer;
	private MyHandler handler;
	private Bitmap face0;
	private Bitmap face1;
	private int noResultMatchCounter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
			
			try {
				
				face0 = BitmapFactory.decodeStream(MainActivity.this.getAssets().open("face_0.png"));
				face1 = BitmapFactory.decodeStream(MainActivity.this.getAssets().open("face_1.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			noResultMatchCounter =0;
					
	        sr = SpeechRecognizer.createSpeechRecognizer(this);       
	        sr.setRecognitionListener(new listener());
	        
   			playMyAudio = new MyRunnable(R.raw.welcome_message);
   			
   			h.postDelayed(playMyAudio, 500);
		}
	
	}
	
	public class MyRunnable implements Runnable {
		  private int responseId;
		  public MyRunnable(int _responseId) {
		    this.responseId = _responseId;
		  }
		  
		@Override
		public void run() {
			mPlayer = MediaPlayer.create(MainActivity.this,responseId );
			mPlayer.start();
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					 mpSet.remove(mp);
					 mp.stop();
					 mp.release();
					 //finish();
					startVoiceListening();
				}
			});
			
			handler= new MyHandler();
			_imagView=(ImageView) findViewById(R.id.imageView1);
			_index=1;
			_faceAnim = false;
       	    _timer= new Timer();
			_timer.schedule(new TickClass(), 500, 250);
		}
	}
	
	private class TickClass extends TimerTask
	{
	    @Override
	    public void run() {
	    	handler.sendEmptyMessage(_index);
	    	_faceAnim = !_faceAnim;
	    }
	}
	
	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler
	{
	    @Override
	    public void handleMessage(Message msg) {
	        // TODO Auto-generated method stub
	        super.handleMessage(msg);
	        if (_faceAnim) {
	        	_imagView.setImageBitmap(face1);
	        } else {
	        	_imagView.setImageBitmap(face0);
	        }
	    }
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	
   class listener implements RecognitionListener          
   {
            public void onReadyForSpeech(Bundle params)
            {
                     Log.d(TAG, "onReadyForSpeech");
            }
            public void onBeginningOfSpeech()
            {
                     Log.d(TAG, "onBeginningOfSpeech");
            }
            public void onRmsChanged(float rmsdB)
            {
                     //Log.d(TAG, "onRmsChanged");
            }
            public void onBufferReceived(byte[] buffer)
            {
                     //Log.d(TAG, "onBufferReceived");
            }
            public void onEndOfSpeech()
            {
                     Log.d(TAG, "onEndofSpeech");
                     startVoiceListening();
            }
            public void onError(int error)
            {
                     Log.d(TAG,  "error " +  error);
                     
                     if (error == 6) {//No speech input
                    		 playMyAudio = new MyRunnable(R.raw.welcome_message);
                    		 h.postDelayed(playMyAudio, 500); 
                     }
                     
                     if (error == 7 || error == 4  ) {//No recognition result matched
                    	 startVoiceListening();
                     }
                     
                     // error 1 Network operation timed out.
                     // error 2 Other network related errors.
                     
     
                     if (error == 1 || error == 2)  { 
                    	 finish();
                     }
                     
            }
            public void onResults(Bundle results)                   
            {
                     Log.d(TAG, "onResults " + results);
                     String tmpData;
                     tmpData = "";
                     
                     ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                     for (int i = 0; i < data.size(); i++)
                     {
	                        tmpData += " " + data.get(i).toString();
	                        Log.d(TAG, "Result : " + data.get(i).toString());
                     }  
                     
                		if (tmpData.toLowerCase().contains("library")) {
                			noResultMatchCounter = 0;
                			playMyAudio = new MyRunnable(R.raw.library);
                   			h.postDelayed(playMyAudio, 500);
                   		}
                   		else if (tmpData.toLowerCase().contains("atm")) {
                   			noResultMatchCounter = 0;
                   			playMyAudio = new MyRunnable(R.raw.atm);
                   			h.postDelayed(playMyAudio, 500);
                   		}
                   		else if (tmpData.toLowerCase().contains("bookshop")) {
                   			noResultMatchCounter = 0;
                   			playMyAudio = new MyRunnable(R.raw.bookshop);
                   			h.postDelayed(playMyAudio, 500);
                   		}
                   		else if (tmpData.toLowerCase().contains("engineering")) {
                   			noResultMatchCounter = 0;
                   			playMyAudio = new MyRunnable(R.raw.engineering);
                   			h.postDelayed(playMyAudio, 500);
                   			
                   		}
                   		else if (tmpData.toLowerCase().contains("reception")) {
                   			noResultMatchCounter = 0;
                   			playMyAudio = new MyRunnable(R.raw.international_house_darwin_reception);
                   			h.postDelayed(playMyAudio, 500);
                   			
                   		}
                   		else if (tmpData.toLowerCase().contains("auditorium")) {
                   			noResultMatchCounter = 0;
                   			playMyAudio = new MyRunnable(R.raw.malnairn_auditorium);
                   			h.postDelayed(playMyAudio, 500);
                   			
                   		}
                   		else if (tmpData.toLowerCase().contains("navitas")) {
                   			noResultMatchCounter = 0;
                   			playMyAudio = new MyRunnable(R.raw.navitas_office);
                   			h.postDelayed(playMyAudio, 500);
                   			
                   		}
                   		else if (tmpData.toLowerCase().contains("post office")) {
                   			playMyAudio = new MyRunnable(R.raw.post_office);
                   			h.postDelayed(playMyAudio, 500);
                   			noResultMatchCounter = 0;
                   		}
                   		else if (tmpData.toLowerCase().contains("security")) {
                   			noResultMatchCounter = 0;
                   			playMyAudio = new MyRunnable(R.raw.security);
                   			h.postDelayed(playMyAudio, 500);
                   			
                   		}
                   		else if (tmpData.toLowerCase().contains("theater")) {
                   			noResultMatchCounter = 0;
                   			playMyAudio = new MyRunnable(R.raw.university_theater);
                   			h.postDelayed(playMyAudio, 500);
                   			
                   		} else {//not in our choices
	                       	 noResultMatchCounter += 1;
	                    	 
	                       	 if (noResultMatchCounter == 1) {
	                       		 playMyAudio = new MyRunnable(R.raw.try_again);
	                       		 h.postDelayed(playMyAudio, 500); 
	                       	 } else {
	                       		 playMyAudio = new MyRunnable(R.raw.default_answer);
	                       		 h.postDelayed(playMyAudio, 500);
	                       	 }

                   		}   
                     
                		Log.d("DATA return", "DATA return " + tmpData);
            }
            public void onPartialResults(Bundle partialResults)
            {
                     Log.d(TAG, "onPartialResults");
            }
            public void onEvent(int eventType, Bundle params)
            {
                     Log.d(TAG, "onEvent " + eventType);
            }
   }
 
	public void startVoiceListening(){
		if (_timer != null) {
       	    _timer.cancel();
       	    _imagView.setImageBitmap(face0);
		}
		
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5); 
		sr.startListening(intent);
		Log.i(TAG,"startVoiceListening");
	}

	public void playPauseVoice(View v){
        
        try {
    		if (mPlayer != null) {
    			if(mPlayer.isPlaying()){
    				if (_timer != null) {
    		       	    _timer.cancel();
    		       	    _imagView.setImageBitmap(face0);
    				}
    				mPlayer.pause();
    			}
    			else{
    				mPlayer.start();
    				_timer.cancel();
    	       	    _timer= new Timer();
    				_timer.schedule(new TickClass(), 500, 250);
    			}
    		}
        } catch (Exception e) {
        	Log.v("Exception in Handler ",e.getMessage());
        }


	}
}
