package com.kova1ski.android.p2db;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kova1ski.android.p2db.data.P2dbContract;

import static com.kova1ski.android.p2db.data.P2dbContract.*;
import static java.lang.Integer.parseInt;

public class Agregar_Activity extends AppCompatActivity {

    EditText editTextNombre;
    EditText editTextTelefono;

    // Las variables de prueba del anterior comit nos servirán así que las
    // sacamos aquí para hacerlas accesibles.
    private String nombre;
    private int telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //--------------------------
                // Este bloque es para probar que se recogen
                // bien los valores de los editText usando el Snackbar
                // Probado hecho que se recogen. Ahora mantenemos aquí la
                // recogida de estos datos y luego llamaremos a estas variables
                // desde otros sitios de la actividad. Vamos que este trabajo
                // de recogida de datos lo damos por correcto a falta de pulir
                // algún detalle. Por ahora lo dejamos así, bruto, y vamos viendo.

                // cargadas las variables vamos a crear un método y a llamarlo para que
                // ejecute el código de inserción de nuevo registro.
                guardarRegistro();
                // Una vez hecho, volvemos a la actividad Main
                finish();

                // ---------------------------fin del bloque
                Snackbar.make(view, "Mira cómo recojo los campos de " + nombre + " y " + telefono, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        editTextNombre = (EditText) findViewById(R.id.editTextAgregarNombre);
        editTextTelefono = (EditText) findViewById(R.id.editTextAgregarNumero);










    }

    private void guardarRegistro() {

        nombre = editTextNombre.getText().toString();
        telefono = parseInt(editTextTelefono.getText().toString());

        /**
         * En primer lugar vamos a insertar algunos registros en la base.
         * Para ello vamos a establecer la estructura básica de inserción
         * de nuevo registro.
         */
        // Ya tenemos las variabes de los editText cargadas. Ahora simplemente
        // cargamos un contentValues.
        ContentValues contentValues = new ContentValues();
        contentValues.put(P2dbEntry.CN_NOMBRE, nombre);
        contentValues.put(P2dbEntry.CN_TELEFONO, telefono);

        // Insert a new pet into the provider, returning the content URI for the new pet.
        // Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
        // Insertamos el nuevo item en el provider y
        // la ejecución de ese código nos devuelve la , URI , del nuevo registro.
        Uri newUri = getContentResolver().insert(P2dbEntry.CONTENT_URI, contentValues);

        // Y ahora vamos a comprobar que el registro ha entrado en la base
        // de manera correcta.
        if(newUri == null){
            // si es null es que ha habido algún error en el código
            Toast.makeText(this, "ERROR, registro no insertado", Toast.LENGTH_LONG).show();

        } else {
            // En caso contrario, el registro se ha insertado de manera correcta feliz.
            Toast.makeText(this, "REGISTRO AÑADIDO CORRECTAMENTE", Toast.LENGTH_LONG).show();
        }


    }

}














