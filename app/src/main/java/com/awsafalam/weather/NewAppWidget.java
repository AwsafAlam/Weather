package com.awsafalam.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    @Override
    public void onDisabled(Context context) {
        Toast.makeText(context, "Widget Disabled", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Toast.makeText(context, "Widget Enabled", Toast.LENGTH_SHORT).show();
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 3 seconds
        // Setting alarm manager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , pi);
//        }
//        else {
//            if(Build.VERSION.SDK_INT >= 19) {
//                am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , pi);
//            } else {
//                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , 60000 , pi);
//            }
//        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && Build.VERSION.SDK_INT >= 19) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , pi);
            }
        }
        else {
                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() , 60000 , pi);

        }





    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context,
                NewAppWidget.class);

        for (int widgetId : appWidgetManager.getAppWidgetIds(thisWidget)) {

            SharedPreferences pref = context.getApplicationContext().getSharedPreferences("MyPref", 0);
//            SharedPreferences.Editor editor = pref.edit();
            int temp =pref.getInt("temp", 0);
            //Get the remote views
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            // Set the text with the current time.
            remoteViews.setTextViewText(R.id.Time, Utility.getCurrentTime("hh:mm"));
            remoteViews.setTextViewText(R.id.Date , Utility.getCurrentTime("EEE, MMM d"));
            remoteViews.setTextViewText(R.id.am_pm , Utility.getCurrentTime("aaa"));
            remoteViews.setTextViewText(R.id.temperature , String.valueOf(temp)+"℃");

            Intent openApp = new Intent(context , MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context , 0 , openApp , 0);

            remoteViews.setOnClickPendingIntent(R.id.Widget_main , pendingIntent);



            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager, int appWidgetId,
                                          Bundle newOptions) {
        //Do some operation here, once you see that the widget has change its size or position.
    //    Toast.makeText(context, "SIze changed - onAppWidgetOptionsChanged() called", Toast.LENGTH_SHORT).show();
    }

/*
    public static final String CLOCK_WIDGET_UPDATE = "com.awsafalam.weather.NewAppWidget.CLOCK_WIDGET_UPDATE";

    private PendingIntent createupdateIntent(Context context){
        Intent intent = new Intent(CLOCK_WIDGET_UPDATE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

     void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

         CharSequence currentTime = new SimpleDateFormat("hh:mm").format(new Date());
         CharSequence currentDate= new SimpleDateFormat("EEE, MMM d").format(new Date());
         CharSequence am_pm = new SimpleDateFormat("aaa").format(new Date());

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.Time, currentTime);
         views.setTextViewText(R.id.Date , currentDate);
         views.setTextViewText(R.id.am_pm , am_pm);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND , 1);
        //Update clock per second interval
        alarmManager.setRepeating(AlarmManager.RTC , calendar.getTimeInMillis() , 1000 , createupdateIntent(context));

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createupdateIntent(context));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //start service if clock widget update is received
        if(CLOCK_WIDGET_UPDATE.equals(intent.getAction())){
            context.startService(new Intent(context , UpdateService.class));
        }

    }
    */
}

class UpdateService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        RemoteViews updateViews = builUpdate(this);
        ComponentName widget = new ComponentName(this , NewAppWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(widget , updateViews);


    }

    private RemoteViews builUpdate(Context context){
     //We make view for clock widget
        RemoteViews updateViews = new RemoteViews(context.getPackageName() , R.layout.new_app_widget);
        //clock data
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        updateViews.setTextViewText(R.id.Time , String.valueOf(calendar.get(Calendar.HOUR)));


        return updateViews;
    }


}

//For faster alarms
/*
private void setAlarm(long triggerTime, PendingIntent pendingIntent) {
        int ALARM_TYPE = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(ALARM_TYPE, triggerTime, pendingIntent);
        } else {
            alarmManager.set(ALARM_TYPE, triggerTime, pendingIntent);
        }
    }
triggerTime is calculated SystemClock.elapsedRealtime() + 600_000;

When alarm fires, firstly I plan new one, only after that I run my sheduled task.

setAlarm();
mySheduledTask;
I do have WAKE_LOCK permission in my manifest.

When I test this on Android 4 - it works perfect (deviation might be 12-15 milliseconds).

But when I run app on Xiaomi Redmi Note 3 Pro (5.1.1) - deviation can be up to 15 seconds!

For example, I see in my log file: first run was at 1467119934477 (of RTC time), second - at 1467120541683. Difference is 607_206 milliseconds, not 600_000, as it was planned!
 */