package com.matheus.jokenpo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.matheus.jokenpo.database.AppDatabase;
import com.matheus.jokenpo.model.Placar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<String> tiposJogadas;
    private final String[][] regras;
    private final JSONObject message;
    private AppDatabase db;
    private Boolean select;
    private TextView contadorHumano;
    private TextView contadorComputador;
    private ImageView imagem;
    private TextView resultado;
    private Spinner spinner;
    private Button zerar;
    private Integer hum;
    private Integer pc;
    private Long initialTime;
    private Integer firstTouch;

    public MainActivity() throws JSONException {
        tiposJogadas = Arrays.asList("pedra", "papel", "tesoura");
        regras = new String[][]{{"e", "d", "v"}, {"v", "e", "d"}, {"d", "v", "e"}};
        message = new JSONObject("{ \"e\": \"Empatou!\", \"v\": \"Parabéns, você venceu!\",\"d\": \"Você foi derrotado!\"}");
        firstTouch = hum = pc = 0;
        initialTime = 0L;
        db = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        contadorHumano = findViewById(R.id.textView3);
        contadorComputador = findViewById(R.id.textView4);
        imagem = findViewById(R.id.imageView);
        resultado = findViewById(R.id.textView7);
        zerar = findViewById(R.id.button2);
        spinner = findViewById(R.id.spinner);
        mostrarConfigs();
    }

    public void alertConfirm(View view) {
        new AlertDialog.Builder(view.getContext())
                .setMessage(R.string.zerar_placar)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        zerarContador();
                    }
                })
                .setNegativeButton(R.string.nao, null)
                .show();
    }

    public void zerarContador() {
        firstTouch = hum = pc = 0;
        initialTime = 0L;
        contadorHumano.setText(String.valueOf(hum));
        contadorComputador.setText(String.valueOf(pc));
        imagem.setImageDrawable(null);
        resultado.setText(R.string.selecione);
        spinner.setSelection(0);
        limparBordas();
    }

    private void limparBordas() {
        for (int i = 1; i <= 3; i++) {
            int id = getResources().getIdentifier("button_" + i, "id", getPackageName());
            ImageButton imageButton = findViewById(id);
            imageButton.setSelected(false);
        }
    }

    public void escolhaHumano(View view) throws JSONException {
        ++firstTouch;
        if (firstTouch == 1) initialTime = System.currentTimeMillis();

        List<String> listaSorteada = Arrays.asList("pedra", "papel", "tesoura");
        Collections.shuffle(listaSorteada);
        if (select == false) setImage(listaSorteada.get(1));

        ImageButton imageButton = (ImageButton) view;
        resultado(select == false ? listaSorteada.get(1) : spinner.getSelectedItem().toString(),
                imageButton.getContentDescription().toString());
        imageButton.setSelected(true);
        verificaSeTemBordaAtiva(imageButton);
    }

    public void setImage(String image) {
        Integer idImage = getResources().getIdentifier(image, "drawable", getPackageName());
        imagem.setImageResource(idImage);
    }

    private void resultado(String pce, String human) throws JSONException {
        String result = message.get(regras[tiposJogadas.indexOf(human)][tiposJogadas.indexOf(pce)]).toString();
        resultado.setText(result);
        acrescentaContador(result);
    }

    private void acrescentaContador(String resultado) {
        if (resultado.contains("derrotado")) contadorComputador.setText(String.valueOf(++pc));
        else if (resultado.contains("venceu")) contadorHumano.setText(String.valueOf(++hum));
        if (hum == 5) {
            redirectScreen("humano");
        } else if (pc == 5) {
            redirectScreen("pc");
        }
    }

    private void verificaSeTemBordaAtiva(ImageButton imageButton) {
        for (int i = 1; i <= 3; i++) {
            int id = getResources().getIdentifier("button_" + i, "id", getPackageName());
            if (id != imageButton.getId()) {
                ImageButton otherButton = findViewById(id);
                otherButton.setSelected(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.placar) {
            redirectScreen("menu");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void redirectScreen(String tipo) {
        String human = "";
        if (!tipo.equals("menu")) {
            String winner = hum == 5 ? "humano" : "Computador";
            if (winner.equals("Computador"))
                db.placarDao().insert(new Placar(winner, hum, pc,
                        (System.currentTimeMillis() - initialTime) / 1000d));
            else
                human = new Placar(winner, hum, pc,
                        (System.currentTimeMillis() - initialTime) / 1000d).toString();
            zerarContador();
        }
        Intent intent = new Intent(this, Placares.class);
        intent.putExtra("hum", human);
        startActivity(intent);
    }

    public void mostrarConfigs() {
        List<String> array = new ArrayList<>();
        array.add("Aletório");
        array.addAll(tiposJogadas);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                array
        );
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                String selected = spinner.getSelectedItem().toString();
                if (!selected.contains("Aletório")) {
                    setImage(selected);
                    select = true;
                } else {
                    select = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
}