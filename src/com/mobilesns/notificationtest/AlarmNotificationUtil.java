package com.mobilesns.notificationtest;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmNotificationUtil {
	public static final int INTERVAL_DAILY = 1000 * 60 * 60 * 24;// 24h
	public static final int INTERVAL_WEEKLY = 1000 * 60 * 60 * 24 * 7; // a week
	public static final int INTERVAL = 1000 * 30;// 30 seconds
	public static final int FAKE_KITKAT_WATCH = 19;
	
	public static Calendar getCalendarForTime (int repeatMode, int dayOfWeek, int hour, int minute, int second) {
		Calendar calendarNow = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		if (repeatMode == 2) {
			int dayOfWeekAndroid = 0; // 7 stands for sunday for interface, but for android, sunday stands for 1.
			dayOfWeekAndroid = dayOfWeek % 7 + 1;
			calendar.set(Calendar.DAY_OF_WEEK, dayOfWeekAndroid);
		}
		
		// make sure the desire alarm time is in future.
		int tryCount = 0;
		int tryCountMax = 62;
		while (calendar.getTimeInMillis() < calendarNow.getTimeInMillis() && tryCount < tryCountMax) {
			if (repeatMode == 1) {
				calendar.add(Calendar.DAY_OF_YEAR, 1);
			}
			else if (repeatMode == 2) {
				calendar.add(Calendar.DAY_OF_YEAR, 7);
			}
			tryCount++;
		}
		Log.v("cpeng", "getCalendearForTime target info: " + calendar.toString());
		return calendar;
	}
	
	public static int getIntervalForMode (int repeatMode) {
		int interval = INTERVAL_DAILY;
		if (repeatMode == 1) {
			interval = INTERVAL_DAILY;
		} else if (repeatMode == 2) {
			interval = INTERVAL_WEEKLY;
		} else if (repeatMode == 100) {
			// this is for test, 30 second repeat
			interval = INTERVAL;
		}
		
		return interval;
	}
	
	public static PendingIntent getPendingIntent (Context applicationContext, String PushAction, int id, 
			int repeatMode, String message, int dayOfWeek, int hour, int minute, int second) {
		Intent intent = new Intent(applicationContext, AlarmNotificationReceiver.class);
		intent.setAction(PushAction);
		intent.putExtra("id", id);
		intent.putExtra("repeatMode", repeatMode);
		intent.putExtra("msg", message);
		intent.putExtra("dayOfWeek", dayOfWeek);
		intent.putExtra("hour", hour);
		intent.putExtra("minute", minute);
		intent.putExtra("second", second);
		PendingIntent sender = PendingIntent.getBroadcast(applicationContext, id, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		return sender;
	}
	
	public static void setupNotificationMessage (AlarmManager am, Calendar calendar, int interval, PendingIntent sender) {
		// official docs:
		// Note: as of API 19, all repeating alarms are inexact. If your application needs precise 
		// delivery times then it must use one-time exact alarms, rescheduling each time as described above. 
		// Legacy applications whosetargetSdkVersion is earlier than API 19 will continue to have all of their alarms, including repeating alarms, treated as exact.
		if (Build.VERSION.SDK_INT >= FAKE_KITKAT_WATCH) {
			//am.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 20000, sender);
			am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		}
		else {
			// use this on sdk level 18 and smaller than 18. later sdk won`t guarantee time to be precise.
			//am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, sender);
			am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		}
	}
}