package com.pradeephr.readow.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.pradeephr.readow.model.DbModelSql;
import com.pradeephr.readow.model.RssNamesandLinks;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String LOCALFEED_TABLE = "LOCALFEED_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_AGENCY_NAME = "AGENCY_NAME";
    public static final String COLUMN_AGENCY_CATEGORY = "AGENCY_CATEGORY";
    public static final String COLUMN_AGENCY_LINK = "AGENCY_LINK";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "readow.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + LOCALFEED_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_AGENCY_NAME + " TEXT, " + COLUMN_AGENCY_CATEGORY + " TEXT, " + COLUMN_AGENCY_LINK + " TEXT)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + LOCALFEED_TABLE );
    }

    public boolean addOne(DbModelSql dbModelSql) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_AGENCY_NAME, dbModelSql.getAgencyName());
        cv.put(COLUMN_AGENCY_CATEGORY, dbModelSql.getAgencyCategory());
        cv.put(COLUMN_AGENCY_LINK, dbModelSql.getAgencyLink());
        Log.d("inserted", dbModelSql.getAgencyLink());
        long insert = db.insert(LOCALFEED_TABLE, null, cv);
        return insert != -1;
    }

    public List<DbModelSql> readAll() {
        List<DbModelSql> retlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + LOCALFEED_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String agencyName = cursor.getString(1);
                String agencyCategory = cursor.getString(2);
                String agencyLink = cursor.getString(3);
                DbModelSql newdb = new DbModelSql(id, agencyName, agencyCategory, agencyLink);
                retlist.add(newdb);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return retlist;
    }


    public boolean deleteOne(DbModelSql dbModelSql) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + LOCALFEED_TABLE + " WHERE " + COLUMN_ID + " = " + dbModelSql.getDataId();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

//

    public Boolean checkifExists(DbModelSql dbModelSql) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        String query = "SELECT * FROM " + LOCALFEED_TABLE + " WHERE " + COLUMN_AGENCY_LINK + "=" + '"'+dbModelSql.getAgencyLink()+'"' ;
        Cursor cursor = MyDB.rawQuery(query, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

}



