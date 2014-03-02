package com.canadianopendataexperience.datadrop.code2014.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by smcintyre on 02/03/14.
 */
public class ActDatabase {

    private static ActDatabase singleton;
    private ActDatabaseHelper opener;
    private SQLiteDatabase currentConnection;
    private Context context;

    private ActDatabase(Context context) {
        this.context = context;
    }

    public static ActDatabase getActDatabase(Context context) {
        if (singleton == null) {
            singleton = new ActDatabase(context);
        }
        singleton.context = context;
        return singleton;
    }

    public void open() {
        if (opener != null) opener.close();
        opener = new ActDatabaseHelper(context);
        currentConnection = opener.getWritableDatabase();
    }

    public void close() {
        opener.close();
    }

    public SQLiteDatabase getCurrentConnection() {
        if (!currentConnection.isOpen()) {
            this.open();
        }
        return currentConnection;
    }

    private class ActDatabaseHelper extends SQLiteAssetHelper {
        private static final String DATABASE_NAME = "act.db";
        private static final int DATABASE_VERSION = 1;

        ActDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    }
}
