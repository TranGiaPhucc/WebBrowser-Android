package com.hufi.webbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class InternetSpeedMeterActivity extends AppCompatActivity {
    ListView listISP;
    ArrayList<InternetSpeedMeterClass> arrayList;
    Database db;
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_speed_meter);

        db = new Database(InternetSpeedMeterActivity.this);

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