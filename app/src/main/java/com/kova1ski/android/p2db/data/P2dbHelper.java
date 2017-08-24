package com.kova1ski.android.p2db.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Usuario on 24/08/2017.
 */

public class P2dbHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos
    private final static String DATA_BASE_NAME = "P2db";
    // número de versión de la base de datos
    private final static int DATA_BASE_VERSION = 1 ;

    // Le quitamos los parámetros que no nos hacen falta y le pasamos las variables que acabamos de crear
    public P2dbHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
