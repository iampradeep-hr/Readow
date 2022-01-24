package com.pradeephr.readow.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.pradeephr.readow.model.ReadLaterModel;

import java.util.ArrayList;
import java.util.List;

public class SavedArticles extends SQLiteOpenHelper {
    public static final String READLATER_TABLE = "READLATER_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ARTICLE_TITLE = "TITLE";
    public static final String COLUMN_ARTICLE_LINK = "LINK";
    public static final String COLUMN_ARTICLE_PUBDATE = "PUBDATE";

    public SavedArticles(@Nullable Context context) {
        super(context, "saved.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + READLATER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ARTICLE_TITLE + " TEXT, " + COLUMN_ARTICLE_LINK + " TEXT, " + COLUMN_ARTICLE_PUBDATE + " TEXT )";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + READLATER_TABLE );
    }

    public boolean addOne(ReadLaterModel readLaterModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ARTICLE_TITLE, readLaterModel.getArticleTitle());
        cv.put(COLUMN_ARTICLE_LINK, readLaterModel.getArticleLink());
        cv.put(COLUMN_ARTICLE_PUBDATE, readLaterModel.getArticlePubDate());
        long insert = db.insert(READLATER_TABLE, null, cv);
        return insert != -1;
    }

    public List<ReadLaterModel> readAll() {
        List<ReadLaterModel> retlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + READLATER_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String articleTitle = cursor.getString(1);
                String articleLink = cursor.getString(2);
                String articlePubDate = cursor.getString(3);
                ReadLaterModel readLaterModel=new ReadLaterModel(id,articleTitle,articleLink,articlePubDate);
                retlist.add(readLaterModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return retlist;
    }


    public boolean deleteOne(ReadLaterModel readLaterModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + READLATER_TABLE + " WHERE " + COLUMN_ID + " = " + readLaterModel.getDataId();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkifExists(ReadLaterModel readLaterModel) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        String query = "SELECT * FROM " + READLATER_TABLE + " WHERE " + COLUMN_ARTICLE_TITLE + "=" + '"'+readLaterModel.getArticleTitle()+'"' ;
        Cursor cursor = MyDB.rawQuery(query, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

}