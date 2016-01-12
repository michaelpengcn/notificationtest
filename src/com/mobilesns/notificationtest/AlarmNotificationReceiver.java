package com.mobilesns.notificationtest;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class AlarmNotificationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		Log.v("cpeng", "receive a alarm notification");
		if (intent.getAction().equals(AlarmNotificationPusher.PushAction)) {
			int		repeatMode	= intent.getIntExtra("repeatMode", 0);
			String	msg		= intent.getStringExtra("msg");
			int		id		= intent.getIntExtra("id", 0);
			int		dayOfWeek	= intent.getIntExtra("dayOfWeek", 7);
			int		hour	= intent.getIntExtra("hour", 12);
			int		minute	= intent.getIntExtra("minute", 0);
			int		second	= intent.getIntExtra("second", 0);
			
			wakeUpScreen (ctx);
			sendNotify (id, msg, ctx);
			
			Boolean force = true;
			if ((force || Build.VERSION.SDK_INT >= AlarmNotificationUtil.FAKE_KITKAT_WATCH) && (repeatMode == 1 || repeatMode == 2)) {
				Log.e("cpeng > ", "reset a notification" + msg);
				int interval = AlarmNotificationUtil.getIntervalForMode(repeatMode);
				PendingIntent sender = AlarmNotificationUtil.getPendingIntent(ctx, AlarmNotificationPusher.PushAction, 
						id, repeatMode, msg, dayOfWeek, hour, minute, second);
				
				// Schedule the alarm!
				AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
				Calendar calendar = AlarmNotificationUtil.getCalendarForTime(repeatMode, dayOfWeek, hour, minute, second);
				AlarmNotificationUtil.setupNotificationMessage(am, calendar, interval, sender);
			}
		}
	}
	
	public static void sendNotify(final int id, final String msgBody, final Context ctx)
	{
		Log.v("cpeng", "send notification with msg: " + msgBody);
		NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(ctx, UnityPlayerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, id,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// smaller than API Level 11 (Android 2.3.3, not include)
//		Notification noti = new Notification(R.drawable.app_icon, body,System.currentTimeMillis());
//		noti.defaults = Notification.DEFAULT_SOUND;
//		String title = ctx.getString(R.string.app_name);
//		noti.flags = Notification.FLAG_AUTO_CANCEL;
//		noti.setLatestEventInfo(ctx,title, body, contentIntent);
	
		// bigger than API Level 16
		//@SuppressWarnings("deprecation")
		Notification.Builder builder = new Notification.Builder(ctx)  
	            .setAutoCancel(true)  
	            .setContentTitle(getApplicationName(ctx))  
	            .setContentText(msgBody) 
	            .setTicker(msgBody)
	            .setContentIntent(contentIntent)  
	            .setSmallIcon(R.drawable.app_icon)
	            .setDefaults(Notification.DEFAULT_SOUND) // if mixed with vibrate flag, we need to specify the vibrate permission
	            //.setSound(Notification.DEFAULT_SOUND, Content)
	            //.setVibrate()  {0,100,200,300}
	            //.setOngoing(true); // set true to don`t allow user to dismiss message on message bar 
	            //.setAutoCancel(true)
	            .setWhen(System.currentTimeMillis());
	            
	             
		Notification noti =builder.build();
		
		nm.notify(id, noti);
	}
	
	 private static String getApplicationName(Context ctx) { 
	        PackageManager packageManager = null; 
	        ApplicationInfo applicationInfo = null; 
	        try { 
	            packageManager = ctx.getPackageManager(); 
	            applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), 0); 
	        } catch (PackageManager.NameNotFoundException e) { 
	            applicationInfo = null; 
	        } 
	        String applicationName =  
	        (String) packageManager.getApplicationLabel(applicationInfo); 
	        return applicationName; 
	    }
	 
	 @SuppressWarnings("deprecation")
	private static void wakeUpScreen (Context ctx) {
		 PowerManager pm = (PowerManager)ctx.getSystemService(Context.POWER_SERVICE);
		 boolean isScreenOn = true;
		 // greater or equal to api level 20
		 if (Build.VERSION.SDK_INT > AlarmNotificationUtil.FAKE_KITKAT_WATCH) {
			 isScreenOn = pm.isInteractive();
		     Log.v("cpeng", "alarm screen is interactive");
		 }
		 else if (Build.VERSION.SDK_INT <= AlarmNotificationUtil.FAKE_KITKAT_WATCH) {
			 isScreenOn = pm.isScreenOn();
			 Log.v("cpeng", "alarm screen is on");
		 }
		 else {
			 Log.v("cpeng", "alarm screen OFF");
		 }

         if(isScreenOn==false)
         {
        	 WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,"ALock");
        	 wl.acquire(10000);
        	 wl.release();
//        	 WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"ACPULock");
//        	 wl_cpu.acquire(10000);
         }
	 }
}