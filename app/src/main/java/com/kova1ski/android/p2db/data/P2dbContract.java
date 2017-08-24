package com.kova1ski.android.p2db.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Usuario on 24/08/2017.
 */


// Esta clase no hace falta que extienda de ningua
public class P2dbContract {

    // Le nombramos un constructor vacío para que no haya malos entendidos
    P2dbContract(){}

    // declaramos el Content Authority
    // con el nombre del paquete
    private static final String CONTENT_AUTHORITY = "com.kova1ski.android.p2db" ;

    // y declaramos la base para la , uri , del content provider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // posible path, o sea, una terminación posible de la uri.
    // Le doy el nombre de path segment porque es la denominación
    // que me pide en el , withAppendedPath ,.
    private static final String PATH_SEGMENT = "p2tabla";

    // CLASE INTERNA que nombramos para definir los valores de las columnas.
    // Para mejor compatibilidad, le implementamos , Basecolumns ,.
    // No lo extendemos, ojo, lo implementamos.
    public static final class P2dbEntry implements BaseColumns {

        // Contenido del uri para acceder a la tabla en el provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SEGMENT);

        // ------------- NOMBRE DE LA TABLA
        // TODO no estoy muy convencido de lo que he hecho.
        // En el ejemplo se asigna el valor a la variable entre comillas con
        // un "p2tabla" pero es que lo he visto y es idéntico al PATH_SEGMENT.
        // Creo que voy a dejarlo así.
        public static final String TABLE_NAME = PATH_SEGMENT;

        // Ahora el nombre de las columnas
        /**
         * _id
         * INTEGER
         */
        public static final String CN_ID = BaseColumns._ID;
        /**
         * nombre
         * TEXT
         */
        public static final String CN_NOMBRE = "nombre";
        /**
         * telefono
         * INTEGER
         */
        public static final String CN_TELEFONO = "telefono";

        // El MIME del tipo content uri.
        // Lo que traduzco, creo, como las dos
        // direcciones posibles PERO en Strings

        // LA PRIMERA DERECCIÓN ES LA DE LA LISTA COMPLETA
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_SEGMENT ;

        // Y LA OTRA DIRECCIÓN ES LA DE UN SÓLO ITEM
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_SEGMENT ;
    }

}





