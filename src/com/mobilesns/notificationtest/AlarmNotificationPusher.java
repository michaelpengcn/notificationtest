package com.mobilesns.notificationtest;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmNotificationPusher {
	// added by cpeng
	private static final int INTERVAL_DAILY = 1000 * 60 * 60 * 24;// 24h
	private static final int INTERVAL_WEEKLY = 1000 * 60 * 60 * 24 * 7; // a week
	private static final int INTERVAL = 1000 * 30;// 30 seconds
	public static String PushAction = "AlarmAction";
	// added end
		
	// repeatMode, 0: none, 1: everyday, 2: week
	public static void SetupNotificationMessage(Context applicationContext, String message, 
			int dayOfWeek, int hour, int minute, int second, int repeatMode, int id) {
		Log.e("cpeng > ", "SetupNoficationMessage");
		if (repeatMode == 0) {
			Log.e("cpeng > ", "SetupNoficationMessage > do not support non-repeat mode yet");
			return;
		}

		int interval = AlarmNotificationUtil.getIntervalForMode(repeatMode);

		Log.e("cpeng > ", "SetupNoficationMessage > repeatMode: 1");

		PendingIntent sender = AlarmNotificationUtil.getPendingIntent(applicationContext, PushAction, 
				id, repeatMode, message, dayOfWeek, hour, minute, second);
		
		// Schedule the alarm!
		AlarmManager am = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = AlarmNotificationUtil.getCalendarForTime(repeatMode, dayOfWeek, hour, minute, second);
		AlarmNotificationUtil.setupNotificationMessage(am, calendar, interval, sender);
	}
}
