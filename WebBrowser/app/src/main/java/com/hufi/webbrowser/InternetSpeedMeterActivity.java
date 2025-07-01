package com.hufi.webbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class InternetSpeedMeterActivity extends AppCompatActivity {
    ListView listISP;
    ArrayList<InternetSpeedMeterClass> arrayList;
    Database db;
    Handler handler = new Handler();
    Runnable runnable;
    TextView lbISP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_speed_meter);

        db = new Database(InternetSpeedMeterActivity.this);

        lbISP=findViewById(R.id.lbISP);

        listISP=findViewById(R.id.listISP);

        arrayList = new ArrayList<>();
        InternetSpeedMeterAdapter adapterISP = new InternetSpeedMeterAdapter(this, R.layout.list_internetspeedmeter, arrayList);
        listISP.setAdapter(adapterISP);

        /*adapterISP.clear();
        arrayList.addAll(db.getInternetSpeedMeterAll());
        adapterISP.notifyDataSetChanged();*/

        runnable = new Runnable() {
            @Override
            public void run() {
                adapterISP.clear();
                arrayList.addAll(db.getInternetSpeedMeterAll());
                adapterISP.notifyDataSetChanged();

                Date d = Calendar.getInstance().getTime();
                InternetSpeedMeterClass i = db.getThisMonthSpeed(d);

                double a = 1;
                double b = 1;
                String txUnit = " MB";
                String rxUnit = " MB";
                if (i.getUpload() >= 1024) {
                    a = 1024;
                    txUnit = " GB";
                }
                if (i.getDownload() >= 1024) {
                    b = 1024;
                    rxUnit = " GB";
                }

                double uploadSpeed = (double) Math.round(i.getUpload() / a * 1000) / 1000;
                double downloadSpeed = (double) Math.round(i.getDownload() / b * 1000) / 1000;

                String uploadStr = String.valueOf(uploadSpeed);
                String downloadStr = String.valueOf(downloadSpeed);

                lbISP.setText("This month" + "     ↑ " + uploadStr + txUnit + "     ↓ " + downloadStr + rxUnit);

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources or save state
        handler.removeCallbacks(runnable);
    }
}