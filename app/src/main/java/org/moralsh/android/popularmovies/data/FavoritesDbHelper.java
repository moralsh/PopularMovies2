package org.moralsh.android.popularmovies.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.moralsh.android.popularmovies.data.FavoritesContract.FavoritesEntry;

/**
 * Created by morals on 16/02/17.
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;


    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE =
                "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID                     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritesEntry.COLUMN_MOVIE_ID         +     " INTEGER, "                 +
                FavoritesEntry.COLUMN_TITLE            + " TEXT," +
                FavoritesEntry.COLUMN_OVERVIEW         + " TEXT," +
                FavoritesEntry.COLUMN_RATING           + " TEXT," +
                FavoritesEntry.COLUMN_RELEASE_DATE     + " TEXT," +
                FavoritesEntry.COLUMN_POSTER           + " TEXT," +
                FavoritesEntry.COLUMN_BACKGROUND       + " TEXT" + ");";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
