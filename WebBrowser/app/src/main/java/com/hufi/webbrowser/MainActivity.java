package com.hufi.webbrowser;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.PictureInPictureParams;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout refreshLayout;
    FrameLayout customViewContainer;
    EditText txtUrl;
    TextView txtMemory, txtAdblock, txtScroll;
    CheckBox cbxAd, cbxInternetSpeedMeter;
    Button btnGo, btnBack, btnForward, btnGoogle, btnYoutube;
    ImageButton btnReload, btnMaps, btnPhoneDesktop, btnHistory, btnBookmark, btnBookmarkCheck, btnQRCode, btnMicrophone, btnCopy, btnPaste, btnFTP;
    ImageView imgInternetConnection, imgWebIcon;
    ListView listUrl;
    ArrayList<History> arrayList;
    Spinner spnSearch;
    ProgressBar prgBar;
    Database db;
    //SQL sql;
    private List<String> list;
    private WebView webView;
    private String search = "";

    private String urlNow = "";

    boolean listUrlVisible = false;

    //boolean isRedirected = false;
    boolean isLoaded = false;

    boolean adCheck = false;
    int adCount = 0;
    int linkCount = 0;

    long uploadSpeedGlobal = 0;
    long downloadSpeedGlobal = 0;

    //Upload
    public Context context;

    //private static final String TAG = MainActivity.class.getSimpleName();

    /*private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;

    // the same for Android 5.0 methods only
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;*/

    private final static int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri[]> mUploadMessage;
    private final int REQUEST_CODE = 103;

    private Handler mHandler = new Handler();
    //private long mStartRX = 0;
    //private long mStartTX = 0;
    //private long txBytes = 0;
    //private long rxBytes = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My notification", "My notification", NotificationManager.IMPORTANCE_LOW);
            channel.setVibrationPattern(new long[]{ 0 });
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }*/

        /*mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();

        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 0);
        }*/

        try {
            AdBlocker.loadFromAssets(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("internet-speed"));

        mHandler.postDelayed(mRunnable, 1000);

        //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        db = new Database(MainActivity.this);
        db.createTable();

        if (!db.checkUserNameExist("admin")) {
            NguoiDung nd = new NguoiDung("admin", "admin", "Trần Gia Phúc", 0);
            db.insert(nd);
        }

        NguoiDung n = db.getNguoiDung("admin");
        n.webcount = db.countHistory();
        db.update(n);

        Toast.makeText(MainActivity.this, "Bạn đã duyệt " + db.countHistory() + " trang web.", Toast.LENGTH_SHORT).show();

        webView=findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        registerForContextMenu(webView);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        //webView.getSettings().setAppCacheMaxSize( 1024 * 1024 * 1024 ); // 1GB (default: 5MB)
        //webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);       //LOAD_DEFAULT     //LOAD_NO_CACHE
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //webView.getSettings().setUserAgentString("Web/5.0 (Linux; U; Android 14)");     //System.getProperty("http.agent")        //webView.getSettings().getUserAgentString()
        //"WebView/2.1.0 (Android 14; Pixel 4 XL Build/QQS1.190604.005)"
        //webView.loadUrl("https://www.google.com");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        /*if (Build.VERSION.SDK_INT >= 11){
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);       //LAYER_TYPE_NONE
        }*/

        if (!CheckConnection.haveNetworkConnection(getApplicationContext())) {
            CheckConnection.ShowToast_Short(getApplicationContext(), "Không có kết nối mạng.");
            //stopService(new Intent(this, InternetSpeedMeter.class));
            //mHandler.removeCallbacks(mRunnable);
            //finish();
        }
        else {
            if (!isMyServiceRunning(InternetSpeedMeter.class))
            {
                startService(new Intent(this, InternetSpeedMeter.class));
            }
        }

        if (db.isHistoryEmpty() == true)
        {
            webView.loadUrl("https://www.google.com");
        }
        else {
            String urlLast = db.getLastUrl();
            webView.loadUrl(urlLast);
        }

        customViewContainer=findViewById(R.id.customViewContainer);
        imgWebIcon=findViewById(R.id.imgWebIcon);
        imgInternetConnection=findViewById(R.id.imgInternetConnection);
        cbxInternetSpeedMeter=findViewById(R.id.cbxInternetSpeedMeter);
        cbxAd=findViewById(R.id.cbxAd);
        txtScroll=findViewById(R.id.txtScroll);
        txtAdblock=findViewById(R.id.txtAdblock);
        txtMemory=findViewById(R.id.txtMemory);
        txtUrl=findViewById(R.id.txtUrl);
        listUrl=findViewById(R.id.listUrl);
        btnQRCode=findViewById(R.id.btnQRCode);
        btnReload=findViewById(R.id.btnReload);
        btnGo=findViewById(R.id.btnGo);
        btnBack=findViewById(R.id.btnBack);
        btnForward=findViewById(R.id.btnForward);
        btnGoogle=findViewById(R.id.btnGoogle);
        btnYoutube=findViewById(R.id.btnYoutube);
        btnFTP=findViewById(R.id.btnFTP);
        btnMicrophone=findViewById(R.id.btnMicrophone);
        btnCopy=findViewById(R.id.btnCopy);
        btnPaste=findViewById(R.id.btnPaste);
        btnMaps=findViewById(R.id.btnMaps);
        btnPhoneDesktop=findViewById(R.id.btnPhoneDesktop);
        btnHistory=findViewById(R.id.btnHistory);
        btnBookmark=findViewById(R.id.btnBookmark);
        btnBookmarkCheck=findViewById(R.id.btnBookmarkCheck);
        prgBar=findViewById(R.id.prgBar);
        spnSearch=findViewById(R.id.spnSearch);
        refreshLayout=findViewById(R.id.refreshLayout);

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (!isMyServiceRunning(InternetSpeedMeter.class))
        {
            cbxInternetSpeedMeter.setChecked(false);
        }
        else
            cbxInternetSpeedMeter.setChecked(true);

        cbxInternetSpeedMeter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (!isMyServiceRunning(InternetSpeedMeter.class))
                        startService(new Intent(MainActivity.this, InternetSpeedMeter.class));
                }
                else
                    stopService(new Intent(MainActivity.this, InternetSpeedMeter.class));
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh () {
                //webView.reload();
                //refreshLayout.setRefreshing(false);

                refreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        webView.reload();
                    }
                },0);
            }
        });

        refreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark)
        );

        // Only enable swipe to refresh if the `WebView` is scrolled to the top.
        webView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                txtScroll.setText("S: " + webView.getScrollY() + "/" + ((int) Math.floor(webView.getContentHeight() * webView.getScale() - webView.getHeight())));    //((int) Math.floor(webView.getContentHeight() * webView.getScale() - webView.getHeight()))
                //if (webView.getUrl().contains("youtube.com")) {
                if (!webView.canScrollVertically(1)) {   //1 down -1 up
                    refreshLayout.setEnabled(false);
                }
                else if (webView.getScrollY() == 0) {
                    refreshLayout.setEnabled(true);
                } else {
                    refreshLayout.setEnabled(false);
                }
            }
        });

        txtUrl.setFocusableInTouchMode(true);
        txtUrl.clearFocus();
        listUrl.setVisibility(View.GONE);
        webView.requestFocus();
        //txtUrl.setCursorVisible(false);

        list = new ArrayList<>();
        list.add("Web");
        list.add("Google");
        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
        spnSearch.setAdapter(spinnerAdapter);

        spnSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();

                search = list.get(i);
                txtUrl.setText("");
                if (search.equals("Web")) {
                    txtUrl.setHint("https://www.google.com");
                }
                else {
                    txtUrl.setHint("Google Search");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();

                search = "Web";
                txtUrl.setText("");
                txtUrl.setHint("https://www.google.com");
            }
        });

        cbxAd.setChecked(false);
        cbxAd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    adCheck = true;
                    Toast.makeText(MainActivity.this, "Ads enabled.", Toast.LENGTH_SHORT).show();
                }
                else {
                    adCheck = false;
                    Toast.makeText(MainActivity.this, "Ads disabled.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("url", txtUrl.getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(MainActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });

        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard.hasPrimaryClip()) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    txtUrl.setText(item.getText().toString());

                    Toast.makeText(MainActivity.this, "Pasted!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnFTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FTPActivity.class);
                startActivity(intent);
            }
        });

        btnMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.RECORD_AUDIO};
                    requestPermissions(permissions, 1);
                } else {
                    btnMicrophone.setBackgroundResource(android.R.drawable.presence_audio_online);

                    Toast.makeText(getApplicationContext(), "Hãy nói gì đó.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.domain.app");

                    SpeechRecognizer recognizer = SpeechRecognizer
                            .createSpeechRecognizer(MainActivity.this);
                    RecognitionListener listener = new RecognitionListener() {
                        @Override
                        public void onResults(Bundle results) {
                            ArrayList<String> voiceResults = results
                                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                            if (voiceResults == null) {
                                System.out.println("No voice results");
                            } else {
                                System.out.println("Printing matches: ");
                                for (String match : voiceResults) {
                                    System.out.println(match);
                                    //Toast.makeText(getApplicationContext(), match, Toast.LENGTH_SHORT).show();
                                    //webView.loadUrl(match);
                                    txtUrl.setText(match);
                                    btnGo.performClick();
                                    //t1.speak(match, TextToSpeech.QUEUE_FLUSH, null);
                                    btnMicrophone.setBackgroundResource(android.R.drawable.presence_audio_busy);
                                }
                            }
                        }

                        @Override
                        public void onReadyForSpeech(Bundle params) {
                            System.out.println("Ready for speech");
                        }

                        /**
                         * ERROR_NETWORK_TIMEOUT = 1;
                         * ERROR_NETWORK = 2;
                         * ERROR_AUDIO = 3;
                         * ERROR_SERVER = 4;
                         * ERROR_CLIENT = 5;
                         * ERROR_SPEECH_TIMEOUT = 6;
                         * ERROR_NO_MATCH = 7;
                         * ERROR_RECOGNIZER_BUSY = 8;
                         * ERROR_INSUFFICIENT_PERMISSIONS = 9;
                         *
                         * @param error code is defined in SpeechRecognizer
                         */
                        @Override
                        public void onError(int error) {
                            System.err.println("Error listening for speech: " + error);
                            btnMicrophone.setBackgroundResource(android.R.drawable.presence_audio_away);
                        }

                        @Override
                        public void onBeginningOfSpeech() {
                            System.out.println("Speech starting");
                        }

                        @Override
                        public void onBufferReceived(byte[] buffer) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onEndOfSpeech() {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onEvent(int eventType, Bundle params) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onPartialResults(Bundle partialResults) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onRmsChanged(float rmsdB) {
                            // TODO Auto-generated method stub

                        }
                    };
                    recognizer.setRecognitionListener(listener);
                    recognizer.startListening(intent);
                }
            }
        });

        btnQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                    startActivityForResult(intent, 31);
                } catch (Exception e) {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);
                }*/
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        String[] permissions = {Manifest.permission.CAMERA};
                        requestPermissions(permissions, 1);
                    } else {
                        Intent intent = new Intent(MainActivity.this, QrCodeScanner.class);
                        startActivityForResult(intent, 31);
                    }
                }
            }
        });

        txtUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    txtUrl.clearFocus();
                    listUrl.setVisibility(View.GONE);
                    listUrlVisible = false;
                }
                else if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER)
                {
                    if (search.equals("Web"))
                    {
                        String url=txtUrl.getText().toString().replace(" ","");
                        if (Patterns.WEB_URL.matcher(url).matches())
                            webView.loadUrl(url);
                        else {
                            url=txtUrl.getText().toString().replace(" ","+");
                            webView.loadUrl("https://google.com/search?q=" + url);
                        }
                    }
                    else
                    {
                        String url=txtUrl.getText().toString().replace(" ","+");
                        webView.loadUrl("https://google.com/search?q=" + url);
                    }

                    //txtUrl.requestFocus();
                    //txtUrl.setCursorVisible(false);
                    txtUrl.clearFocus();
                    listUrl.setVisibility(View.GONE);
                    listUrlVisible = false;
                    webView.requestFocus();
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    return true;
                }
                return false;
            }
        });

        arrayList = new ArrayList<>();
        HistoryAdapter adapterUrl = new HistoryAdapter(this, R.layout.list_urlrecommend, arrayList);
        listUrl.setAdapter(adapterUrl);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!txtUrl.getText().toString().equals(urlNow))
                    txtUrl.setText(urlNow);

                if (txtUrl.isFocused()) {
                    txtUrl.clearFocus();
                    listUrl.setVisibility(View.GONE);
                    listUrlVisible = false;
                    webView.requestFocus();
                }
                return false;
            }
        });

        txtUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listUrl.getVisibility() == View.GONE) {
                    listUrl.setVisibility(View.VISIBLE);
                    listUrlVisible = true;

                    adapterUrl.clear();
                    arrayList.addAll(db.getUrlRecommend(txtUrl.getText().toString()));
                    adapterUrl.notifyDataSetChanged();
                }
            }
        });

        txtUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    txtUrl.clearFocus();
                    listUrl.setVisibility(View.GONE);
                    listUrlVisible = false;
                    webView.requestFocus();
                }
                else {
                    listUrl.setVisibility(View.VISIBLE);
                    listUrlVisible = true;

                    adapterUrl.clear();
                    arrayList.addAll(db.getUrlRecommend(txtUrl.getText().toString()));
                    adapterUrl.notifyDataSetChanged();
                }
            }
        });

        txtUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (search.equals("Web"))
                {
                    adapterUrl.clear();
                    arrayList.addAll(db.getUrlRecommend(txtUrl.getText().toString()));
                    adapterUrl.notifyDataSetChanged();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (search.equals("Web"))
                {
                    adapterUrl.clear();
                    arrayList.addAll(db.getUrlRecommend(txtUrl.getText().toString()));
                    adapterUrl.notifyDataSetChanged();
                }

            }
        });

        listUrl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = ((TextView) view.findViewById(R.id.lbUrl)).getText().toString();
                txtUrl.setText(url);

                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                webView.loadUrl(url);
            }
        });

        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();

                webView.reload();
            }
        });

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();

                Intent intent = new Intent(MainActivity.this, Maps.class);
                startActivity(intent);
            }
        });

        btnPhoneDesktop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();

                if (webView.getSettings().getUseWideViewPort() == false)
                    setDesktopMode(webView, true);
                else
                    setDesktopMode(webView, false);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();

                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();

                Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btnBookmarkCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = txtUrl.getText().toString();
                if (!db.checkBookmarkExist(url)) {
                    String title = db.getTitle(url);
                    Bookmark b = new Bookmark(url, title);
                    db.insertBookmark(b);
                    btnBookmarkCheck.setBackgroundResource(android.R.drawable.btn_star_big_on);
                    Toast.makeText(MainActivity.this,"Đã đánh dấu trang.",Toast.LENGTH_SHORT).show();
                }
                else {

                    db.deleteBookmark(url);
                    btnBookmarkCheck.setBackgroundResource(android.R.drawable.btn_star_big_off);
                    Toast.makeText(MainActivity.this,"Đã xoá dấu trang.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search.equals("Web"))
                {
                    String url=txtUrl.getText().toString().replace(" ","");
                    if (Patterns.WEB_URL.matcher(url).matches())
                        webView.loadUrl(url);
                    else {
                        url=txtUrl.getText().toString().replace(" ","+");
                        webView.loadUrl("https://google.com/search?q=" + url);
                    }
                }
                else
                {
                    String url=txtUrl.getText().toString().replace(" ","+");
                    webView.loadUrl("https://google.com/search?q=" + url);
                }
                //txtUrl.requestFocus();
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                //txtUrl.setCursorVisible(false);
                webView.requestFocus();
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView=findViewById(R.id.webView);
                if(webView.canGoBack()){
                    webView.goBack();
                }
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoForward())
                    webView.goForward();
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("https://www.google.com");
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();
            }
        });

        btnYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("https://www.youtube.com");
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                listUrlVisible = false;
                webView.requestFocus();
            }
        });

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View windowMain = inflater.inflate(R.layout.activity_main, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON|
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.END | Gravity.BOTTOM;
        params.x = 100;
        params.y = 100;
        params.width = 500;
        params.height = 400;

        //windowManager.addView(windowMain, params);
        //windowManager.removeView(windowMain);

        windowMain.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("AD","Action E" + event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("AD","Action Down");
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    /*case MotionEvent.ACTION_UP:
                        Log.d("AD","Action Up");
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;*/
                    case MotionEvent.ACTION_MOVE:
                        Log.d("AD","Action Move");
                        params.x = initialX - (int) (event.getRawX() - initialTouchX);
                        params.y = initialY - (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(windowMain, params);
                        return true;
                }
                return false;
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                // Here you can check your new URL.
                super.onPageStarted(view, url, favicon);
                //Log.e("URL", url);
                //txtUrl.setText(url);

                //if (!isRedirected) {
                    isLoaded = false;
                //}

                //isRedirected = false;

                prgBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Here you can check your new URL.
                super.onPageFinished(view, url);
                //Log.e("URL", url);

                if (prgBar.getProgress() < 100)
                    return;

                if (isLoaded)
                    return;

                isLoaded = true;
                prgBar.setVisibility(View.GONE);

                txtScroll.setText("S: " + view.getScrollY() + "/" + ((int) Math.floor(view.getContentHeight() * view.getScale() - view.getHeight())));

                //String urlCheck = txtUrl.getText().toString();
                urlNow = url;
                txtUrl.setText(urlNow);

                //if (!urlCheck.equals(txtUrl.getText().toString())) {
                //if (!isRedirected) {
                    String title = view.getTitle();
                    History h = new History(url, title);
                    db.insertHistory(h);

                    if (db.checkBookmarkExist(url)) {
                        btnBookmarkCheck.setBackgroundResource(android.R.drawable.btn_star_big_on);
                    } else {
                        btnBookmarkCheck.setBackgroundResource(android.R.drawable.btn_star_big_off);
                    }

                    AsyncTaskSQL runner = new AsyncTaskSQL();
                    runner.execute("History", url, title);

                    /*sql = new SQL();
                    if (sql.isConnected() == false) {
                        return;
                    }
                    sql.insertUrl(url);
                    v.vibrate(100);
                    try {
                        sql.Close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }*/
                //}

                capture();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                txtUrl.setText(url);

                //isRedirected = true;

                if (url.startsWith("intent://")) {
                    try {
                        Context context = view.getContext();
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                        if (intent != null) {
                            view.stopLoading();

                            PackageManager packageManager = context.getPackageManager();
                            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            if (info != null) {
                                context.startActivity(intent);
                            } else {
                                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                view.loadUrl(fallbackUrl);

                                // or call external broswer
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl));
//                    context.startActivity(browserIntent);
                            }

                            return true;
                        }
                    } catch (URISyntaxException e) {
                        /*if (GeneralData.DEBUG) {
                            Log.e(TAG, "Can't resolve intent://", e);
                        }*/
                    }
                }





                if (url.endsWith(".mp4")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                    return true;        //true
                }/* else
                    if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("smsto:")
                        || url.startsWith("mms:") || url.startsWith("mmsto:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                    return true;        //true
                }*/ else {
                    return super.shouldOverrideUrlLoading(view, url);
                    //return false;
                    //view.loadUrl(url);
                    //return false;
                }
            }

            private Map<String, Boolean> loadedUrls = new HashMap<>();
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (adCheck == true)
                    return super.shouldInterceptRequest(view, url);

                /*boolean ad;
                if (!loadedUrls.containsKey(url)) {
                    ad = AdBlocker.isAd(url);
                    //loadedUrls.put(url, ad);
                    //linkCount++;
                    if (ad) {
                        adCount++;
                        loadedUrls.put(url, ad);
                    }
                } else {
                    ad = loadedUrls.get(url);
                }*/

                boolean ad = AdBlocker.isAd(url);
                if (ad) {
                    AsyncTaskSQL runner = new AsyncTaskSQL();
                    runner.execute("Ad", url);

                    adCount++;
                }

                return ad ? AdBlocker.createEmptyResource() :
                        super.shouldInterceptRequest(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                prgBar.setProgress(progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);


            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, Message resultMsg) {

                WebView newWebView = new WebView(MainActivity.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setSupportMultipleWindows(true);
                //view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        webView.loadUrl(url);

                        return false;
                    }
                });

                //newWebView.destroy();

                return true;
            }

            /*@Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                //Required functionality here
                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                return super.onJsAlert(view, url, message, result);
            }*/

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);

                //imgWebIcon.setImageBitmap(icon);
                btnReload.setBackground(new BitmapDrawable(getResources(), icon));
            }

            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view,callback);
                //webView.setVisibility(View.GONE);
                customViewContainer.setVisibility(View.VISIBLE);
                customViewContainer.addView(view);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                ////getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                //windowManager.addView(windowMain, params);
            }

            public void onHideCustomView () {
                super.onHideCustomView();
                //webView.setVisibility(View.VISIBLE);
                customViewContainer.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                webView.requestFocus();

                //windowManager.removeView(windowMain);
            }

            // for Lollipop, all in one
            /*public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    // create the file where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }

                    // continue only if the file was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");       //image/*

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                return true;
            }

            // creating image files (Lollipop only)
            private File createImageFile() throws IOException {

                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere");

                if (!imageStorageDir.exists()) {
                    imageStorageDir.mkdirs();
                }

                // create an image file name
                imageStorageDir = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                return imageStorageDir;
            }

            // openFileChooser for Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;

                try {
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere");

                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs();
                    }

                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                    mCapturedImageURI = Uri.fromFile(file); // save to the private variable

                    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");        //image/*

                    Intent chooserIntent = Intent.createChooser(i, getString(R.string.image_chooser));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Camera Exception:" + e, Toast.LENGTH_LONG).show();
                }

            }

            // openFileChooser for Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // openFileChooser for other Android versions
            // may not work on KitKat due to lack of implementation of openFileChooser() or onShowFileChooser()
            // https://code.google.com/p/android/issues/detail?id=62220
            // however newer versions of KitKat fixed it on some devices
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }
        */
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(null);
                }

                mUploadMessage = filePathCallback;

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");

                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

                return true;
            }
        });


        //Download
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                Log.d("permission","permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,1);
            }
        }

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                /*DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie",cookies);
                request.addRequestHeader("User-Agent",userAgent);
                request.setDescription("Downloading file....");
                request.setTitle(URLUtil.guessFileName(url,contentDisposition,mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(),"Đang tải...",Toast.LENGTH_SHORT).show();      //Downloading File*/

                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                //request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie",cookies);
                request.addRequestHeader("User-Agent",userAgent);
                request.setDescription(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.setTitle(URLUtil.guessFileName(url,contentDisposition,mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Đang tải file về", //To notify the Client that the file is being downloaded
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkInternetConnection() {
        int red = 255;
        int green = 255;
        int blue = 0;

        double value = (double) (uploadSpeedGlobal + downloadSpeedGlobal);
        if (value >= 10000) {
            red = 0;
            green = 0;
            blue = 255;
        } else if (value >= 1000) {
            red = 0;
            green = (int) (255 - Math.round(value / 10000 * 255));
            blue = (int) Math.round(value / 10000 * 255);
        } else {
            red = (int) (255 - Math.round(value / 1000 * 255));
            green = 255;
            blue = 0;
        }

        imgInternetConnection.setColorFilter(Color.argb(255, red, green, blue));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            final Runtime runtime = Runtime.getRuntime();
            final long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
            final long maxHeapSizeInMB=runtime.maxMemory() / 1048576L;
            final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;

            txtMemory.setText("M: " + usedMemInMB + "/" + availHeapSizeInMB + " MB");
            txtAdblock.setText("Ads: " + adCount);
            cbxAd.setText("" + adCount);

            //capture();

            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

            //For 3G check
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .isConnectedOrConnecting();
            //For WiFi Check
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();

            if (!is3g && !isWifi) {
                imgInternetConnection.setImageResource(R.drawable.red);
                imgInternetConnection.setColorFilter(Color.argb(0, 0, 0, 0));
            }
            else {
                imgInternetConnection.setImageResource(R.drawable.green);

                checkInternetConnection();
            }

            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo){
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

        final WebView.HitTestResult webViewHitTestResult = webView.getHitTestResult();

        if (webViewHitTestResult.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE || webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                        webViewHitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

            String url = webViewHitTestResult.getExtra();

            contextMenu.setHeaderTitle(url);    //Download Image From Below

            contextMenu.add(0, 0, 0, "Mở liên kết").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    txtUrl.setText(url);
                    btnGo.performClick();

                    return false;
                }
            });

            contextMenu.add(0, 0, 0, "Sao chép liên kết").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("url", url);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(MainActivity.this, "Copied!", Toast.LENGTH_SHORT).show();

                    return false;
                }
            });

            if (webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                    webViewHitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

                contextMenu.add(0, 0, 0, "Lưu hình ảnh")    //Save - Download Image
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                String DownloadImageURL = webViewHitTestResult.getExtra();

                                if (URLUtil.isValidUrl(DownloadImageURL)) {

                                    /*DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageURL));
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                    downloadManager.enqueue(request);*/

                                    String filename = "";
                                    //String type = null;
                                    String mimeType = MimeTypeMap.getFileExtensionFromUrl(DownloadImageURL);
                                    filename = URLUtil.guessFileName(DownloadImageURL, DownloadImageURL, mimeType);
                                    /*if(mimeType!=null)
                                    {
                                        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeType);
                                    }
                                    if(type==null)
                                    {
                                        filename = filename.replace(filename.substring(filename.lastIndexOf(".")),".png");
                                        type = "image/*";
                                    }*/

                                    DownloadManager.Request request = new DownloadManager.Request(
                                            Uri.parse(DownloadImageURL));

                                    //request.setMimeType(mimeType);
                                    request.setDescription(filename);
                                    request.setTitle(filename);

                                    String cookies = CookieManager.getInstance().getCookie(DownloadImageURL);
                                    request.addRequestHeader("cookie", cookies);
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                    dm.enqueue(request);

                                    Toast.makeText(MainActivity.this, "Đang tải hình ảnh về", Toast.LENGTH_LONG).show();        //Image Downloaded Successfully
                                } else {
                                    Toast.makeText(MainActivity.this, "Không tải được, có lỗi xảy ra.", Toast.LENGTH_LONG).show();         //Sorry.. Something Went Wrong.
                                }
                                return false;
                            }
                        });
            }
        }
    }
