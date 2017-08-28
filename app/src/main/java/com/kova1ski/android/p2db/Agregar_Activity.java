package com.kova1ski.android.p2db;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.kova1ski.android.p2db.data.P2dbContract.P2dbEntry;
import static java.lang.Integer.parseInt;



// En este punto y, dado que vamos a usar esta pantalla, también para
// la edición de los items, debemos implementar el LoaderManager.
// Lo implementamos y tal y al , LoaderManager.LoaderCallbacks , le tenemos que
// añadir , <Cursor> , y también generar sus métodos los cuales se añadirán
// al final de la clase.
public class Agregar_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    //
    // Acabamos de implementar el LoaderManager así que antes de nada
    // vamos a establecer el IDENTIFICADOR para el loader.
    private static final int EXISTING_ITEM_LOADER = 0 ;

    // Declaramos una variable que contendrá el URI para un
    // item que ya exista, es decir, que venga de la pantalla principal
    // para ser editado en esta. El valor null querrá decir que habrá que
    // tratar esta pantalla para un nuevo item dado que no hay ninguno
    // que se haya ordenado para editar y, por tanto si es null significa
    // que estamos generando un nuevo registro a la base.
    private Uri currentItemUri;

    EditText editTextNombre;
    EditText editTextTelefono;

    // Las variables de prueba del anterior comit nos servirán así que las
    // sacamos aquí para hacerlas accesibles.
    private String nombre;
    private int telefono;


    // Esta técnica que vamos a utilizar es fantástica, no
    // pierdas detalle. Vamos a poner un listener que escuche
    // Su misión será escuchar cualquier toque en los edittexty
    // asumiremos que, si existe ese toqe es que el item ha
    // sido cambiado para su edición
    //
    // Sin más vamos allá. Vamos a declarar primero la variable
    // booleana del cambio.
    private boolean itemHasChanged = false;

    // Y, claro, ahora toca DECLARAR el listener
    // y cuidado porque está fuera del Oncreate así que todavía no funciona,
    // solamente lo hemos declarado para poder usarlo luego.
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // Simplemente si existe un toque, pues
            // cambiamos el valor de la variable del cambio.
            itemHasChanged = true;
            return false;
        }
    // Y le tenemos que poner , ; , aquí.
    };







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

        // Comienza lo chulo. Ahora vamos a examinar el intent
        // que lanzó esta actividad. Usando , getIntent() , y
        // , getData , vamos a obtener lo que nos interesa de la URI
        // asociada al intent del que procede la llamada a esta
        // actividad.
        // Examinamos el intent.
        Intent intent = getIntent();
        currentItemUri = intent.getData(); // Lo tenemos!!

        // Y ahora vamos a ver qué hay dentro de ese, currentItem ,
        // Si no hay nada, o sea null, entonces significa que lo
        // que queremos es insertar un nuevo registro pero si contiene
        // algo es que se refiere a un item que ya existe y, por lo tanto,
        // lo que tenemos que hacer es editarlo.
        if (currentItemUri == null) {
            // Esto tendría la intención de ser un nuevo item.
            // Vamos a hacer algo ya que estamos aquí y vamos a
            // cambiar el título de la actividad para que ponga algo descriptivo.
            // (en este comit convertimos el texto raw en variable)
            setTitle(getString(R.string.titulo_agregar_item));
        } else {
            // Si estamos en el , else , es que la URI traía regalito así
            // que vamos a cargar el loader. Sólo a mandar su carga.
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
            // Y una vez cargado, el flujo de este Loader continua en
            // los métodos de abajo

            // Y de paso le cambiamos el título a la actividad
            setTitle("EDITAR ESTE REGISTRO");
        }







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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Si estamos aquí es que hemos mandado cargar este loader y
        // queremos que cargue los datos en los campos correspondientes
        // para ser editados. Para ello lo primero que debemos hacer es tachán
        // tachán... EXACTO: generar un cursor para leer de él. En nuestro
        // caso, que tenemos los cojones como el caballo de Espartero, vamos
        // a cargar un CURSORLOADER.
        // Primero ya sabes, la projection.
        String[] projection = {
                P2dbEntry.CN_ID,
                P2dbEntry.CN_NOMBRE,
                P2dbEntry.CN_TELEFONO
        }; // Y este punto y coma que hay que poner

        // Así que el siguiente loader ejecutará el método query del Content Provider.
        // El resultado será exactamente lo que devuelva este método, y ya.
        return new CursorLoader(this,   // Parent activity
                currentItemUri,         // En la query montamos el contenido de la URI para el item actual
                projection,             // Las columnas que vamos a querer extraer
                null,                   // No selection clause
                null,                   // No selection arguments
                null                    // Default short order
        );



    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Y una vez cargado el cursor, en el método anterior, vamos a
        // comprobar que ha salido bien. Si el cursor es null o tiene menos de una fila
        // es que no hay filas cargadas en el cursor.
        // ATENTOS que el método llama al cursor , data ,.
        if (data == null || data.getCount() < 1){
            return;

        }

        // Si ha salido tod bien, vamos al lío yvamos a obtener primero
        // todos los datos que nos interesan.

        // Empezamos con esta instrucción que lo que hace es llevar el
        // puntero al principio del cursor y empezar a leer ahí
        if (data.moveToFirst()){
            // Buscamos las columnas con los atributos del item.
            int nombreColumIndex = data.getColumnIndex(P2dbEntry.CN_NOMBRE);
            int telefonoColumIndex = data.getColumnIndex(P2dbEntry.CN_TELEFONO);

            // Después de saber las columnas, extraemos los valores
            String nombreParaEditar = data.getString(nombreColumIndex);
            int telefonoParaEditar = data.getInt(telefonoColumIndex);

            // Y, como no, actualizamos los campos para que se vea
            // lo que queremos editar.
            // Tenemos un poco de cuidado con diferenciar los datos de
            // String y los , int , y arreglado.
            editTextNombre.setText(nombreParaEditar);
            editTextTelefono.setText(Integer.toString(telefonoParaEditar));

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Por último, a la salida del loader, tenemos que acordarnos de dejar
        // los editText en blanco.
        editTextNombre.setText(R.string.texto_vacio);
        editTextTelefono.setText(R.string.texto_vacio);

    }
}














