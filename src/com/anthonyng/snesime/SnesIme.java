package com.anthonyng.snesime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class SnesIme extends InputMethodService {

	private final static String TAG = "SnesImeService";

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w(TAG, "onCreate");
		
		//Register a broadcast receiver for keypress events
		//registerReceiver(keypressReceiver, new IntentFilter(""));
	}

	@Override
	public View onCreateInputView() {
		super.onCreateInputView();
		Log.w(TAG, "onCreateInputView");
		return null;
	}

	@Override
	public View onCreateCandidatesView() {
		super.onCreateCandidatesView();
		return null;
	}

	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		super.onStartInputView(info, restarting);
	}

	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);
		Log.w(TAG, "onStartInput");
	}

	@Override
	public void onFinishInput() {
		super.onFinishInput();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private BroadcastReceiver keypressReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			InputConnection inputConnection = getCurrentInputConnection();
			
			//Send keypress event
			//inputConnection.sendKeyEvent(event);
			
		}
	};
}
