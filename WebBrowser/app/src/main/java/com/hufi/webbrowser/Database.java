package com.hufi.webbrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Database {
    Context context;
    private String dbName = "UserWebBrowser.db";
    private String dbTable = "NguoiDung";
    private String dbTableHistory = "History";
    private String dbTableBookmark = "Bookmark";
    private String dbTableInternetSpeedMeter = "InternetSpeedMeter";

    public Database(Context context)
    {
        this.context = context;
    }

    public SQLiteDatabase openDB() {
        //return SQLiteDatabase.openOrCreateDatabase(dbName,null);
        return context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
    }
    /*
    public SQLiteDatabase openDB() {
        String path = Environment.getExternalStorageDirectory().getPath()+"/"+dbName;
        return SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
    }*/

    public void closeDB(SQLiteDatabase db) {
        db.close();
    }

    public void createTable() {
        SQLiteDatabase db = openDB();
        String sql = "create table if not exists NguoiDung(" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "name TEXT, " +
                "webcount INTEGER ) ";
        db.execSQL(sql);
        sql = "create table if not exists History(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "url TEXT, " +
                "title TEXT ) ";
        db.execSQL(sql);
        sql = "create table if not exists Bookmark(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "url TEXT, " +
                "title TEXT ) ";
        db.execSQL(sql);
        sql = "create table if not exists InternetSpeedMeter(" +
                "date INTEGER PRIMARY KEY, " +
                "upload REAL," +
                "download REAL ) ";
        db.execSQL(sql);
        closeDB(db);
    }

    public ArrayList<Bookmark> getBookmarkAll()	{
        SQLiteDatabase db =	openDB();
        ArrayList<Bookmark>	arr =	new	ArrayList<>();
        String	sql =	"select	*	from	Bookmark";
        Cursor csr =	db.rawQuery(sql,	null);
        if	(csr !=	null)	{
            if	(csr.moveToFirst())	{
                do	{
                    String url = csr.getString(1);
                    String title = csr.getString(2);
                    arr.add(new	Bookmark(url, title));
                }	while	(csr.moveToNext());
            } }
        closeDB(db);
        return	arr;
    }

    public ArrayList<History> getHistoryAll()	{
        SQLiteDatabase db =	openDB();
        ArrayList<History>	arr =	new	ArrayList<>();
        String	sql =	"select	*	from	History";
        Cursor csr =	db.rawQuery(sql,	null);
        if	(csr !=	null)	{
            if	(csr.moveToLast())	{
                do	{
                    String url = csr.getString(1);
                    String title = csr.getString(2);
                    arr.add(new	History(url, title));
                }	while	(csr.moveToPrevious());
            }
        }
        closeDB(db);
        return	arr;
    }

    public ArrayList<InternetSpeedMeterClass> getInternetSpeedMeterAll()	{
        SQLiteDatabase db =	openDB();
        ArrayList<InternetSpeedMeterClass>	arr =	new	ArrayList<>();
        String	sql =	"select	*	from	InternetSpeedMeter";
        Cursor csr =	db.rawQuery(sql,	null);
        if	(csr !=	null)	{
            if	(csr.moveToLast())	{
                do	{
                    int date = csr.getInt(0);
                    double upload = csr.getDouble(1);
                    double download = csr.getDouble(2);

                    DateFormat df = new SimpleDateFormat("ddMMyyyy");

                    String dateStr = String.valueOf(date);
                    int day = date / 1000000;
                    if (day < 10)
                        dateStr = "0" + dateStr;

                    int month = date % 1000000;

                    String dateStrNow = df.format(Calendar.getInstance().getTime());
                    int monthNow = Integer.parseInt(dateStrNow) % 1000000;

                    if (month == monthNow) {
                        try {
                            arr.add(new InternetSpeedMeterClass(df.parse(dateStr), upload, download));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }	while	(csr.moveToPrevious());
            }
        }
        closeDB(db);
        return	arr;
    }

    public ArrayList<History> getUrlRecommend(String urlText)	{
        ArrayList<History> arr = new ArrayList<>();

        /*if (urlText.equals(""))
            return arr;*/

        int countUrl = 0;

        SQLiteDatabase db =	openDB();

        String sql = "select * from History where url like '%" + urlText + "%'";
        Cursor csr = db.rawQuery(sql, null);
        if	(csr !=	null) {
            if (csr.moveToLast()) {
                do {
                    String url = csr.getString(1);
                    String title = csr.getString(2);

                    boolean duplicatedUrl = false;

                    if (url.contains(urlText)) {
                        for (int i = 0; i < arr.size(); i++) {
                            if (arr.get(i).getUrl().equals(url)) {
                                duplicatedUrl = true;
                                break;
                            }
                        }
                        if (!duplicatedUrl) {
                            History h = new History(url, title);
                            arr.add(0, h);

                            countUrl++;
                        }
                    }
                } while (csr.moveToPrevious() && countUrl < 10);
            }
        }

        closeDB(db);

        ArrayList<History> arrRev = new ArrayList<>();
        for (int i = arr.size() - 1; i >= 0; i--) {
            arrRev.add(arr.get(i));
        }

        return arrRev;
    }

    public ArrayList<NguoiDung> getNguoiDungAll()	{
        SQLiteDatabase db =	openDB();
        ArrayList<NguoiDung>	arr =	new	ArrayList<>();
        String	sql =	"select	*	from	NguoiDung";
        Cursor csr =	db.rawQuery(sql,	null);
        if	(csr !=	null)	{
            if	(csr.moveToFirst())	{
                do	{
                    String	username	=	csr.getString(0);
                    String	password	=	csr.getString(1);
                    String	name	=	csr.getString(2);
                    int webcount = csr.getInt(3);
                    arr.add(new	NguoiDung(username, password, name, webcount));
                }	while	(csr.moveToNext());
            } }
        closeDB(db);
        return	arr;
    }

    public	NguoiDung getNguoiDung(String cUsername) {
        String[] fields = {"username", "password", "name", "webcount"};
        String[] ids = {cUsername};
        SQLiteDatabase db = openDB();
        Cursor cursor = db.query(dbTable, fields, "username	=	?", ids, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        String username = cursor.getString(0);
        String password = cursor.getString(1);
        String name = cursor.getString(2);
        int webcount = cursor.getInt(3);
        closeDB(db);
        return new NguoiDung(username, password, name, webcount);
    }

    public int countHistory() {
        int count = 0;
        SQLiteDatabase db =	openDB();
        String	sql =	"select	*	from History";
        Cursor csr =	db.rawQuery(sql,	null);
        if	(csr !=	null)	{
            if	(csr.moveToFirst())	{
                do	{
                    count++;
                }	while	(csr.moveToNext());
            }
        }
        closeDB(db);
        return count;
    }

    public int countHistoryUrl(String url) {
        int count = 0;
        SQLiteDatabase db =	openDB();
        String[] fields = {"id", "url", "title"};
        String[] ids = {url};
        Cursor csr = db.query(dbTableHistory, fields, "url	=	?", ids, null, null, null, null);
        if	(csr !=	null)	{
            if	(csr.moveToFirst())	{
                do	{
                    count++;
                }	while	(csr.moveToNext());
            }
        }
        closeDB(db);
        return count;
    }

    public boolean insertBookmark(Bookmark b) {
        boolean flag = false;
        SQLiteDatabase db = openDB();
        ContentValues cv = new ContentValues();
        cv.put("url", b.getUrl());
        cv.put("title", b.getTitle());
        flag = db.insert(dbTableBookmark, null, cv) > 0;
        closeDB(db);
        return flag;
    }

    public boolean insertHistory(History h) {
        boolean flag = false;
        SQLiteDatabase db = openDB();
        ContentValues cv = new ContentValues();
        cv.put("url", h.getUrl());
        cv.put("title", h.getTitle());
        flag = db.insert(dbTableHistory, null, cv) > 0;
        closeDB(db);
        return flag;
    }

    public boolean insertInternetSpeedMeterInitializeDate(Date d) {
        boolean flag = false;
        SQLiteDatabase db = openDB();
        ContentValues cv = new ContentValues();

        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        int value = Integer.parseInt(df.format(d));

        cv.put("date", value);
        cv.put("upload", 0);
        cv.put("download", 0);
        flag = db.insert(dbTableInternetSpeedMeter, null, cv) > 0;

        closeDB(db);
        return flag;
    }

    public boolean insertInternetSpeedMeter(InternetSpeedMeterClass i) {
        boolean flag = false;
        SQLiteDatabase db = openDB();
        ContentValues cv = new ContentValues();

        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        int value = Integer.parseInt(df.format(i.getDate()));

        cv.put("date", value);
        cv.put("upload", i.getUpload());      //getUploadSpeed(i.getDate())
        cv.put("download", i.getDownload());
        //flag = db.insert(dbTableInternetSpeedMeter, null, cv) > 0;
        flag = db.insertWithOnConflict(dbTableInternetSpeedMeter, null, cv, SQLiteDatabase.CONFLICT_REPLACE) > 0;

        closeDB(db);
        return flag;
    }

    public double getUploadSpeed(Date date) {
        double upload = 0;
        SQLiteDatabase db =	openDB();
        String[] fields = {"date", "upload", "download"};

        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        int value = Integer.parseInt(df.format(date));

        String[] ids = {String.valueOf(value)};
        Cursor csr = db.query(dbTableInternetSpeedMeter, fields, "date	=	?", ids, null, null, null, null);
        if	(csr !=	null) {
            csr.moveToFirst();
            upload = csr.getDouble(1);
        }

        closeDB(db);
        return upload;
    }

    public double getDownloadSpeed(Date date) {
        double download = 0;
        SQLiteDatabase db =	openDB();
        String[] fields = {"date", "upload", "download"};

        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        int value = Integer.parseInt(df.format(date));

        String[] ids = {String.valueOf(value)};
        Cursor csr = db.query(dbTableInternetSpeedMeter, fields, "date	=	?", ids, null, null, null, null);
        if	(csr !=	null) {
            csr.moveToFirst();
            download = csr.getDouble(2);
        }

        closeDB(db);
        return download;
    }

    public InternetSpeedMeterClass getThisMonthSpeed(Date d)	{
        InternetSpeedMeterClass i = new InternetSpeedMeterClass(d, 0, 0);
        double upload = 0;
        double download = 0;

        SQLiteDatabase db =	openDB();
        String	sql =	"select	*	from	InternetSpeedMeter";
        Cursor csr =	db.rawQuery(sql,	null);
        if	(csr !=	null)	{
            if	(csr.moveToLast())	{
                do	{
                    int date = csr.getInt(0);
                    int month = date % 1000000;

                    DateFormat df = new SimpleDateFormat("ddMMyyyy");
                    String dateStr = df.format(d);
                    int dateInt = Integer.parseInt(dateStr) % 1000000;

                    if (month == dateInt) {
                        upload += csr.getDouble(1);
                        download += csr.getDouble(2);

                        i.setUpload(upload);
                        i.setDownload(download);
                    }
                }	while	(csr.moveToPrevious());
            }
        }
        closeDB(db);
        return	i;
    }

    public boolean insert(NguoiDung nguoidung) {
        boolean flag = false;
        SQLiteDatabase db = openDB();
        ContentValues cv = new ContentValues();
        cv.put("username", nguoidung.getUsername());
        cv.put("password", nguoidung.getPassword());
        cv.put("name", nguoidung.getName());
        cv.put("webcount", nguoidung.getWebcount());
        flag = db.insert(dbTable, null, cv) > 0;
        closeDB(db);
        return flag;
    }

    public	boolean	update(NguoiDung	nd)	{
        boolean flag = false;
        SQLiteDatabase	db	=	openDB();
        ContentValues	cv	=	new	ContentValues();
        cv.put("username",	 nd.username);
        cv.put("password",	 nd.password);
        cv.put("name",	 nd.name);
        cv.put("webcount", nd.getWebcount());
        String[]	id	=	{nd.username};
        flag = db.update(dbTable, cv, "username = ?", id) > 0;
        closeDB(db);
        return flag;
    }

    public boolean checkUserExist(String username, String password){
        String[] columns = {"username"};

        SQLiteDatabase db = openDB();

        String selection = "username = ? and password = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(dbTable, columns, selection, selectionArgs, null, null, null);

        int count = cursor.getCount();

        cursor.close();
        db.close();

        if(count > 0){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkUserNameExist(String username){
        String[] columns = {"username"};

        SQLiteDatabase db = openDB();

        String selection = "username = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(dbTable, columns, selection, selectionArgs, null, null, null);

        int count = cursor.getCount();

        cursor.close();
        db.close();

        if(count > 0){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkUrlExist(String url){
        String[] columns = {"url"};

        SQLiteDatabase db = openDB();

        String selection = "url = ?";
        String[] selectionArgs = {url};

        Cursor cursor = db.query(dbTableHistory, columns, selection, selectionArgs, null, null, null);

        int count = cursor.getCount();

        cursor.close();
        db.close();

        if(count > 0){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkBookmarkExist(String url){
        String[] columns = {"url"};

        SQLiteDatabase db = openDB();

        String selection = "url = ?";
        String[] selectionArgs = {url};

        Cursor cursor = db.query(dbTableBookmark, columns, selection, selectionArgs, null, null, null);

        int count = cursor.getCount();

        cursor.close();
        db.close();

        if(count > 0){
            return true;
        } else {
            return false;
        }
    }

    public void deleteHistoryAll() {
        SQLiteDatabase db = openDB();
        db.delete(dbTableHistory, null, null);
        db.close();
    }

    public String getTitle(String url) {
        String title = "";
        SQLiteDatabase db =	openDB();
        String[] fields = {"id", "url", "title"};
        String[] ids = {url};
        Cursor csr = db.query(dbTableHistory, fields, "url	=	?", ids, null, null, null, null);
        if	(csr !=	null)	{
            if	(csr.moveToFirst())	{
                do	{
                    title = csr.getString(2);
                }	while	(csr.moveToNext());
            }
        }
        closeDB(db);
        return title;
    }

    public void deleteBookmark(String url) {
        SQLiteDatabase db =	openDB();
        String[] ids = {url};
        db.delete(dbTableBookmark, "url = ?", ids);
        closeDB(db);
    }

    public void deleteBookmarkAll() {
        SQLiteDatabase db = openDB();
        db.delete(dbTableBookmark, null, null);
        db.close();
    }

    public	String getLastUrl() {
        SQLiteDatabase db = openDB();
        String	sql =	"select	*	from	History";
        Cursor csr =	db.rawQuery(sql,	null);
        if (csr != null)
            csr.moveToLast();
        String url = csr.getString(1);
        closeDB(db);
        return url;
    }

    public boolean isHistoryEmpty() {
        SQLiteDatabase db = openDB();
        boolean c = true;
        String	sql =	"select	*	from	History";
        Cursor csr =	db.rawQuery(sql,	null);
        if(csr.getCount() > 0)
            c = false;
        csr.close();
        db.close();
        return c;
    }
}
