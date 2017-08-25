package com.kova1ski.android.p2db.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.kova1ski.android.p2db.data.P2dbContract.*;

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
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_SEGMENT, TODA_LA_TABLA);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_SEGMENT + "/#", SINGLE_ITEM_ID);
    }

    // Declaramos el objeto DbHelper
    private P2dbHelper mDbHelper;

    // Ahora sí, inicializamos el provider con el objeto dbHelper y está
    // guay porque le marcamos un getContext()
    // Y también le devolvemos un valor , true ,.
    @Override
    public boolean onCreate() {
        // de esta manera, en el onCreate, tenermos la llamada a dbHelper
        mDbHelper = new P2dbHelper(getContext());
        return true;
    }

    /**
     * Vamos a cargar la , query , que es un , cursor ,.
     * Y es una variable cursor la que vamos a devolver en vez de , null ,.
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Antes llamamos a dbHelper así que ahora, como eso ya lo tenemos
        // hecho en el onCreate, pues sólo debemos continuar ese camino y
        // hacer lo que sabemos que es acceder, ahora sí a la base, esta
        // vez en modo lectura.
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Ahora declaramos el cursor que vamos a utilizar
        Cursor cursor;

        // También partimos las diferencias entre
        // las dos llamadas diferentes que podemos hacer a
        // la query pasando a una variable , int , una
        // de las dos alternativas que hay de acceso a la base.
        // Para ello extraemos esa porción (100 ó 101) de
        // aquella variable , sUrimatcher ,.
        // La uri a la que estamos haciendo referencia y que se ve dentro del paréntesis es
        // la variable uri que obtenemos del propio método en el que estamos.
        int match = sUriMatcher.match(uri);
        switch (match){
            case TODA_LA_TABLA: // usamos las variables que nos pasan a este método
                cursor = db.query(P2dbEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SINGLE_ITEM_ID:
                // En este caso debemos modificar las variables , selection , y , selectionArgs ,.
                selection = P2dbEntry.CN_ID + "=?" ;
                //Aclaro que este , selectionArgs , pues lo construimos con el _id de un item
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Y por fin ordenamos este cursor
                cursor = db.query(P2dbEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            // Aprovechamos el , default , para tratar el error en caso de que se produzca.
            default:
                throw new IllegalArgumentException("No se puede construir la , query , "
                        + "por estar tratando con la URI desconocida " + uri);
        }

        // Ahora hacemos una NOTIFICACIÓN al cursor de manera que
        // sepamos el contenido de la URI que fue creado en el cursor de
        // forma que si la URI cambia ENTONCES SABREMOS QUE necesitamos
        // actualizar el , cursor ,.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Una vez construido, ahora sí, devolvemos el cursor.
        return cursor;
    }

    /**
     * Devuelve el tipo MIME del dato contenido en el URI
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case TODA_LA_TABLA:
                // Esta variable que se devuelve es una String que es una dirección
                return P2dbEntry.CONTENT_LIST_TYPE;
            case SINGLE_ITEM_ID:
                return P2dbEntry.CONTENT_ITEM_TYPE;
            // aprovechamos el , defaul , para controlar la excepción
            default:
                throw new IllegalStateException("desconocida URI " + uri + " con match " + match);
        }
        // Evidentemente, el , return null , nos lo cargamos porque ya hemos , return , en el método
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
