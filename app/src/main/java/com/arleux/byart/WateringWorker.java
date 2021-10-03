package com.arleux.byart;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WateringWorker extends Worker {
    private static final String CHANNEL_ID = "channel_id";

    public WateringWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        System.out.println("Qwerty:");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = null;

        builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_report_image) //иконка
                .setContentTitle("title") //заголовок
                .setContentText("text") //текст
                .setAutoCancel(true); //оповещение при нажатии будет удаляться с выдвижной панели оповещений

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "WateringService", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(0, builder.build());

        return Result.success();
    }
}
