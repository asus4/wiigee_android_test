package asus4.wiigeeandroid;

import java.io.IOException;

import org.wiigee.control.AndroidWiigee;
import org.wiigee.control.Wiigee;
import org.wiigee.event.GestureEvent;
import org.wiigee.event.GestureListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import asus4.wiigeeandroid.R.id;

public class MainActivity extends Activity {
	
	static final int _TRAIN_BUTTON = 0x01;
	static final int _SAVE_BUTTON = 0x02;
	static final int _RECOGNIZE_BUTTON = 0x03;
	
	AndroidWiigee wiigee;
	Logger logger;
	
	boolean isRecording;
	boolean isRecognizing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		wiigee = new AndroidWiigee(this);
		logger = new Logger((TextView) findViewById(id.logText), 20);
		isRecording = false;
		isRecognizing = false;
		
		wiigee.setTrainButton(_TRAIN_BUTTON);
		wiigee.setCloseGestureButton(_SAVE_BUTTON);
		wiigee.setRecognitionButton(_RECOGNIZE_BUTTON);
		wiigee.addGestureListener(new GestureListener() {
			
			@Override
			public void gestureReceived(GestureEvent event) {
				logger.addLog("Recognized: "+event.getId()+" Probability: "+event.getProbability());
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			wiigee.getDevice().setAccelerationEnabled(true);
		}
		catch(IOException e) {
			
		}
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		try {
			wiigee.getDevice().setAccelerationEnabled(false);			
		}
		catch(IOException e) {
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// events from xml
	public void onRecordButtonClick(View view) {
		Button btn = (Button) view;
		if(isRecording) {
			btn.setText(R.string.button_train_start);
			isRecording = false;
			wiigee.getDevice().fireButtonReleasedEvent(_TRAIN_BUTTON);
		}
		else {
			btn.setText(R.string.button_train_stop);
			isRecording = true;
			wiigee.getDevice().fireButtonPressedEvent(_TRAIN_BUTTON);
		}
		logger.addLog("click:onRecord:"+isRecording);
	}
	
	public void onSaveButtonClick(View view) {
		logger.addLog("click:onSave");
		wiigee.getDevice().fireButtonPressedEvent(_SAVE_BUTTON);
		wiigee.getDevice().fireButtonReleasedEvent(_SAVE_BUTTON);
	}
	
	public void onRecognizeButtonClick(View view) {
		Button btn = (Button) view;
		if(isRecognizing) {
			btn.setText(R.string.button_recognize_start);
			isRecognizing = false;
			wiigee.getDevice().fireButtonReleasedEvent(_RECOGNIZE_BUTTON);
		}
		else {
			btn.setText(R.string.button_recognize_stop);
			isRecognizing = true;
			wiigee.getDevice().fireButtonPressedEvent(_RECOGNIZE_BUTTON);
		}
		logger.addLog("click:onRecognize");
	}

}
