package com.matheus.jokenpo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class AlarmReceiever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        View view = View.inflate(context.getApplicationContext(), R.layout.activity_main, null);
        PersonalNotification.criaNotificacao("AlarmReciever", "Teste AlarmReciever", view);
    }
}
