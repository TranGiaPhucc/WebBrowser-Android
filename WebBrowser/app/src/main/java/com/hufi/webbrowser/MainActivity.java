package com.hufi.webbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView txtUrl;
    Button btnGo, btnForward;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView=(WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        //webView.loadUrl("https://google.com");

        txtUrl=findViewById(R.id.txtUrl);
        btnGo=findViewById(R.id.btnGo);
        btnForward=findViewById(R.id.btnForward);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url=txtUrl.getText().toString().replace(" ","");
                webView.loadUrl(url);
                //txtUrl.setText("");
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoForward())
                    webView.goForward();
            }
        });

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                // Here you can check your new URL.
                Log.e("URL", url);
                txtUrl.setText(url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                // Here you can check your new URL.
                Log.e("URL", url);
                txtUrl.setText(url);
                super.onPageFinished(view, url);
            }
        });
    }


    @Override
    public void onBackPressed() {
        WebView webView = (WebView) findViewById(R.id.webView);
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }
}