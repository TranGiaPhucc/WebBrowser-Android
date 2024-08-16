package com.hufi.webbrowser;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class InternetSpeedMeterAutoStart extends BroadcastReceiver {

    public void onReceive(Context context, Intent arg1)
    {
        /*if (!CheckConnection.haveNetwordConnection(context)) {
            CheckConnection.ShowToast_Short(context, "No internet connection.");
            //context.stopService(new Intent(context, InternetSpeedMeter.class));
            //finish();
        }
        else {

        }*/

        //Intent main = new Intent(context, MainActivity.class);
        //context.startActivity(main);

        Intent intent = new Intent(context, InternetSpeedMeter.class);
        context.startService(intent);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }*/
        /*
        Intent intent1 = new Intent(context, Sensor.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else {
            context.startService(intent1);
        }

        Intent intent2 = new Intent(context, Sensor1.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent2);
        } else {
            context.startService(intent2);
        }

        Intent intent3 = new Intent(context, Sensor2.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent3);
        } else {
            context.startService(intent3);
        }*/

        //context.sendBroadcast(main);

        //Log.i("Autostart", "started");
    }
}