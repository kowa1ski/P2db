package com.kova1ski.android.p2db;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.kova1ski.android.p2db.data.P2dbContract;

import static com.kova1ski.android.p2db.data.P2dbContract.*;

/**
 * Created by Usuario on 27/08/2017.
 */

public class P2bdCursorAdapter extends CursorAdapter {
    // Después de lo comentado en el anterior comit, resulta que el , int flags , no
    // es necesario. Bueno, a medias. Sí que es necesario porque se lo vamos a marcar a 0, pero
    // ese valor va a ser fijo así que de la referencia de variables lo quitamos.
    public P2bdCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */ );
    }

    /**
     *
     * @param context   El contexto
     * @param cursor    El cursor desde el cual se obtienen los datos. El cursor es
     *                  movido a la posición correcta.
     * @param parent    El parent al cual la nueva vista es adjuntada(incorporada).
     * @return          La nueva lista de items creada.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Le decimos que infle desde el contexto que se pasa y le decimos la lista
        // que vamos a usar. En este caso no me complico y cojo uno que trae android por defecto y
        // que ya tiene dos items.
        // Por otro lado le decimos que el , ViewGroup , es el parent que se le pasa.
        // Tod eso es el retorno en sí mismo.
        return LayoutInflater.from(context).inflate(android.R.layout.two_line_list_item, parent, false);

    }

    /**
     *
     * @param view      Es la vista existente y se ha retornado en el método anterior , newView ,.
     * @param context   El contexto.
     * @param cursor    El cursor desde el que se obtiene la información. Este cursor ya se ha
     *                  movido a la fila correcta.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Buscamos los views individuales que vamos a modificar para el listado
        // de items.
        // Los buscamos con , android.R.id.XXXXX , porque forman parte
        // de la librería original proporcionada por el Studio.
        TextView primerTextView = (TextView) view.findViewById(android.R.id.text1);
        TextView segundoTextView = (TextView) view.findViewById(android.R.id.text2);

        // Localizamos las columnas en las cuales estamos interesados
        int text1ColumIndex = cursor.getColumnIndex(P2dbEntry.CN_NOMBRE);
        int text2ColumIndex = cursor.getColumnIndex(P2dbEntry.CN_TELEFONO);

        // Leemos ahora los datos que nos interesan de esas columnas en
        // esos , ColumIndex.
        String text1EnString = cursor.getString(text1ColumIndex);
        String text2EnString = cursor.getString(text2ColumIndex);

        // ES FACIL. Ahora asignamos los Strings a los dos views.
        primerTextView.setText(text1EnString);
        segundoTextView.setText(text2EnString);

    }
}

















