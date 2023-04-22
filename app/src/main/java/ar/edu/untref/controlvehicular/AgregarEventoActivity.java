package ar.edu.untref.controlvehicular;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AgregarEventoActivity extends AppCompatActivity {
    EventosViewModel viewModel;

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

        //Lo agrego a la base
        this.viewModel.insertEvento(nuevoEvento);
    }

    public void limpiarEventos(){
        this.viewModel.deleteAllEventos();
    }


}