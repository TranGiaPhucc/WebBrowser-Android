package com.hufi.webbrowser;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Vibrator;

import java.sql.SQLException;

public class AsyncTaskSQL extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... strings) {
        SQL sql = new SQL();
        if (sql.isConnected() == true) {
            if (strings[0].equals("History"))
                sql.insertUrl(strings[1], strings[2]);
            if (strings[0].equals("Ad"))
                sql.insertAd(strings[1]);
            //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            //v.vibrate(100);
            try {
                sql.Close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
