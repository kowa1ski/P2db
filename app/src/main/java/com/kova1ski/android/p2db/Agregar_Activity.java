package com.kova1ski.android.p2db;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
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

    // Aquí mismo declaramos la variable para controlar el teclado.
    // Es que estoy viendo que nos va a hacer falta en varios métodos y, es por eso
    // que la tengo que sacar aquí fuera.
    InputMethodManager tecladoPaFuera;


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

        nombre = editTextNombre.getText().toString().trim(); // añadimos trim() para quitar
                                                             // esos molestos espacios al final.
        telefono = parseInt(editTextTelefono.getText().toString());

        // Vamos a chequear que NO estamos tratando con un currentItem ni
        // y que todos los editText están vacíos. En ese caso no hay nada que
        // hacer y vamos a retornar sin hacer absolutamente nada.
        if (currentItemUri == null && TextUtils.isEmpty(nombre)
                && telefono == 0 ){
            // Si esta comprobación es positiva es que tod está vacío y por
            // tanto nada hay por hacer y se devuelve la acción sin más.
            return;
        }

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


        // Ahora debemos determinar si estamos tratando con la intención
        // de insertar un nuevo registro o, por el contrario, lo que
        // queremos es usar este procedimiento para que el comportamiento del botón
        // sea el de edición de un item previamente conocido.
        if (currentItemUri == null){

            // ESTE BLOQUE QUE OPERABA FUERA AHORA ESTÁ DENTRO DEL IF PARA
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

        } else {
            // En este caso, en este caso contrario, en este caso en el que no es un nuevo
            // registro, es que se trata de la edición.
            // Para la edición vamos a "updatear" el registro. Los parámetros que necesitamos
            // serán únicamente el currentItem, evidentemente y por otro lado
            // el contenedor de valores que ya tenemos cargado así que, mediante
            // un , getContentResolver() lo tenemos tod controlado.
            // La ejecución de esta línea de código da como resultado un número de
            // columnas "updateadas" por lo que si medimos que las mismas no
            // sean 0, estaremos seguros de que la edición se ha realizado.
            int rowsAffectedFilasAfectadas = getContentResolver().update(currentItemUri, contentValues, null, null);

            // Lo dicho, ahora medimos el resultado y se lo decimos al usuario
            if (rowsAffectedFilasAfectadas == 0) {
                // Si no ha habido filas afectadas es que no se ha realizado ingún update.
                Toast.makeText(this, "EDICIÓN DE ITEM FALLIDA", Toast.LENGTH_SHORT).show();
            } else {
                // Pero si devuelve que sí que hay alguna fila, quiere decir que por lo
                // menos una de ellas ha sido updateada. Y lo decimos también.
                Toast.makeText(this, "EDICIÓN REALIZADA CON ÉXITO", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Las filas afectadas han sido: "+rowsAffectedFilasAfectadas, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Y el URI usado es el : "+currentItemUri, Toast.LENGTH_LONG).show();
            }

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

            // Y algo de mi cosecha que acabo de ver por internet en hermosaprogramacion. Voy
            // a hacer que el texto del nombre esté selecionado.
            editTextNombre.selectAll(); // joder que está guay

            // Y ahora quiero que el teclado salga, o sea, que después de lo anteior, de
            // haber seleccionado la palabra, el teclado esté fuera para empezar a editar de
            // inmediato. Para ello he estado mirando en hermosaprogramación y tengo que hacer
            // uso de la clase InputMethodMAnager y llamar a un procedimiento que he encontrado
            // mirando por internet.
            tecladoPaFuera =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            // Nos aseguramos que tiene el foco.
            editTextNombre.requestFocus();
            // Y ahora sí sacamos el teclado.
            tecladoPaFuera.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

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

    /**
     * Llamamos al menú sobreescribiendo aquí. Yo he puesto la palabra , menu , y
     * ya me ha sugerido el método:
     * @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    return super.onCreateOptionsMenu(menu);
                        }
     Ahora hay que inflar el xml.
     Y lo hago fijándome en cómo ya está hecho en el main.

      */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agregar, menu); // Y yaaaa!!!
        return true; // Y aquí true
    }

    // Vamos a darle funcionalidad al botón de borrar. Comenzamos asignándole una
    // función sencilla, un toast.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Hacemos los del id
        int id = item.getItemId();
        switch(id){
            case R.id.action_borrarAmigo:
                Toast.makeText(this, "ESTA ACCIÓN BORRARÁ EL CURRENT REGISTRO", Toast.LENGTH_SHORT).show();

                // Vamos a crear un método de borrar y ordenaremos ir hasta allí
                // borrarRegistro(); // Esto ya no servirá aquí

                // Ahora vamos a hacer otra cosa chula.
                // Ahora vamos a generar un mensaje de confirmación para asegurarnos
                // de que verdaderamente lo que queremos es borrar el registro
                // seleccionado. Para ello vamos a llamar a otro procedimiento que será el que
                // saque la ventanita de confirmación. Si allí, en aquella ventana, ya le
                // indicamos que sí, será entonces cuando llamaremos (allí, allí) al
                // procedimiento de borrarRegistro().
                showDeleteConfirmationDialogVentanitaDeConfirmacion();

                // Y no nos olvidamos del return true
                return true;
        }


        // Este return hay que dejarlo tal cual para que funcione tod
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialogVentanitaDeConfirmacion() {

        // Vamos a crear un AlertDialog.Builder y comprondremos un mensaje, y
        // tendremos dos click listeners para los botones de positivo y
        // negativo dentro del diálogo.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Como no, ahora hay que buildeArlo, jeje.

        // Primero el título.
        builder.setMessage("ESTÁS A PUNTO DE ELIMINAR ESTE REGISTRO");

        // Y ahora los botones

        // Primero el botón positivo
        // Dentro de los parámetros es fácilo porque le ponemos el mensaje
        // y creamos un nuevo listener que nos sirve para controlar su pulsación.
        builder.setPositiveButton("SÍ, ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Usamos este botón para eliminar de una vez por todas
                // simplemente llamando al método que ya hemos creado en el comit
                // anterior.
                borrarRegistro();
            }
        });

        // Ahora vamos a por el botón del negativo.
        builder.setNegativeButton("NoOOOO!!!, PARA. NO elimines", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // El usuario ha clickado en el botón de cancelar, o sea
                // es este de NoOOOO!!!. simplmente, descartamos este diálogo y
                // continuamos en la pantalla de edición.
                // APUNTE: hay que comprobar que el valor del , dialog , no
                // es null. Que no sé por qué pero es así.
                if (dialog != null){
                    dialog.dismiss();
                }

            }
        });

        // Una vez confeccionado tod este método, lo finalizamos rematando la
        // faena. HAY QUE MOSTRARLO !!! JAJAJAJA.
        // Para ello sí que ahora creamos un objeto de la clase AlertDialog y
        // le metemos las instrucciones que hemos estado programando.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void borrarRegistro() {

        //Una pequeña comprobación
        if(currentItemUri != null){
            // Y ahora sí al lío
            int rowsDeleted = getContentResolver().delete(currentItemUri, null, null);

            // No nos olvidemos que, con esta instrucción estamos enviando el
            // flujo de trabajo al Provider. Allí tiene que estar construido
            // el método detele.

            // Y ya estaría hecho así que vamos a comunicar al usuario que se
            // ha llevado tod a cabo tal y como estaba planeado.
            if (rowsDeleted == 0){
                // Si no hay filas borradas es que, evidentemente, no ha habido deletes
                Toast.makeText( this, "ERROR!! NO SE HA ELIMINADO NINGÚN REGISTRO !", Toast.LENGTH_SHORT).show();

            } else {
                // Si sí que hay filas borradas es que se ha borrado el registro.
                Toast.makeText(this, "YeEEEEaAAAA REGISTRO ELIMINADO", Toast.LENGTH_SHORT).show();

            }
        }
        // hacemos que el teclado no vaya a salir cuando cambiemos de pantalla.
        // Vaya por delante que esto se me ha ocurrido a mi. El tema es que
        ocultarTeclado();




        // Y ahora no nos olvidamos de cerrar la actividad
        finish();
    }

    private void ocultarTeclado() {
        tecladoPaFuera.hideSoftInputFromWindow(editTextNombre.getWindowToken(), 0);
    }
}
















