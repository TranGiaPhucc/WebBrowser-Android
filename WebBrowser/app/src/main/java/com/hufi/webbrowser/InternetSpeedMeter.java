package com.hufi.webbrowser;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import java.util.Timer;
import java.util.TimerTask;

public class InternetSpeedMeter extends Service {

    private Handler mHandler = new Handler();
    private long mStartRX = 0;
    private long mStartTX = 0;
    private long txBytes = 0;
    private long rxBytes = 0;

    private boolean checkHandlerOn = false;

    public InternetSpeedMeter() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        /*super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notifManager= getSystemService(NotificationManager.class);
            notifManager.cancelAll();
        }
        mHandler.removeCallbacks(mRunnable);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            start();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

                //For 3G check
                boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                        .isConnectedOrConnecting();
                //For WiFi Check
                boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                        .isConnectedOrConnecting();


                if (!is3g && !isWifi)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationManager notifManager= getSystemService(NotificationManager.class);
                        notifManager.cancelAll();
                    }
                    mHandler.removeCallbacks(mRunnable);
                    checkHandlerOn = false;
                }
                else
                {
                    if (checkHandlerOn == false) {
                        mHandler.postDelayed(mRunnable, 0);
                        checkHandlerOn = true;
                    }
                }
            }
        }, 0, 1000);*/

        //onDestroy();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void start()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My notification", "My notification", NotificationManager.IMPORTANCE_LOW);
            channel.setVibrationPattern(new long[]{ 0 });
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();

        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 0);
        }
    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            //TextView RX = (TextView) findViewById(R.id.txtDownloadSpeed);
            //TextView TX = (TextView) findViewById(R.id.txtUploadSpeed);

            rxBytes = (TrafficStats.getTotalRxBytes() - mStartRX)/1024;        //KBps
            //RX.setText("Download: " + Long.toString(rxBytes) + " KBps");
            /*if (rxBytes < 1024)
                RX.setText("Download: " + Long.toString(rxBytes) + " Kbps");
            else {
                double round = Math.round((double) rxBytes / 1024 * 10.0) / 10;
                RX.setText("Download: " + Double.toString(round) + " Mbps");
            }*/

            txBytes = (TrafficStats.getTotalTxBytes() - mStartTX)/1024;           //KBps
            //TX.setText("Upload: " + Long.toString(txBytes) + " KBps");
            /*if (txBytes < 1024)
                TX.setText("Upload: " + Long.toString(txBytes) + " Kbps");
            else {
                double round = Math.round((double) txBytes / 1024 * 10.0) / 10;
                TX.setText("Upload: " + Double.toString(round) + " Mbps");
            }*/

            showNotification();

            mStartRX = TrafficStats.getTotalRxBytes();
            mStartTX = TrafficStats.getTotalTxBytes();

            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    private void showNotification() {
        // TODO Auto-generated method stub
        /*NotificationManager nMN = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder n  = new Notification.Builder(this)
                .setContentTitle("Internet Speed Meter")
                .setContentText("Upload: " + Long.toString(txBytes) + "        Download: " + Long.toString(rxBytes))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent);
        //n.flags |= Notification.FLAG_NO_CLEAR;
        nMN.notify(0, n.build());*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "My notification");
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
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
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
    }
}