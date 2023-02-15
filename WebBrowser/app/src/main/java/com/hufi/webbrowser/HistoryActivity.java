package com.hufi.webbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    Button btnXoa, btnLoadSQL;
    ListView listHistory;
    ArrayList<History> arrayList;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = new Database(HistoryActivity.this);
        //db.createTable();

        btnLoadSQL=findViewById(R.id.btnLoadSQL);
        btnXoa=findViewById(R.id.btnXoaLichSu);
        listHistory=findViewById(R.id.listHistory);

        arrayList = new ArrayList<>();
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
                db.deleteHistoryAll();
                NguoiDung nd = db.getNguoiDung("admin");
                nd.webcount = 0;
                db.update(nd);
                adapterHistory.clear();
                arrayList.addAll(db.getHistoryAll());
                adapterHistory.notifyDataSetChanged();
            }
        });

        btnLoadSQL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQL sql = new SQL();
                ArrayList<History> arrayHistory;

                if (sql.isConnected() == false) {
                    Toast.makeText(HistoryActivity.this, "Không thể kết nối đến server.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    arrayHistory = sql.loadHistorySQL();
                    int size = arrayHistory.size();
                    for (int i = 0; i < size ; i++){
                        History h = new History(arrayHistory.get(i).getUrl(), arrayHistory.get(i).getTitle());
                        db.insertHistory(h);
                    }
                    Toast.makeText(HistoryActivity.this, "Load lịch sử từ SQL thành công.", Toast.LENGTH_SHORT).show();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                adapterHistory.clear();
                arrayList.addAll(db.getHistoryAll());
                adapterHistory.notifyDataSetChanged();

                try {
                    sql.Close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }
}