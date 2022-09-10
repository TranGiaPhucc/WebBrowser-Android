package com.hufi.webbrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<History> {
    Context context;
    int layoutResource;
    ArrayList<History> data;

    public HistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<History> objects) {
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

        History h = data.get(position);

        String url = h.getUrl();

        TextView lbUrl = convertView.findViewById(R.id.lbUrl);
        lbUrl.setText(url);

        return convertView;
    }
}
