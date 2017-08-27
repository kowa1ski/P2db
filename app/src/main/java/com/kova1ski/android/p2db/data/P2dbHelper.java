package com.kova1ski.android.p2db.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.kova1ski.android.p2db.data.P2dbContract.*;

/**
 * Created by Usuario on 24/08/2017.
 */

public class P2dbHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos
    private final static String DATA_BASE_NAME = "p2db.sqlite";
    // número de versión de la base de datos
    private final static int DATA_BASE_VERSION = 1 ;

    // Le quitamos los parámetros que no nos hacen falta y le pasamos las variables que acabamos de crear
    public P2dbHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creammos un String que contenga las sentencias SQL
        String SQL_CREATE_TABLE = "CREATE TABLE " + P2dbEntry.TABLE_NAME + " ("
                + P2dbEntry.CN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + P2dbEntry.CN_NOMBRE + " TEXT NOT NULL, "
                + P2dbEntry.CN_TELEFONO + " INTEGER);";

        // ejecución del código inmediato
        db.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
