package com.hufi.webbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    Button btnXoa;
    ListView listHistory;
    ArrayList<History> arrayList;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = new Database(HistoryActivity.this);
        //db.createTable();

        btnXoa=findViewById(R.id.btnXoaLichSu);
        listHistory=findViewById(R.id.listHistory);

        arrayList =new ArrayList<>();
        HistoryAdapter adapterHistory = new HistoryAdapter(this, R.layout.list_history, arrayList);
        listHistory.setAdapter(adapterHistory);

        adapterHistory.clear();
        arrayList.addAll(db.getHistoryAll());
        adapterHistory.notifyDataSetChanged();

        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent data = new Intent();
                String url = ((TextView) view.findViewById(R.id.lbUrl)).getText().toString();
                data.putExtra("url", url);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteHistory();
                NguoiDung nd = db.getNguoiDung("admin");
                nd.webcount = 0;
                db.update(nd);
                adapterHistory.clear();
                arrayList.addAll(db.getHistoryAll());
                adapterHistory.notifyDataSetChanged();

            }
        });
    }
}