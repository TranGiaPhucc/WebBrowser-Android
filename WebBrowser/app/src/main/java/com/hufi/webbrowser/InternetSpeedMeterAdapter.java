package com.hufi.webbrowser;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class InternetSpeedMeterAdapter extends ArrayAdapter<InternetSpeedMeterClass> {
    Context context;
    int layoutResource;
    ArrayList<InternetSpeedMeterClass> data;

    public InternetSpeedMeterAdapter(@NonNull Context context, int resource, @NonNull ArrayList<InternetSpeedMeterClass> objects) {
        super(context, resource, objects);
        this.data=objects;
        this.layoutResource=resource;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        convertView = layoutInflater.inflate(layoutResource, parent, false);

        InternetSpeedMeterClass h = data.get(position);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(h.getDate());

        double a = 1;
        double b = 1;
        String txUnit = " MB";
        String rxUnit = " MB";
        if (h.getUpload() >= 1024) {
            a = 1024;
            txUnit = " GB";
        }
        if (h.getDownload() >= 1024) {
            b = 1024;
            rxUnit = " GB";
        }

        double uploadSpeed = (double) Math.round(h.getUpload() / a * 1000) / 1000;
        double downloadSpeed = (double) Math.round(h.getDownload() / b * 1000) / 1000;

        String upload = String.valueOf(uploadSpeed);
        String download = String.valueOf(downloadSpeed);

        TextView lbDate = convertView.findViewById(R.id.lbDate);
        lbDate.setText(date);

        TextView lbUpload = convertView.findViewById(R.id.lbUpload);
        lbUpload.setText("↑: " + upload + txUnit);

        TextView lbDownload = convertView.findViewById(R.id.lbDownload);
        lbDownload.setText("↓: " + download + rxUnit);

        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#505050"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#6c6c6c"));
        }

        return convertView;
    }
}
