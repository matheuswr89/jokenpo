package com.matheus.jokenpo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.ListenerRegistration;

public class Placares extends AppCompatActivity {

    private ListView listView;
    private TextView textView;
    private Button buttonZerar;
    private TextView textViewLeg;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placares);

        buttonZerar = findViewById(R.id.buttonZerar);
        textView = findViewById(R.id.textView9);
        textViewLeg = findViewById(R.id.textView9);
        listView = findViewById(R.id.lista);
        mostrarLista();
    }

    public void mostrarLista() {
        textView.setText(R.string.obs);
        buttonZerar.setVisibility(Button.VISIBLE);
        setListAdapter();
        textViewLeg.setVisibility(View.VISIBLE);
    }

    private void setListAdapter() {
        listener = Firebase.getPlacares(this, textView, buttonZerar, listView);
    }

    public void zerarPlacar(View view) {
        new AlertDialog.Builder(view.getContext())
                .setMessage(R.string.deletar)
                .setPositiveButton(R.string.sim, (dialog, id) -> {
                    Firebase.apagaPlacares();
                    listView.setAdapter(null);
                    textView.setText(R.string.placar_vazio);
                    buttonZerar.setVisibility(Button.INVISIBLE);
                })
                .setNegativeButton(R.string.nao, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listener.remove();
    }
}
