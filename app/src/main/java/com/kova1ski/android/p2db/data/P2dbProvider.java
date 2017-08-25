package com.kova1ski.android.p2db.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Usuario on 25/08/2017.
 */
/*
Aparece mucho el sigo , @nullable , y , @NonNull ,. Esto se ha generado automáticamente al
autogenerar yo mismo todos los métodos con la bombilla. He mirado en la documentación y estas
cosas no son más que anotaciones que especifican si puede el valor ser nulo o no serlo y que
se creen advertencias relacionadas con estas anotaciones.
 */

// Evidentemente esta clase extiende de ContenProvider y ya mola porque lleva de serie
    // los métodos de insert, delete, ect.
public class P2dbProvider extends ContentProvider {

    // Declaramos el código , urimatcher , para el contenido del uri para toda la tabla
    private static final int TODA_LA_TABLA = 100 ;
    // Y ahora el otro para el contenido del uri para un sólo item
    private static final int SINGLE_ITEM_ID = 101 ;

    /**
     * El objeto , UriMatcher , hace coincidir el contenido
     * del , URI , con el código correspondiente.
     * Estas dos líneas de arriba no sé aún si están bien traducidas.
     *
     * Lo que se le pasa al interior dle constructor representa el
     * código de retorno para la raíz , URI ,.
     *  Esto es como usar , NO_MATCH , como entrada en este caso concreto.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Inicializador , static,. Esto corre(run) la primera vez que algo es llamado
    // desde esta clase.
    // ES FLIPANTE ESTO, VERDAD? ;-)
    static {
        sUriMatcher.addURI(P2dbContract.CONTENT_AUTHORITY, P2dbContract.PATH_SEGMENT, TODA_LA_TABLA);
        sUriMatcher.addURI(P2dbContract.CONTENT_AUTHORITY, P2dbContract.PATH_SEGMENT + "/#", SINGLE_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
