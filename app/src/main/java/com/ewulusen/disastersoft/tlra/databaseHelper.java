package com.ewulusen.disastersoft.tlra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by diszterhoft.zoltan on 2018.10.11
 * Ebben a javafájlban fogom létrehozni az adatbázisokat amivel dolgozni fogunk
 */

public class databaseHelper extends SQLiteOpenHelper {
    /**
     * Előszőr is létrhozzuk az összes változót amivel dolgozni fogunk.
     */
    public static final String DatabaseName = "tulora.db";
    public static final String userTable = "user_TL";
    public static final String hourTable = "hour_TL";

    private Context context;
    public databaseHelper(Context paramContext)

    {
        super(paramContext, DatabaseName, null, 3);
        this.context = paramContext;
    }
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
        paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+userTable+" " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,EMAIL TEXT,AKTIV TEXT DEFAULT 1)");
        paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+hourTable+" " +
                "(EMAIL TEXT,TULORA TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String login()
    {
    String email="default@d.d";
        SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
        String str1 = "SELECT * FROM "+userTable+" where AKTIV='1'";
        Cursor localCursor = localSQLiteDatabase.rawQuery(str1, null);
        if(localCursor.getCount()>0) {
            localCursor.moveToNext();
            email=localCursor.getString(localCursor.getColumnIndex("EMAIL"));
        }
        else
        {
            addUser("default@d.d");
        }
        return email;
        }

    /**
     * hozzá ad egy sort az User táblájához
     * @id az email címe a felhasználónak
     */
    public void addUser(String id)
    {
        SQLiteDatabase localSQLiteDatabaseR = getReadableDatabase();
        String str1 = "SELECT * FROM "+userTable+" where EMAIL='"+id+"'";
        Cursor localCursor = localSQLiteDatabaseR.rawQuery(str1, null);
        if(localCursor.getCount()>0) {
            SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
            ContentValues localContentValues = new ContentValues();
            localContentValues.put("AKTIV","0");
            localSQLiteDatabase.update(userTable, localContentValues, null, null);
            localContentValues.clear();
            localContentValues.put("AKTIV","1");
            localSQLiteDatabase.update(userTable, localContentValues, "EMAIL='"+id+"'", null);
            localContentValues.clear();
        }
        else {
            SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
            ContentValues localContentValues = new ContentValues();
            localContentValues.put("AKTIV","0");
            localSQLiteDatabase.update(userTable, localContentValues, null, null);
            localContentValues.clear();
            localContentValues.put("EMAIL", id);
            localSQLiteDatabase.insert(userTable, null, localContentValues);
            localContentValues.clear();
            localContentValues.put("EMAIL", id);
            localContentValues.put("TULORA", "0");
            localSQLiteDatabase.insert(hourTable, null, localContentValues);
            localContentValues.clear();
        }
    }

    public String addHour(String email, String hour)
    {
        String tulora="0";
        if(email.equals(context.getString(R.string.email))){
            email="default@d.d";
        }
        SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
        String str1 = "SELECT * FROM "+hourTable+" where EMAIL='"+email+"'";
        Cursor localCursor = localSQLiteDatabase.rawQuery(str1, null);
        if(localCursor.getCount()>0) {
            localCursor.moveToLast();
            tulora=localCursor.getString(localCursor.getColumnIndex("TULORA"));
        }
        tulora=Integer.toString((Integer.parseInt(tulora))+(Integer.parseInt(hour)));
        SQLiteDatabase localSQLiteDatabaseW = getWritableDatabase();
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("TULORA",tulora);
        localSQLiteDatabaseW.update(hourTable, localContentValues, "EMAIL='"+email+"'", null);
        localContentValues.clear();

        return tulora;
    }
    public String getHour(String email)
    {
        String tulora="0";
        SQLiteDatabase localSQLiteDatabase = getReadableDatabase();
        String str1 = "SELECT * FROM "+hourTable+" where EMAIL='"+email+"'";
        Cursor localCursor = localSQLiteDatabase.rawQuery(str1, null);
        if(localCursor.getCount()>0) {
            localCursor.moveToLast();
            tulora=localCursor.getString(localCursor.getColumnIndex("TULORA"));
        }
        return tulora;
    }
    public void nullaz(String email)
    {
        SQLiteDatabase localSQLiteDatabaseW = getWritableDatabase();
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("TULORA","0");
        localSQLiteDatabaseW.update(hourTable, localContentValues, "EMAIL='"+email+"'", null);
        localContentValues.clear();

    }
}

