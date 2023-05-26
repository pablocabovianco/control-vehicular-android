package ar.edu.untref.controlvehicular;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class AgregarEventoActivity extends AppCompatActivity {
    EventosViewModel viewModel;

    private ArrayAdapter<String> mAdapter;
    //Seteo valores de LV
    Spinner tipoEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_evento);

        this.viewModel = ViewModelProviders.of(this).get(EventosViewModel.class);
        //Asigno valores de LV
        tipoEvento = findViewById(R.id.tipoEvento);
        String[] tiposEvento = new String[]{"Por Kilometros", "En fecha"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tiposEvento);

        tipoEvento.setAdapter(adapter);

        Button agregarBtn = findViewById(R.id.btnGuardarEvento);
        agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarEvento();
            }
        });
        findViewById(R.id.btnCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onCancelar();}
        });


    }
    public void agregarEvento(){
        //Obtengo las variables de pantalla
        EditText campoTitulo = (EditText) findViewById(R.id.textTitulo);
        String titulo = campoTitulo.getText().toString();

        EditText campoKilometros = (EditText) findViewById(R.id.textKilometros);
        int kilometros = Integer.parseInt(campoKilometros.getText().toString());
        //Asigno segun el tipo para cargar en la tabla
        Boolean esPorKm = tipoEvento.getSelectedItem().toString() == "Por Kilometros" ? Boolean.TRUE : Boolean.FALSE;

        //Creo el evento
        //TODO debe agregar kilometros totales + los ingresados
        Eventos nuevoEvento = new Eventos(titulo,esPorKm,0, kilometros);

        //Lo agrego a la base y actualizo la lista
        this.viewModel.insertEvento(nuevoEvento);

        //Limpio los campos
        campoTitulo.getText().clear();
        campoKilometros.getText().clear();

        //Vuelvo a la pantalla anterior
        Intent intent = new Intent(this, MostrarEventosActivity.class);
        startActivity(intent);
    }

    public void onCancelar(){
        //Vuelvo a la pantalla anterior
        Intent intent = new Intent(this, MostrarEventosActivity.class);
        startActivity(intent);
    }
}