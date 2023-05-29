package ar.edu.untref.controlvehicular;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AgregarEventoActivity extends AppCompatActivity {
    EventosViewModel viewModel;

    private ArrayAdapter<String> mAdapter;
    Spinner tipoEvento;
    String fechaDeEvento;
    private int mYear,mMonth,mDay;
    public int yearEvento, monthEvento, dayEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_evento);

        this.viewModel = ViewModelProviders.of(this).get(EventosViewModel.class);
        TextView campoKilometros = findViewById(R.id.textKilometros);
        TextView labelKilometros = findViewById(R.id.labelKilometros);

        TextView campoFecha = findViewById(R.id.fechaEvento);
        TextView labelFecha = findViewById(R.id.labelFecha);


        //Asigno valores de LV
        tipoEvento = findViewById(R.id.tipoEvento);
        String[] tiposEvento = new String[]{"Por Kilometros", "En fecha"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tiposEvento);
        //Muestro campo de fecha o km segun corresponda
        tipoEvento.setAdapter(adapter);
        tipoEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(tipoEvento.getSelectedItem().toString() == "Por Kilometros"){
                    campoKilometros.setVisibility(View.VISIBLE);
                    labelKilometros.setVisibility(View.VISIBLE);
                    campoFecha.setVisibility(View.GONE);
                    labelFecha.setVisibility(View.GONE);
                } else {
                    campoKilometros.setVisibility(View.GONE);
                    labelKilometros.setVisibility(View.GONE);
                    campoFecha.setVisibility(View.VISIBLE);
                    labelFecha.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                campoKilometros.setVisibility(View.VISIBLE);
                campoFecha.setVisibility(View.GONE);
            }
        });
        //Manejo de fecha
        EditText fechaDeEvento = (EditText) findViewById(R.id.fechaEvento);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                fechaDeEvento.setText(sdf.format(myCalendar.getTime()));
            }


        };

        fechaDeEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(AgregarEventoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox

                                if (year < mYear)
                                    view.updateDate(mYear,mMonth,mDay);

                                if (monthOfYear < mMonth && year == mYear)
                                    view.updateDate(mYear,mMonth,mDay);

                                if (dayOfMonth < mDay && year == mYear && monthOfYear == mMonth)
                                    view.updateDate(mYear,mMonth,mDay);

                                fechaDeEvento.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                yearEvento = year;
                                monthEvento = monthOfYear+1;
                                dayEvento = dayOfMonth;
                            }
                        }, mYear, mMonth, mDay);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis());
                dpd.show();

            }
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
        int kilometros;
        Boolean esPorKm;
        //Asigno segun el tipo para cargar en la tabla
        if (tipoEvento.getSelectedItem().toString() == "Por Kilometros"){
            esPorKm = Boolean.TRUE;
            fechaDeEvento = "0";
            kilometros = Integer.parseInt(campoKilometros.getText().toString());
        } else {
            esPorKm = Boolean.FALSE;
            kilometros = 0;
            fechaDeEvento = "" + yearEvento + (Integer.toString(monthEvento).length() == 1 ? ("0" + monthEvento) : monthEvento) + dayEvento;
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

}