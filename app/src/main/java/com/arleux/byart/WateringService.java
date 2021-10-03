package com.arleux.byart;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

public class WateringService extends Service {
    private static final String CHANNEL_ID = "channel_id";
    private static final long POLL_INTERVAL_MS = TimeUnit.HOURS.toMillis(24);
    private NotificationManager notificationManager;
    private Notification.Builder builder;

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, WateringService.class);
        return intent;
    }

    @Override
    public void onCreate(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        stopSelf(); //чтобы остановить службу
        return Service.START_REDELIVER_INTENT; // возвращаемое значение определяет поведение службы при завершении: в данном случае если служба была остановлена системой преждевременно
        //то служба попытается запуститься позднее, когда ресурсы не будут столь ограничены
    }
    @Override
    public void onDestroy(){
        notificationManager.cancel(1);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showNotification(){
        if (Build.VERSION.SDK_INT>25){
            builder = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_menu_report_image) //иконка
                    .setContentTitle("title") //заголовок
                    .setContentText("text") //текст
                    .setAutoCancel(true); //оповещение при нажатии будет удаляться с выдвижной панели оповещений

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "WateringService", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        else {
            builder = new Notification.Builder(this)
                    .setSmallIcon(android.R.drawable.ic_menu_report_image) //иконка
                    .setContentTitle("title") //заголовок
                    .setContentText("text") //текст
                    .setAutoCancel(true); //оповещение при нажатии будет удаляться с выдвижной панели оповещений
        }
        notificationManager.notify(0, builder.build());
    }
    public static void setServiceAlarm(Context context){
        Intent intent = WateringService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3000, pi);
        /*alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                POLL_INTERVAL_MS, pi);
         */
    }
}
