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
import android.util.Log;
import android.widget.Toast;

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
        // Es sencillo. Si estamos aquí es porque
        // queremos insertar un nuevo item. Si es así llamaremos a otro método, el
        // cual crearemos también, para insertar el nuevo item.
        // Medimos la , URI , con el , UriMatcher , y comprobamos que viene bien
        // sabiendo que la terminación es 100, o sea, que es toda la tabla porque
        // insertamos un registro de nueva creación contra TODA LA TABLA.
        final int match = sUriMatcher.match(uri);
        switch (match){
            case TODA_LA_TABLA:
                // Si no aseguramos de que vamos a insertar un sólo item
                // entonces vamos a ejecutar un método que tenemos que
                // crear para para ello. A ese método hay que pasarle esta
                // , URI , que acabamos de comprobar y también el
                // , ContentValues , que en este método vemos que se llama , values ,.
                return insertNewItem(uri, values);
            default:
                throw new IllegalArgumentException("La inserción no es soportada para " + uri);
        }
    }

    private Uri insertNewItem(Uri uri, ContentValues values) {

        // Pues ya estamos aquí que venimos desde el método insert.
        // Aquí retornaremos un nuevo contenido URI para una , row , dentro de
        // la base de datos.

        // Antes de nada vamos a chequear que el nombre no sea , null , porque
        // cuando declaramos la columna al crear la base le digimos que no
        // podía serlo, que era NOT NULL
        String nombreQueEstamosComprobando = values.getAsString(P2dbEntry.CN_NOMBRE);
        if (nombreQueEstamosComprobando == null ){
            // si es null pues controlamos esta excepción
            throw new IllegalArgumentException("ERROR. El nombre es null y el campo es NOT NULL");
        }

        // No necesitamos chequear el teléfono que es un , int , porque
        // le digimos a la base que podía ser null. Esto pasó porque NO le
        // especificamos NOT NULL a drede porque este campo hemos decidico que
        // puede estar vacío.

        // Ahora accedemos a la base en modo WRITABLE
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Una vez conseguido el acceso anterior, vamos a añadir un
        // nuevo item
        // Y metemos la instrucción en una variable de tipo , long , para
        // medir si ha salido tod bien. Si el resultado es -1 es que
        // algo a salido mal
        long id = database.insert(P2dbEntry.TABLE_NAME, null, values);
        if (id == -1 ) {
            // Si esto ocurre dejaremos una notita en los Logs
            Log.e("LogTag", "ERROR al insertar row con " + uri);
            return null;
        }

        // Notificamos a todos los listeners que los datos han
        // cambiado para el contenido del URI de la tabla
        getContext().getContentResolver().notifyChange(uri, null);

        // Una vez que sabemos el _id de la nueva row de la tabla,
        // retornamos la nueva URI con el _id pegado al final del mismo y
        // para ello usamos la variable , id , que declaramos long unas
        // líneas más arriba.
        return ContentUris.withAppendedId(uri, id);


    }

    /**
     * Este es el delete.
     * @param uri               Para un solo item proviene de la instrucción en
     *                          AgregarActivity int rowsDeleted = getContentResolver().delete(currentItemUri, null, null);
     *                          o sea, que es el valor de currentItemUri el que ocupa
     *                          el valor en el interior de esta variable , uri ,.
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Ya estamos aquí, terminando el proceso que lleva a eliminar el registro ordenado.
        // Como, no, accedemos a la base
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Vamos a declarar una variable que recoja el número de rows deleted
        int rowsDeleted;

        // ahora hacemos los del uriMatcher
        final int match = sUriMatcher.match(uri);

        // Y lo leemos y actuamos en consecuencia
        switch (match){
            case TODA_LA_TABLA:
                // En este caso hay que borrar toda la tabla.
                // Este caso aún no lo he contemplado en las actividades de origen
                rowsDeleted = database.delete(P2dbEntry.TABLE_NAME, selection, selectionArgs);
                // Y ya. Al final del switch le return el rowsdeleted.
                break;
            case SINGLE_ITEM_ID:
                // Tenemos que modificar las claúsulas y tal.

                // Evidentemente localizaremos el item desde su _id. Así que
                // primero le decimos que queremos buscar en la columna del _id.
                selection = P2dbEntry.CN_ID + "=?";

                // Ahora le decimos qué registro de esa columna de _id es
                // el que queremos.
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };

                // Ahora ya tod ordenadito, le mandamos ejecutar la
                // instrucción.
                rowsDeleted = database.delete(P2dbEntry.TABLE_NAME, selection, selectionArgs);

                // Luego abajo ya retornamos esta varible de las filas borradas.
                break;
            // Necesitamos el default para que nos capture una eventual excepción.
            default:
                throw new IllegalArgumentException("Deletion is not suported for " + uri);

        }


        // Una pequeña medición para asegurarnos de que hay almenos una fila
        // que ha sido deleted y, si es así, avisamos a todos los listeners que
        // los datos han cambiado.
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Ahora sí que vamos a , return ,.
        return rowsDeleted;


        // Quitamos esto último porque vamos a retornar otro valor tod el rato.
        // return 0;
    }


    // Debemos completar aquí el provider para poder updatear desde la pantalla de edición
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Hasta ahora retornaba 0 este método porque retornaba las 0 filas modificadas
        // Vamos, que no updateaba ninguna y así lo hacía saber si se le preguntaba.
        // vamos a darle funcionalidad y es fácil. Se accede a la base en modo writte y
        // se updatea la fila.

        // IMPORTANTÍSIMO !! VAMOS A MODIFICAR ESTE MÉTODO Y VAMOS A DIFERENCIAR ENTRE LAS URIS
        // QUE AQUÍ SE ENVÍAN. Haremos uso del uriMatcher y discriminaremos las uris. Luego,
        // Crearemos un método aparte para updatear UN SOLO ITEM.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODA_LA_TABLA:
                // El método updateItem lo creamos nuevo para tratar abajo.
                return updateItem(uri, values, selection, selectionArgs);
            // Y ahora el que nos interesa de verdad
            case SINGLE_ITEM_ID:
                // Para este caso debemos extraer el _id de la URI para identificar
                // el item en concreto que queremos updatear.
                // Luego, en la selección haremos , "_id=?" y también los
                // selection arguments serán un String Array conteniendo el actual ID.
                selection = P2dbEntry.CN_ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                // Y con estas dos cositas modificadas ya pasamos los parámetros al método
                // creado nuevo y al cual hemos llamado updateItem.
                return updateItem(uri, values, selection, selectionArgs);
            // Le metemos un default al switch para controlar la excepción
            default:
                throw new IllegalArgumentException("Update no es soportado para " + uri);

        }

    }

        // EL BLOQUE DE CÓDIGO QUE HABÍA AQUÍ HA SIDO PASADO AL MÉTODO updateItem



    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Accedemos a la base que ya hemos declarado arriba.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Damos la orden del updateado y recogemos el número de rows
        // afectadas por el mismo que será 1. En tod caso será más
        // que 0.
        int rowsUpdated = database.update(P2dbEntry.TABLE_NAME, values, selection, selectionArgs);



        // IMPORTANTE !!!!
        // Si 1 o más rows se han visto afectadas, entonces noficamos a todos
        // los LISTENERs que los datos han cambiado.
        // (el segundo parámetro es un contentObserver que no tengo ni idea ni lo que es).
        if (rowsUpdated != 0 ){
            getContext().getContentResolver().notifyChange(uri, null);
        }



        // Por último, como es lógico, retornamos el número de filas updateadas
        return rowsUpdated;
    }



}




























