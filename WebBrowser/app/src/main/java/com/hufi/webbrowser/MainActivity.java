package com.hufi.webbrowser;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout refreshLayout;
    FrameLayout customViewContainer;
    TextView txtUrl;
    Button btnGo, btnBack, btnForward, btnGoogle, btnYoutube;
    ImageButton btnReload, btnMaps, btnPhoneDesktop, btnHistory;
    ListView listUrl;
    ArrayList<History> arrayList;
    Spinner spnSearch;
    ProgressBar prgBar;
    Database db;
    //SQL sql;
    private List<String> list;
    private WebView webView;
    private String search = "";

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

    //private Handler mHandler = new Handler();
    //private long mStartRX = 0;
    //private long mStartTX = 0;
    //private long txBytes = 0;
    //private long rxBytes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!CheckConnection.haveNetworkConnection(getApplicationContext())) {
            CheckConnection.ShowToast_Short(getApplicationContext(), "No internet connection.");
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

        //mHandler.postDelayed(mRunnable, 0);

        //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        db = new Database(MainActivity.this);
        db.createTable();

        if (!db.checkUserNameExist("admin")) {
            NguoiDung nd = new NguoiDung("admin", "admin", "Trần Gia Phúc", 0);
            db.insert(nd);
        }

        NguoiDung n = db.getNguoiDung("admin");
        //Toast.makeText(MainActivity.this, "Bạn đã duyệt " + n.getWebcount() + " trang web.", Toast.LENGTH_SHORT).show();

        Toast.makeText(MainActivity.this, "Bạn đã duyệt " + db.countHistory() + " trang web.", Toast.LENGTH_SHORT).show();

        webView=findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        registerForContextMenu(webView);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        //webView.loadUrl("https://www.google.com");

        if (db.isHistoryEmpty() == true)
        {
            webView.loadUrl("https://www.google.com");
        }
        else {
            String urlLast = db.getLastUrl();
            webView.loadUrl(urlLast);
        }

        customViewContainer = findViewById(R.id.customViewContainer);

        txtUrl=findViewById(R.id.txtUrl);
        listUrl=findViewById(R.id.listUrl);
        btnReload=findViewById(R.id.btnReload);
        btnGo=findViewById(R.id.btnGo);
        btnBack=findViewById(R.id.btnBack);
        btnForward=findViewById(R.id.btnForward);
        btnGoogle=findViewById(R.id.btnGoogle);
        btnYoutube=findViewById(R.id.btnYoutube);
        btnMaps=findViewById(R.id.btnMaps);
        btnPhoneDesktop=findViewById(R.id.btnPhoneDesktop);
        btnHistory=findViewById(R.id.btnHistory);
        prgBar=findViewById(R.id.prgBar);
        spnSearch=findViewById(R.id.spnSearch);
        refreshLayout=findViewById(R.id.refreshLayout);

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh () {
                webView.reload();
                refreshLayout.setRefreshing(false);
                /*
                refreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        webView.reload();
                    }
                },0);*/
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
                if (webView.getScrollY() == 0) {
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
                webView.requestFocus();

                search = "Web";
                txtUrl.setText("");
                txtUrl.setHint("https://www.google.com");
            }
        });

        txtUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    txtUrl.clearFocus();
                    listUrl.setVisibility(View.GONE);
                }
                else if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER)
                {
                    if (search.equals("Web"))
                    {
                        String url=txtUrl.getText().toString().replace(" ","");
                        webView.loadUrl(url);
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
        HistoryAdapter adapterUrl = new HistoryAdapter(this, R.layout.list_history, arrayList);
        listUrl.setAdapter(adapterUrl);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                webView.requestFocus();
                return false;
            }
        });

        txtUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listUrl.getVisibility() == View.GONE) {
                    listUrl.setVisibility(View.VISIBLE);

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
                    webView.requestFocus();
                }
                else {
                    listUrl.setVisibility(View.VISIBLE);

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
                webView.requestFocus();

                webView.reload();
            }
        });

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
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
                webView.requestFocus();

                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search.equals("Web"))
                {
                    String url=txtUrl.getText().toString().replace(" ","");
                    webView.loadUrl(url);
                }
                else
                {
                    String url=txtUrl.getText().toString().replace(" ","+");
                    webView.loadUrl("https://google.com/search?q=" + url);
                }
                //txtUrl.requestFocus();
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
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
                webView.requestFocus();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("https://www.google.com");
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                webView.requestFocus();
            }
        });

        btnYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("https://www.youtube.com");
                txtUrl.clearFocus();
                listUrl.setVisibility(View.GONE);
                webView.requestFocus();
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                // Here you can check your new URL.
                //super.onPageStarted(view, url, favicon);
                //Log.e("URL", url);
                //txtUrl.setText(url);



                prgBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                // Here you can check your new URL.
                //super.onPageFinished(view, url);
                //Log.e("URL", url);

                prgBar.setVisibility(View.GONE);

                String urlCheck = txtUrl.getText().toString();
                txtUrl.setText(url);

                if (!urlCheck.equals(txtUrl.getText().toString()))
                {
                    //String username = getIntent().getStringExtra("username");
                    //NguoiDung nd = db.getNguoiDung(username);
                    NguoiDung nd = db.getNguoiDung("admin");
                    int webCount = nd.getWebcount() + 1;
                    nd.webcount = webCount;
                    db.update(nd);

                    String title = view.getTitle();
                    History h = new History(url, title);
                    db.insertHistory(h);

                    AsyncTaskSQL runner = new AsyncTaskSQL();
                    runner.execute(url, title);

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
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*if (url.endsWith(".mp4")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                    return true;
                } else*/
                    if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("smsto:")
                        || url.startsWith("mms:") || url.startsWith("mmsto:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            private Map<String, Boolean> loadedUrls = new HashMap<>();

            @SuppressWarnings("deprecation")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                boolean ad;
                if (!loadedUrls.containsKey(url)) {
                    ad = AdBlocker.isAd(url);
                    loadedUrls.put(url, ad);
                } else {
                    ad = loadedUrls.get(url);
                }
                return ad ? AdBlocker.createEmptyResource() :
                        super.shouldInterceptRequest(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                prgBar.setProgress(progress);
            }

            public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
                super.onShowCustomView(view,callback);
                webView.setVisibility(View.GONE);
                customViewContainer.setVisibility(View.VISIBLE);
                customViewContainer.addView(view);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }

            public void onHideCustomView () {
                super.onHideCustomView();
                webView.setVisibility(View.VISIBLE);
                customViewContainer.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                webView.requestFocus();
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
                    WebChromeClient.FileChooserParams fileChooserParams) {
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

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
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
                Toast.makeText(getApplicationContext(),"Đang tải...",Toast.LENGTH_SHORT).show();      //Downloading File
            }
        });
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
/*
    private final Runnable mRunnable = new Runnable() {
        public void run() {
            TextView RX = (TextView) findViewById(R.id.txtDownloadSpeed);
            TextView TX = (TextView) findViewById(R.id.txtUploadSpeed);

            String downloadSpeed, downloadUnit, uploadSpeed, uploadUnit;
            String contentDownload, contentUpload;

            //rxBytes = (TrafficStats.getTotalRxBytes() - mStartRX)/1024;        //KBps
            if (InternetSpeedMeter.rxBytes < 1000) {
                downloadSpeed = Long.toString(InternetSpeedMeter.rxBytes);
                downloadUnit = "KB/s";
            }
            else {
                downloadSpeed = Double.toString((double)Math.round((double)InternetSpeedMeter.rxBytes / 1000 * 10) / 10);
                downloadUnit = "MB/s";
            }
            contentDownload = "Download: " + downloadSpeed + " " + downloadUnit;
            RX.setText(contentDownload);

            //txBytes = (TrafficStats.getTotalTxBytes() - mStartTX)/1024;           //KBps
            if (InternetSpeedMeter.txBytes < 1000) {
                uploadSpeed = Long.toString(InternetSpeedMeter.txBytes);
                uploadUnit = "KB/s";
            }
            else {
                uploadSpeed = Double.toString((double)Math.round((double)InternetSpeedMeter.txBytes / 1000 * 10) / 10);
                uploadUnit = "MB/s";
            }
            contentUpload = "Upload: " + uploadSpeed + " " + uploadUnit;
            TX.setText(contentUpload);

            //showNotification();

            //mStartRX = TrafficStats.getTotalRxBytes();
            //mStartTX = TrafficStats.getTotalTxBytes();

            mHandler.postDelayed(mRunnable, 0);
        }
    };
*/
    /*private void showNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My notification");
        builder.setContentTitle("Internet Speed Meter");
        builder.setContentText("Upload: " + Long.toString(txBytes) + " KBps        Download: " + Long.toString(rxBytes) + " KBps");
        //builder.setSmallIcon(R.mipmap.ic_launcher_round);
        Bitmap bitmap = createBitmapFromString(Long.toString(rxBytes), " KB/s");
        Icon icon = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            icon = Icon.createWithBitmap(bitmap);
        }
        builder.setSmallIcon(IconCompat.createFromIcon(icon));
        builder.setAutoCancel(false);
        builder.setOnlyAlertOnce(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
        managerCompat.notify(1, builder.build());
    }

    private Bitmap createBitmapFromString(String speed, String units) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(55);
        paint.setTextAlign(Paint.Align.CENTER);

        Paint unitsPaint = new Paint();
        unitsPaint.setAntiAlias(true);
        unitsPaint.setTextSize(40); // size is in pixels
        unitsPaint.setTextAlign(Paint.Align.CENTER);

        Rect textBounds = new Rect();
        paint.getTextBounds(speed, 0, speed.length(), textBounds);

        Rect unitsTextBounds = new Rect();
        unitsPaint.getTextBounds(units, 0, units.length(), unitsTextBounds);

        int width = (textBounds.width() > unitsTextBounds.width()) ? textBounds.width() : unitsTextBounds.width();

        Bitmap bitmap = Bitmap.createBitmap(width + 10, 90,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(speed, width / 2 + 5, 50, paint);
        canvas.drawText(units, width / 2, 90, unitsPaint);

        return bitmap;
    }*/

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo){
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);

        final WebView.HitTestResult webViewHitTestResult = webView.getHitTestResult();

        if (webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                webViewHitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

            contextMenu.setHeaderTitle("");    //Download Image From Below

            contextMenu.add(0, 1, 0, "Lưu hình ảnh")    //Save - Download Image
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            String DownloadImageURL = webViewHitTestResult.getExtra();

                            if(URLUtil.isValidUrl(DownloadImageURL)){

                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageURL));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.enqueue(request);

                                Toast.makeText(MainActivity.this,"Đang tải xuống.",Toast.LENGTH_LONG).show();        //Image Downloaded Successfully
                            }
                            else {
                                Toast.makeText(MainActivity.this,"Không tải được, có lỗi xảy ra.",Toast.LENGTH_LONG).show();         //Sorry.. Something Went Wrong.
                            }
                            return false;
                        }
                    });
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
            contentDownload = "Download: " + downloadSpeed + " " + downloadUnit;
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
            contentUpload = "Upload: " + uploadSpeed + " " + uploadUnit;
            TX.setText(contentUpload);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        txtUrl.clearFocus();
        webView.requestFocus();

        if (!isMyServiceRunning(InternetSpeedMeter.class))
        {
            startService(new Intent(this, InternetSpeedMeter.class));
        }

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(messageReceiver, new IntentFilter("internet-speed"));
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        //WebView webView = (WebView) findViewById(R.id.webView);
        webView=findViewById(R.id.webView);
        txtUrl=findViewById(R.id.txtUrl);
        listUrl=findViewById(R.id.listUrl);
        if (listUrl.getVisibility() == View.VISIBLE) {
            txtUrl.clearFocus();
            listUrl.setVisibility(View.GONE);
            webView.requestFocus();
        }
        else if (webView.canGoBack()){
            webView.goBack();
            txtUrl.clearFocus();
            listUrl.setVisibility(View.GONE);
            webView.requestFocus();
        }
        /*else {
            super.onBackPressed();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String url = data.getStringExtra("url");
            webView.loadUrl(url);
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
}