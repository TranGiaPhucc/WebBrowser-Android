package com.hufi.webbrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.util.ArrayList;

public class Database {
    Context context;
    private String dbName = "UserWebBrowser.db";
    private String dbTable = "NguoiDung";
    private String dbTableHistory = "History";

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
        String sql1 = "create table if not exists History(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "url TEXT ) ";
        db.execSQL(sql1);
        closeDB(db);
    }

    public ArrayList<History> getHistoryAll()	{
        SQLiteDatabase db =	openDB();
        ArrayList<History>	arr =	new	ArrayList<>();
        String	sql =	"select	*	from	History";
        Cursor csr =	db.rawQuery(sql,	null);
        if	(csr !=	null)	{
            if	(csr.moveToLast())	{
                do	{
                    String	url	=	csr.getString(1);
                    arr.add(new	History(url));
                }	while	(csr.moveToPrevious());
            } }
        closeDB(db);
        return	arr;
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

    public boolean insertHistory(History h) {
        boolean flag = false;
        SQLiteDatabase db = openDB();
        ContentValues cv = new ContentValues();
        cv.put("url", h.getUrl());
        flag = db.insert(dbTableHistory, null, cv) > 0;
        closeDB(db);
        return flag;
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

    public void deleteHistory() {
        SQLiteDatabase db = openDB();
        db.delete(dbTableHistory, null, null);
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