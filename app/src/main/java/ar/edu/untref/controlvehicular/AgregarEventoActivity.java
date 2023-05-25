package ar.edu.untref.controlvehicular;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AgregarEventoActivity extends AppCompatActivity {
    EventosViewModel viewModel;

    private ListView listaEventos;
    private ArrayAdapter<String> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_evento);

        this.viewModel = ViewModelProviders.of(this).get(EventosViewModel.class);

        Button agregarBtn = findViewById(R.id.btnGuardarEvento);
        //Muestro los eventos cargados
        listaEventos = findViewById(R.id.listaEventos);
        agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarEvento();
            }
        });

        Button borrarBtn = findViewById(R.id.btnBorrarEventos);
        borrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarEventos();
            }
        });

    }
    public void agregarEvento(){
        //Obtengo las variables de pantalla
        EditText campoTitulo = (EditText) findViewById(R.id.textTitulo);
        String titulo = campoTitulo.getText().toString();

        EditText campoDescripcion = (EditText) findViewById(R.id.textDescripcion);
        String descripcion = campoDescripcion.getText().toString();

        EditText campoKilometros = (EditText) findViewById(R.id.textKilometros);
        int kilometros = Integer.parseInt(campoKilometros.getText().toString());

        //Creo el evento
        EventoPorKilometraje nuevoEvento = new EventoPorKilometraje(kilometros, titulo, descripcion);

        //Lo agrego a la base y actualizo la lista
        this.viewModel.insertEvento(nuevoEvento);

        //Limpio los campos
        campoTitulo.getText().clear();
        campoDescripcion.getText().clear();
        campoKilometros.getText().clear();
    }

    public void limpiarEventos(){

        this.viewModel.deleteAllEventos();
        //Limpio la lista de vista
        List<String> titulosDeEventos = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titulosDeEventos);
        listaEventos.setAdapter(mAdapter);
    }


}