package com.citu.qrcodescanner.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.citu.qrcodescanner.model.Entry;

/**
 * Created by metalgear8019 on 8/21/2015.
 */
public class EntryDao extends SQLiteOpenHelper {
    public static final String KEY_ID = "_id";
    public static final String KEY_DATA = "data";
    public static final String KEY_DATESCANNED = "dateScanned";
    private static final String DATABASE_NAME = "scan_history.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ENTRIES = "entries";

    private static final String[] COLUMNS = {KEY_ID, KEY_DATA, KEY_DATESCANNED};

    public EntryDao(Context paramContext)
    {
        super(paramContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void addEntry(Entry paramEntry)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATA, paramEntry.getData());
        values.put(KEY_DATESCANNED, System.currentTimeMillis());
        db.insert(TABLE_ENTRIES, null, values);
        db.close();
    }

    public void deleteEntry(Entry paramEntry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                TABLE_ENTRIES, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(paramEntry.getID())}
        ); //selections args

        db.close();
    }

    public Entry getEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
            db.query(
                    TABLE_ENTRIES, // a. table
                    COLUMNS, // b. column names
                    KEY_ID + " = ?", // c. selections
                    new String[]{String.valueOf(id)}, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null // h. limit
            );

        if (cursor != null)
            cursor.moveToFirst();

        Entry entry = new Entry();
        entry.setID(Integer.parseInt(cursor.getString(0)));
        entry.setData(cursor.getString(1));

        return entry;
    }

    public Entry getEntry(String data)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
            db.query(
                TABLE_ENTRIES, // a. table
                COLUMNS, // b. column names
                KEY_DATA + " = ?", // c. selections
                new String[]{ "%" + String.valueOf(data) + "%" }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null // h. limit
            );

        if (cursor != null)
            cursor.moveToFirst();

        Entry entry = new Entry();
        entry.setID(Integer.parseInt(cursor.getString(0)));
        entry.setData(cursor.getString(1));
        entry.setDate(cursor.getLong(2));

        return entry;
    }

    public List<Entry> getAllEntries()
    {
        List<Entry> entries = new ArrayList<Entry>();

        //String query = "SELECT  * FROM " + TABLE_ENTRIES;

        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor = db.rawQuery(query, null);

        Cursor cursor =
                db.query(
                        TABLE_ENTRIES, // a. table
                        COLUMNS, // b. column names
                        null, // c. selections
                        null, // d. selections args
                        null, // e. group by
                        null, // f. having
                        KEY_DATESCANNED + " DESC", // g. order by
                        null // h. limit
                );

        Entry entry = null;
        if (cursor.moveToFirst()) {
            do {
                entry = new Entry();
                entry.setID(Integer.parseInt(cursor.getString(0)));
                entry.setData(cursor.getString(1));
                entry.setDate(cursor.getLong(2));

                entries.add(entry);
            } while (cursor.moveToNext());
        }

        return entries;
    }

    @Override
    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase.execSQL("CREATE TABLE entries(_id INTEGER PRIMARY KEY, data TEXT, dateScanned BIGINTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS entries");
        onCreate(paramSQLiteDatabase);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
