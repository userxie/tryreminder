package com.tryreminder.myandroid.newreminder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Date;

/**
 * Created by Muzhou on 1/17/2017.
 */
public class ReminderAlarmReceiver extends BroadcastReceiver{

    public static final String REMINDER_TEXT ="REMINDER_TEXT" ;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        String reminderText =intent.getStringExtra(REMINDER_TEXT);
        Intent intentAction =new Intent(context,ReminderActivity.class);
        PendingIntent pi =PendingIntent.getActivities(context,0, new Intent[]{intentAction},0);
        Notification notification =new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_action_name)
                .setTicker("Reminder")
                .setWhen(new Date().getTime())
                .setContentText(reminderText)
                .setContentIntent(pi)
                .build();
        NotificationManager notificationManager =(NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);
    }
}
