package com.matheus.jokenpo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.firebase.firestore.ListenerRegistration;

public class AlarmReciever extends BroadcastReceiver {

    private ListenerRegistration listenerRegistration;

    @Override
    public void onReceive(Context context, Intent intent) {
        View view = View.inflate(context.getApplicationContext(), R.layout.activity_main, null);
        listenerRegistration = Firebase.getChagePlacares(view);
    }
}
