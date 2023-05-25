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

import java.util.ArrayList;
import java.util.List;


public class AgregarEventoActivity extends AppCompatActivity {
    EventosViewModel viewModel;

    private ArrayAdapter<String> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_evento);

        this.viewModel = ViewModelProviders.of(this).get(EventosViewModel.class);

        Button agregarBtn = findViewById(R.id.btnGuardarEvento);
        agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarEvento();
            }
        });

    }
    public void agregarEvento(){
        //Obtengo las variables de pantalla
        EditText campoTitulo = (EditText) findViewById(R.id.textTitulo);
        String titulo = campoTitulo.getText().toString();

        EditText campoKilometros = (EditText) findViewById(R.id.textKilometros);
        int kilometros = Integer.parseInt(campoKilometros.getText().toString());

        //Creo el evento
        //TODO debe agregar kilometros totales + los ingresados
        Eventos nuevoEvento = new Eventos(titulo,Boolean.TRUE,null, kilometros);

        //Lo agrego a la base y actualizo la lista
        this.viewModel.insertEvento(nuevoEvento);

        //Limpio los campos
        campoTitulo.getText().clear();
        campoKilometros.getText().clear();

        //Vuelvo a la pantalla anterior
        Intent intent = new Intent(this, MostrarEventosActivity.class);
        startActivity(intent);
    }



}