package com.mobilesns.notificationtest;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmNotificationPusher {
	// added by cpeng
		private static Context mContext = null;
		private static final int INTERVAL_DAILY		= 1000 * 60 * 60 * 24;// 24h
		private static final int INTERVAL_WEEKLY	= 1000 * 60 * 60 * 24 * 7; // a week 
		private static final int INTERVAL			= 1000 * 30;// 30 seconds
		private static final int REQUEST_CODE		= 10001;
		public static String PushAction = "AlarmAction";
		// added end
		
	// repeatMode, 0: none, 1: everyday, 2: week
	public static void SetupNotificationMessage(Context applicationContext, String message, int hour, int minute, int second, int repeatMode, int id) {
		Log.v("cpeng > ", "SetupNoficationMessage");
		if (repeatMode == 0) {
			Log.v("cpeng > ", "SetupNoficationMessage > do not support non-repeat mode yet");
			return;
		}

		int interval = INTERVAL_DAILY;
		if (repeatMode == 1) {
			interval = INTERVAL_DAILY;
		} else if (repeatMode == 2) {
			interval = INTERVAL_WEEKLY;
		} else if (repeatMode == 100) {
			// this is for test, 30 second repeat
			interval = INTERVAL;
		}

		Log.v("cpeng > ", "SetupNoficationMessage > repeatMode: 1");
		Intent intent = new Intent(applicationContext, AlarmNotificationReceiver.class);
		intent.setAction(PushAction);
		intent.putExtra("id", id);
		intent.putExtra("msg", message);
		PendingIntent sender = PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		// Schedule the alarm!
		AlarmManager am = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);

		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, sender);
	}
}
