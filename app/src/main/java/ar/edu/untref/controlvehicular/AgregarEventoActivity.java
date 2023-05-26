package ar.edu.untref.controlvehicular;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AgregarEventoActivity extends AppCompatActivity {
    EventosViewModel viewModel;

    private ArrayAdapter<String> mAdapter;
    Spinner tipoEvento;
    String fechaDeEvento;

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

        EditText fechaDeEvento = (EditText) findViewById(R.id.fechaEvento);
        fechaDeEvento.setOnClickListener(view -> {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        Button agregarBtn = findViewById(R.id.btnGuardarEvento);
        agregarBtn.setOnClickListener(view -> agregarEvento());
        findViewById(R.id.btnCancelar).setOnClickListener(view -> onCancelar());


    }
    public void agregarEvento(){
        //Obtengo las variables de pantalla
        EditText campoTitulo = (EditText) findViewById(R.id.textTitulo);
        String titulo = campoTitulo.getText().toString();

        EditText campoKilometros = (EditText) findViewById(R.id.textKilometros);
        int kilometros = Integer.parseInt(campoKilometros.getText().toString());
        Boolean esPorKm;
        //Asigno segun el tipo para cargar en la tabla
        if (tipoEvento.getSelectedItem().toString() == "Por Kilometros"){
            esPorKm = Boolean.TRUE;
            fechaDeEvento = "0";
        } else {
            esPorKm = Boolean.FALSE;
            kilometros = 0;
        }
        //Creo el evento
        //TODO debe agregar kilometros totales + los ingresados
        Eventos nuevoEvento = new Eventos(titulo,esPorKm,Integer.parseInt(fechaDeEvento), kilometros);

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
    //Manejo de fecha
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            fechaDeEvento = "" + year + month + day;
        }

    }
}