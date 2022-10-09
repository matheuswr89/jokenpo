package com.matheus.jokenpo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.matheus.jokenpo.database.AppDatabase;
import com.matheus.jokenpo.model.Placar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Placares extends AppCompatActivity {

    private EditText editTextTextPersonName;
    private AppDatabase db;
    private ListView listView;
    private Button button;
    private TextView textView;
    private Button buttonZerar;
    private TextView textViewLeg;
    private String valor = "";
    private List<Placar> lista;

    public static void hideKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placares);

        db = AppDatabase.getInstance(this);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        button = findViewById(R.id.buttonLista);
        buttonZerar = findViewById(R.id.buttonZerar);
        textView = findViewById(R.id.textView9);
        textViewLeg = findViewById(R.id.textView9);
        valor = getIntent().getStringExtra("hum");
        envioComEnter();
        listView = findViewById(R.id.lista);
        lista = db.placarDao().getAll();
        if (valor.equals("")) {
            editTextTextPersonName.setVisibility(EditText.INVISIBLE);
            button.setVisibility(Button.INVISIBLE);
            setListAdapter(lista);
            buttonZerar.setVisibility(Button.INVISIBLE);
            if (lista.size() >= 1) buttonZerar.setVisibility(Button.VISIBLE);
        } else {
            textViewLeg.setVisibility(View.INVISIBLE);
            buttonZerar.setVisibility(Button.INVISIBLE);
        }
        if (lista.size() == 0) textView.setText("Nenhum placar por aqui!");
    }

    public void mostrarLista(View view) {
        textView.setText(R.string.obs);
        buttonZerar.setVisibility(Button.VISIBLE);
        if (!editTextTextPersonName.getText().toString().isEmpty()) {
            hideKeyboard(view.getContext(), view);
            Button button = (Button) view;
            String[] array = valor.split(" - ");

            db.placarDao().insert(new Placar(
                    editTextTextPersonName.getText().toString(),
                    Integer.parseInt(array[1]),
                    Integer.parseInt(array[2]),
                    Double.parseDouble(array[3].replace(" s", ""))
            ));
            editTextTextPersonName.setVisibility(EditText.INVISIBLE);
            button.setVisibility(Button.INVISIBLE);

            setListAdapter(db.placarDao().getAll());
        } else {
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Campo vazio")
                    .setMessage("Não pode ter um campo vazio!")
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void setListAdapter(List<Placar> lista) {
        Collections.sort(lista);
        textViewLeg.setVisibility(View.VISIBLE);
        ArrayAdapter<Placar> adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                lista
        );
        listView.setAdapter(adapter);
    }

    private void envioComEnter() {
        editTextTextPersonName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                        mostrarLista(button);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void zerarPlacar(View view) {
        new AlertDialog.Builder(view.getContext())
                .setMessage(R.string.deletar)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.placarDao().nukeTable();
                        setListAdapter(new ArrayList<>());
                        textView.setText("Nenhum placar por aqui!");
                        buttonZerar.setVisibility(Button.INVISIBLE);
                    }
                })
                .setNegativeButton(R.string.nao, null)
                .show();
    }
}
