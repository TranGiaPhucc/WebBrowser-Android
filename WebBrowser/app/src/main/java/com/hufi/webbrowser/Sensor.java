package com.hufi.webbrowser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;

public class Sensor extends Service implements SensorEventListener {

    private Handler mHandler = new Handler();

    private SensorManager sensorManager;
    private android.hardware.Sensor light;
    int lightValue = 0;

    public Sensor() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {}

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != android.hardware.Sensor.TYPE_LIGHT) return;

        lightValue = (int) event.values[0];
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_LIGHT);

        if (light == null) {
            Toast.makeText(this, "No sensor found.", Toast.LENGTH_SHORT).show();
        }
        else {
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                start();
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void start()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My notification light", "My notification light", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setVibrationPattern(new long[]{ 0 });
            channel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        mHandler.postDelayed(mRunnable, 1000);
    }

    private final Runnable mRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void run() {

            showNotification();

            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showNotification() {
        // TODO Auto-generated method stub

        String contentText = "Light: "  + lightValue + " lx";

        Bitmap bitmap = createBitmapFromString(String.valueOf(lightValue), "lx");
        Icon icon = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            icon = Icon.createWithBitmap(bitmap);
        }

        startForeground(2, new NotificationCompat.Builder(this, "My notification light")
                //.setContentTitle("Internet Speed Meter" + "     " + connectionType)
                .setContentTitle("Light Sensor")
                .setContentText(contentText)
                //builder.setSmallIcon(R.mipmap.ic_launcher_round);
                .setSmallIcon(IconCompat.createFromIcon(icon))
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .build());
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