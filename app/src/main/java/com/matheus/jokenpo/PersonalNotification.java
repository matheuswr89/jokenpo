package com.matheus.jokenpo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.view.View;

import androidx.core.app.NotificationCompat;

public class PersonalNotification {
    public static Integer id = 0;
    public static void criaNotificacao(String titulo, String message, View view) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(view.getContext(), "1")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(titulo)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX);
        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(id++, buildNotification);
    }
}

