package com.hufi.webbrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLException;
import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {
    Button btnLoadSQL, btnXoa;
    ListView listBookmark;
    ArrayList<Bookmark> arrayList;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        db = new Database(BookmarkActivity.this);
        //db.createTable();

        btnXoa=findViewById(R.id.btnXoaDauTrang);
        btnLoadSQL=findViewById(R.id.btnLoadSQL);
        listBookmark=findViewById(R.id.listBookmark);

        arrayList = new ArrayList<>();
        BookmarkAdapter adapterBookmark = new BookmarkAdapter(this, R.layout.list_bookmark, arrayList);
        listBookmark.setAdapter(adapterBookmark);

        adapterBookmark.clear();
        arrayList.addAll(db.getBookmarkAll());
        adapterBookmark.notifyDataSetChanged();

        listBookmark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent data = new Intent();
                String url = ((TextView) view.findViewById(R.id.lbUrl)).getText().toString();
                data.putExtra("url", url);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

        btnLoadSQL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQL sql = new SQL();
                ArrayList<Bookmark> arrayBookmark;

                if (sql.isConnected() == false) {
                    Toast.makeText(BookmarkActivity.this, "Không thể kết nối đến server.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    arrayBookmark = sql.loadBookmarkSQL();
                    int size = arrayBookmark.size();
                    for (int i = 0; i < size ; i++){
                        Bookmark b = new Bookmark(arrayBookmark.get(i).getUrl(), arrayBookmark.get(i).getTitle());
                        db.insertBookmark(b);
                    }
                    Toast.makeText(BookmarkActivity.this, "Load dấu trang từ SQL thành công.", Toast.LENGTH_SHORT).show();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                adapterBookmark.clear();
                arrayList.addAll(db.getBookmarkAll());
                adapterBookmark.notifyDataSetChanged();

                try {
                    sql.Close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteBookmarkAll();
                adapterBookmark.clear();
                arrayList.addAll(db.getBookmarkAll());
                adapterBookmark.notifyDataSetChanged();
            }
        });
    }
}