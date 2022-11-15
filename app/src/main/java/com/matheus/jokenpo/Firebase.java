package com.matheus.jokenpo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.matheus.jokenpo.model.Placar;

import java.util.ArrayList;
import java.util.List;

public class Firebase {
    private static final String TAG = "Firebase Action";
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void adicionaPlacar(Placar placar) {
        db.collection("placares")
                .add(placar)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "DocumentSnapshot added: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static ListenerRegistration getPlacares(Context context, TextView textView, Button buttonZerar, ListView listView) {
        return db.collection("placares")
                .orderBy("hum", Direction.ASCENDING)
                .orderBy("pc", Direction.ASCENDING)
                .orderBy("duration", Direction.ASCENDING)
                .addSnapshotListener(
                        (value, e) -> {
                            if (e != null) {
                                Log.d(TAG, "Listen failed.", e);
                                return;
                            }

                            List<Placar> placares = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                placares.add(new Placar(doc.getData()));
                            }
                            if (placares.size() == 0) {
                                textView.setText(R.string.placar_vazio);
                                buttonZerar.setVisibility(Button.INVISIBLE);
                            }
                            ArrayAdapter<Placar> adapter = new ArrayAdapter(
                                    context,
                                    android.R.layout.simple_list_item_1,
                                    placares
                            );
                            listView.setAdapter(adapter);
                        });

    }

    public static void getUniquePlacar(String name, Context context) {
        db.collection("placares")
                .whereEqualTo("winner", name)
                .orderBy("timestamp", Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                        Placar placar = new Placar(task.getResult().getDocuments().get(0).getData());
                        db.collection("placares")
                                .whereGreaterThan("timestamp", placar.getTimestamp())
                                .orderBy("timestamp", Direction.ASCENDING)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task1.getResult()) {
                                            Placar placar1 = new Placar(document.getData());
                                            if (!placar1.getWinner().equals(placar.getWinner())) {
                                                String titulo = String.format("O jogador %s, passou você no ranking!", placar1.getWinner());
                                                View view = View.inflate(context, R.layout.activity_main, null);
                                                criaNotificacao(titulo, view);
                                                return;
                                            }
                                        }
                                    }
                                });
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }

    public static void apagaPlacares() {
        db.collection("placares").get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                db.collection("placares").document(queryDocumentSnapshot.getId()).delete();
            }
        });
    }

    public static void criaNotificacao(String titulo, View view) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(view.getContext(), "1")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(titulo)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX);
        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, buildNotification);
    }
}