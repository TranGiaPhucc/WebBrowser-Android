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

import java.util.ArrayList;

public class BookmarkAdapter extends ArrayAdapter<Bookmark> {
    Context context;
    int layoutResource;
    ArrayList<Bookmark> data;

    public BookmarkAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Bookmark> objects) {
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

        Bookmark b = data.get(position);

        String url = b.getUrl();
        String title = b.getTitle();

        TextView lbUrl = convertView.findViewById(R.id.lbUrl);
        lbUrl.setText(url);

        TextView lbTitle = convertView.findViewById(R.id.lbTitle);
        lbTitle.setText(title);

        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#505050"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#6c6c6c"));
        }

        return convertView;
    }
}
