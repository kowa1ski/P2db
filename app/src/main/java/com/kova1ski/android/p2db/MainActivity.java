package com.kova1ski.android.p2db;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.kova1ski.android.p2db.data.P2dbContract;

import static com.kova1ski.android.p2db.data.P2dbContract.*;

// En este punto de comit nos vemos obligados a , implements , LoaderManager.ETC.... , e
// implementar los 3 métodos
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    // Esto no sé ahora mismo por qué lo pondo pero seguro que es sano
    // declarar el loader y establecerlo a 0.
    private static final int ITEMS_LOADER = 0;
    // delaramos el , CursorAdapter ,.
    P2bdCursorAdapter p2bdCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.pasas_de_actividad, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, Agregar_Activity.class);
                startActivity(intent);

            }
        });


        // Buscamos la lista donde publicamos los registros que devulelve el cursor.
        ListView itemListView = (ListView) findViewById(R.id.listaItemsEnContentMain);

        // Llamamos al adapter para crear una lista para cada registro
        // Todavía no hay ningún registro (hasta que el loader acabe) así que
        // lo pasamos en null para el cursor.
        p2bdCursorAdapter = new P2bdCursorAdapter(this, null);
        itemListView.setAdapter(p2bdCursorAdapter);


        // No sé muy bien cómo funciona esto pero sí que veo que sirve para
        // inicializar el , Loader ,. El Android Studio me pide sólo dos
        // parámetros dentro de los paréntesis, o al menos eso parece. Lo que
        // sí es cierto es que es así y añado más. El , this , sólo funciona si
        // arriba se implementa el Loader.
        getLoaderManager().initLoader(ITEMS_LOADER, null, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Y ahora a completar el loader. Se han generado estos
    // 3 métodos con la bombilla al implementar el loader
    // allá arriba.
    //
    // El primero de ellos es decirle al loader lo que tiene que
    // hacer cuando es creado.

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Definimos una , projection , que especifica las columnas
        // de la tabla de la tabla que nos interesa.
        String[] projection = {
                P2dbEntry.CN_ID,
                P2dbEntry.CN_NOMBRE,
                P2dbEntry.CN_TELEFONO
        };

        // Este loader ejecutará el método , query , del ContentProvider en un
        // hilo en background.
        // Y EFECTIVAMENTE ES LO QUE DEVOLVEMOS EN ESTE MÉTODO
       return new CursorLoader(this,    // Qué va a ser, pues el contexto
               P2dbEntry.CONTENT_URI,   // URI del Content Provider para consultar
               projection,              // Columnas a incluir en el Cursor resultante
               null,                    // Es la CLAUSULA, no se selecciona nada
               null,                    // No selection arguments
               null);                   // Default short order


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Actualizamos el CursorAdapter con este nuevo Cursor el cual
        // contiene los datos de los items ya actualizado.
        //
        // Que no llame a error la variable , data ,. Esa variable es el
        // Cursor que viene al método y que la llama data. Es sólo
        // un nombre de variabe de Cursor que podría ser Antonio.
        p2bdCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback es llamado cuando los datos necesita ser eliminado.
        // En el null me está pidiendo el nuevoCursor así que
        // se lo paso en null.
        p2bdCursorAdapter.swapCursor(null);

    }
}
