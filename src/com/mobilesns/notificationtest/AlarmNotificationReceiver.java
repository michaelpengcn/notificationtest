package com.mobilesns.notificationtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class AlarmNotificationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("cpeng", "receive a alarm notification");
		if (intent.getAction().equals(UnityPlayerActivity.PushAction)) {
			String	msg		= intent.getStringExtra("msg");
			int		id		= intent.getIntExtra("id", 0); 
			sendNotify (id, msg, context);
		}
	}
	
	public static void sendNotify(final int id, final String body, final Context ctx)
	{
		Log.v("cpeng", "send notification with msg: " + body);
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
		Notification.Builder builder = new Notification.Builder(ctx)  
	            .setAutoCancel(true)  
	            .setContentTitle(getApplicationName(ctx))  
	            .setContentText(body)  
	            .setContentIntent(contentIntent)  
	            .setSmallIcon(R.drawable.app_icon)  
	            .setWhen(System.currentTimeMillis())  
	            .setOngoing(true);  
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
}