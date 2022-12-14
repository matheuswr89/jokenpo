package com.matheus.jokenpo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QuerySnapshot;
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
    private Boolean select;
    private TextView contadorHumano;
    private TextView contadorComputador;
    private ImageView imagem;
    private TextView resultado;
    private Spinner spinner;
    private Integer hum;
    private Integer pc;
    private Long initialTime;
    private Integer firstTouch;
    private MediaPlayer mp;
    private String nome;
    private EditText input;
    private Integer requestCode = 0;

    public MainActivity() throws JSONException {
        tiposJogadas = Arrays.asList("pedra", "papel", "tesoura");
        regras = new String[][]{{"e", "d", "v"}, {"v", "e", "d"}, {"d", "v", "e"}};
        message = new JSONObject("{ \"e\": \"Empatou!\", \"v\": \"Parab??ns, voc?? venceu!\",\"d\": \"Voc?? foi derrotado!\"}");
        firstTouch = hum = pc = 0;
        initialTime = 0L;
        nome = "";
        mp = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createChannel();
        getNewNotification();
        if (nome.isEmpty()) exibirMensagemEdt();
        contadorHumano = findViewById(R.id.textView3);
        contadorComputador = findViewById(R.id.textView4);
        imagem = findViewById(R.id.imageView);
        resultado = findViewById(R.id.textView7);
        spinner = findViewById(R.id.spinner);
        mostrarConfigs();

        Firebase.getLastPlacar().addOnCompleteListener(task -> {
            QuerySnapshot result = task.getResult();
            if (result.size() > 0) {
                Placar placar = new Placar(result.getDocuments().get(0).getData());
                hum = placar.getHum();
                pc = placar.getPc();
                contadorComputador.setText(pc+"");
                contadorHumano.setText(hum+"");
            }
        });
    }

    public void alertConfirm(View view) {
        new AlertDialog.Builder(view.getContext()).setMessage(R.string.zerar_placar)
                .setPositiveButton(R.string.sim, (dialog, id) -> zerarContador())
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
        if (!select) setImage(listaSorteada.get(1));

        ImageButton imageButton = (ImageButton) view;
        resultado(!select ? listaSorteada.get(1) : spinner.getSelectedItem().toString(),
                imageButton.getContentDescription().toString());
        imageButton.setSelected(true);
        verificaSeTemBordaAtiva(imageButton);
    }

    public void setImage(String image) {
        int idImage = getResources().getIdentifier(image, "drawable", getPackageName());
        imagem.setImageResource(idImage);
    }

    private void resultado(String pce, String human) throws JSONException {
        String result = message.get(regras[tiposJogadas.indexOf(human)][tiposJogadas.indexOf(pce)]).toString();
        resultado.setText(result);
        acrescentaContador(result);
    }

    private void acrescentaContador(String resultado) {
        if (resultado.contains("derrotado") && pc < 5)
            contadorComputador.setText(String.valueOf(++pc));
        if (resultado.contains("venceu") && hum < 5) contadorHumano.setText(String.valueOf(++hum));

        Placar placar = new Placar(nome, hum, pc,
                (System.currentTimeMillis() - initialTime) / 1000d,
                Timestamp.now());

        Firebase.adicionaPlacar(placar, "currentPlacar");

        if (hum == 5) {
            criaAlert("Parab??ns, voc?? ganhou!", R.raw.somdesucesso, "Humano");
        }
        if (pc == 5) {
            criaAlert("Que pena, voc?? perdeu!", R.raw.somdefalha, "Computador");
        }
        releasePlayer();
        mp = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_NOTIFICATION_URI);
        mp.start();
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
            redirectScreen("Menu");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void redirectScreen(String tipo) {
        if (!tipo.equals("Menu")) {
            String winner = tipo.contains("Com") ? tipo : nome;
            Placar placar = new Placar(winner, hum, pc,
                    (System.currentTimeMillis() - initialTime) / 1000d,
                    Timestamp.now());

            Firebase.adicionaPlacar(placar, "placares");
            zerarContador();
        }
        Intent intent = new Intent(this, Placares.class);
        intent.putExtra("winner", tipo);
        startActivity(intent);
    }

    public void mostrarConfigs() {
        List<String> array = new ArrayList<>();
        array.add("Alet??rio");
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
                if (!selected.contains("Alet??rio")) {
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

    private void releasePlayer() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public void criaAlert(String titulo, int som, String tipo) {
        releasePlayer();
        mp = MediaPlayer.create(getApplicationContext(), som);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(titulo)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> redirectScreen(tipo))
                .setCancelable(false).show();
        mp.start();
    }

    public void exibirMensagemEdt() {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(MainActivity.this);
        mensagem.setTitle("Forne??a o seu nome:");

        input = new EditText(this);
        mensagem.setView(input);
        mensagem.setPositiveButton("OK", (dialog, which) -> {
            nome = input.getText().toString();
            if (nome.isEmpty()) exibirMensagemEdt();
            Firebase.getUniquePlacar(nome, getApplicationContext());
        });
        mensagem.setCancelable(false);
        mensagem.show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "notific";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void getNewNotification(){
        Intent intent = new Intent(this, AlarmReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, requestCode++, intent, PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60*1000, alarmIntent);
        }
    }
}