/*
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }
*/

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            long txBytes = intent.getLongExtra("txBytes", 0);
            long rxBytes = intent.getLongExtra("rxBytes", 0);

            TextView RX = (TextView) findViewById(R.id.txtDownloadSpeed);
            TextView TX = (TextView) findViewById(R.id.txtUploadSpeed);

            String downloadSpeed, downloadUnit, uploadSpeed, uploadUnit;
            String contentDownload, contentUpload;

            //rxBytes = (TrafficStats.getTotalRxBytes() - mStartRX)/1024;        //KBps
            if (rxBytes < 1000) {
                downloadSpeed = Long.toString(rxBytes);
                downloadUnit = "KB/s";
            }
            else {
                downloadSpeed = Double.toString((double)Math.round((double)rxBytes / 1000 * 10) / 10);
                downloadUnit = "MB/s";
            }
            contentDownload = "↓: " + downloadSpeed + " " + downloadUnit;
            RX.setText(contentDownload);

            //txBytes = (TrafficStats.getTotalTxBytes() - mStartTX)/1024;           //KBps
            if (txBytes < 1000) {
                uploadSpeed = Long.toString(txBytes);
                uploadUnit = "KB/s";
            }
            else {
                uploadSpeed = Double.toString((double)Math.round((double)txBytes / 1000 * 10) / 10);
                uploadUnit = "MB/s";
            }
            contentUpload = "↑: " + uploadSpeed + " " + uploadUnit;
            TX.setText(contentUpload);

            uploadSpeedGlobal = txBytes;
            downloadSpeedGlobal = rxBytes;
        }
    };

    /*@Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        if (isInPictureInPictureMode) {

        }
        else {

        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        //LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("internet-speed"));

        txtUrl.clearFocus();
        webView.requestFocus();

        /*if (!isMyServiceRunning(InternetSpeedMeter.class))
        {
            startService(new Intent(this, InternetSpeedMeter.class));
        }*/
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        super.onPause();

        //LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    @Override
    public void onBackPressed() {
        if (listUrlVisible) {     //txtUrl.isFocused() == true
            txtUrl.clearFocus();
            listUrl.setVisibility(View.GONE);
            listUrlVisible = false;
            webView.requestFocus();
        }
        else if (webView.canGoBack()) {
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Click on URL
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String url = data.getStringExtra("url");
            webView.loadUrl(url);
        }

        //QR Code
        if (requestCode == 31) {
            if (resultCode == RESULT_OK) {
                //String contents = data.getStringExtra("SCAN_RESULT");
                String url = data.getStringExtra("qrcode");
                webView.loadUrl(url);
            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }

        //Upload
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage || data == null || resultCode != RESULT_OK) {
                return;
            }

            Uri[] result = null;
            String dataString = data.getDataString();

            if (dataString != null) {
                result = new Uri[]{Uri.parse(dataString)};
            }

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        /*
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e, Toast.LENGTH_LONG).show();
                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }

        } // end of code for all versions except of Lollipop

        // start of code for Lollipop only
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.getData() == null) {
                    // if there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }*/
    }

    public void setDesktopMode(WebView webView, boolean enabled) {
        String newUserAgent = webView.getSettings().getUserAgentString();
        if (enabled) {
            try {
                String ua = webView.getSettings().getUserAgentString();
                String androidOSString = webView.getSettings().getUserAgentString().substring(ua.indexOf("("), ua.indexOf(")") + 1);
                newUserAgent = webView.getSettings().getUserAgentString().replace(androidOSString, "(X11; Linux x86_64)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            newUserAgent = null;
        }

        webView.getSettings().setUserAgentString(newUserAgent);
        webView.getSettings().setUseWideViewPort(enabled);
        webView.getSettings().setLoadWithOverviewMode(enabled);
        webView.reload();
    }

    public void capture() {
        Bitmap bmp = null;
        ByteArrayOutputStream bos = null;
        byte[] bt = null;
        String image = "";
        try {
            bmp = Bitmap.createBitmap(webView.getWidth(),
                    webView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmp);
            webView.draw(c);        //With quality value 100 still works on main page google.com, just too heavy cause exception e or simply not load, so change quality compress (default: 100)

            bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 0, bos);
            bt = bos.toByteArray();

            image = Base64.encodeToString(bt, Base64.DEFAULT);

            Intent intentWidget = new Intent(getApplicationContext(), NewAppWidget.class);
            intentWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            intentWidget.putExtra("webView", image);

            int[] ids = AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetIds(new ComponentName(getApplicationContext(), NewAppWidget.class));
            if (ids != null && ids.length > 0) {
                intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intentWidget);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
    }

    public static Bitmap toGrayscale(Bitmap srcImage) {

        Bitmap bmpGrayscale = Bitmap.createBitmap(srcImage.getWidth(), srcImage.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmpGrayscale);
        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);        //0
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(srcImage, 0, 0, paint);

        return bmpGrayscale;
    }
}