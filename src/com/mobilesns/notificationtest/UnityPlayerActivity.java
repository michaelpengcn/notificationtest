package com.mobilesns.notificationtest;

import java.io.Console;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.unity3d.player.*;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class UnityPlayerActivity extends Activity
{
	protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
	// added by cpeng
	private static Context mContext = null;
	private static final int INTERVAL_DAILY		= 1000 * 60 * 60 * 24;// 24h
	private static final int INTERVAL_WEEKLY	= 1000 * 60 * 60 * 24 * 7; // a week 
	private static final int INTERVAL			= 1000 * 30;// 30 seconds
	private static final int REQUEST_CODE		= 10001;
	public static String PushAction = "AlarmAction";
	// added end
	
	// Setup activity layout
	@Override protected void onCreate (Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

		mUnityPlayer = new UnityPlayer(this);
		setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();
		mContext = this;
		SetupNotificationMessage1 ("test wakeup notifiaction", 11, 14, 0, 100, 1);
	}
	
	public void SetupNotificationMessage1(String message, int hour, int minute, int second, int repeatMode, int id)
    {}
	
	// repeatMode, 0: none, 1: everyday, 2: week
	public void SetupNotificationMessage(String message, int hour, int minute, int second, int repeatMode, int id)
    {
		Log.v("cpeng > ", "SetupNoficationMessage");
		if (repeatMode == 0) {
			Log.v("cpeng > ", "SetupNoficationMessage > do not support non-repeat mode yet");
			return;
		}
		
		int interval = INTERVAL_DAILY;
		if (repeatMode == 1) {
			interval = INTERVAL_DAILY;
		}
		else if (repeatMode == 2) {
			interval = INTERVAL_WEEKLY;
		}
		else if (repeatMode == 100) {
			// this is for test, 30 second repeat
			interval = INTERVAL;
		}
		
		Log.v("cpeng > ", "SetupNoficationMessage > repeatMode: 1");
		Intent intent = new Intent(getApplicationContext(), AlarmNotificationReceiver.class);
		intent.setAction(PushAction);
		intent.putExtra("id", id);
		intent.putExtra("msg", message);
		PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(),
				REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Schedule the alarm!
		AlarmManager am = (AlarmManager) getApplicationContext()
			.getSystemService(Context.ALARM_SERVICE);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);

		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				interval, sender);
    }
	
	public void CancelNotification(int id) {
		Intent intent = new Intent(getApplicationContext(), AlarmNotificationReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), id, intent, 0);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
	}
	
	// Quit Unity
	@Override protected void onDestroy ()
	{
		mUnityPlayer.quit();
		super.onDestroy();
	}

	// Pause Unity
	@Override protected void onPause()
	{
		super.onPause();
		mUnityPlayer.pause();
	}

	// Resume Unity
	@Override protected void onResume()
	{
		super.onResume();
		mUnityPlayer.resume();
	}

	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}


