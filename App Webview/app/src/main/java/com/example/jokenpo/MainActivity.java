package com.example.jokenpo;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Configura a WebView
        webView = findViewById(R.id.webView);
        webView.setVisibility(View.INVISIBLE);
        webView.setWebChromeClient(new WebChromeClient());
        // Habilita o JS
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Garante que usará a WebView e não o navegador padrão
        webView.setWebViewClient(new WebViewClient() {
            // Callback que determina quando terminou de ser carregada a
            // WebView, para trocarmos a imagem de carregamento por ela
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        // Associa a interface (a ser definida abaixo) e carrega o HTML
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    public class WebAppInterface {
        MainActivity mainActivity;
        public WebAppInterface(MainActivity activity) {
            this.mainActivity = activity;
        }
        @JavascriptInterface
        public void androidToast(String msg) {
            Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT).show();
            // Chama uma função do JavaScript
            runJavaScript("mostraNome();");
        }
    }
    // Executa um comando JavaScript
    public void runJavaScript(final String jsCode) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(jsCode, null);
            }
        });
    }
}