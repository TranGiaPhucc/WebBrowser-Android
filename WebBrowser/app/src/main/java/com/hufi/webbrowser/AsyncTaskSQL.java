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
            sql.insertUrl(strings[0], strings[1]);
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
