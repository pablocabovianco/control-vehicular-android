package ar.edu.untref.controlvehicular;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditarEventoActivity extends AppCompatActivity {

    EventosViewModel viewModel;

    private ArrayAdapter<String> mAdapter;
    Spinner tipoEvento;
    String fechaDeEvento;
    private int mYear,mMonth,mDay;
    public int yearEvento, monthEvento, dayEvento;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_evento);

        this.viewModel = ViewModelProviders.of(this).get(EventosViewModel.class);
        TextView campoKilometros = findViewById(R.id.textKilometros);
        TextView labelKilometros = findViewById(R.id.labelKilometros);

        TextView campoFecha = findViewById(R.id.fechaEvento);
        TextView labelFecha = findViewById(R.id.labelFecha);

        Intent intent = getIntent();
        id = intent.getIntExtra(MostrarEventosActivity.EXTRA_TEXT_ID, -1);
        EditText campoTitulo = (EditText) findViewById(R.id.textTitulo);
        campoTitulo.setText(intent.getStringExtra(MostrarEventosActivity.EXTRA_TEXT_TITULO));
        //Variable temporal para mostrar correcta la LV
        int kmTemp = intent.getIntExtra(MostrarEventosActivity.EXTRA_TEXT_KM,-1);
        campoKilometros.setText(String.valueOf(intent.getIntExtra(MostrarEventosActivity.EXTRA_TEXT_KM,-1)));

        campoFecha.setText(formateoFecha(intent.getIntExtra(MostrarEventosActivity.EXTRA_TEXT_FECHA, -1)));
        Boolean esPorKilometro = intent.getBooleanExtra(MostrarEventosActivity.EXTRA_TEXT_POR_KILOMETROS, false);


        //Asigno valores de LV
        tipoEvento = findViewById(R.id.tipoEvento);
        String[] tiposEvento = new String[]{"Por Kilometros", "En fecha", "Ambos"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tiposEvento);
        //Muestro campo de fecha o km segun corresponda
        tipoEvento.setAdapter(adapter);
        tipoEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(esPorKilometro){
                    campoKilometros.setVisibility(View.VISIBLE);
                    labelKilometros.setVisibility(View.VISIBLE);
                    campoFecha.setVisibility(View.GONE);
                    labelFecha.setVisibility(View.GONE);
                    tipoEvento.setSelection(0);
                } else if(kmTemp == 0){
                    campoKilometros.setVisibility(View.GONE);
                    labelKilometros.setVisibility(View.GONE);
                    campoFecha.setVisibility(View.VISIBLE);
                    labelFecha.setVisibility(View.VISIBLE);
                    tipoEvento.setSelection(1);
                } else {
                    campoKilometros.setVisibility(View.VISIBLE);
                    labelKilometros.setVisibility(View.VISIBLE);
                    campoFecha.setVisibility(View.VISIBLE);
                    labelFecha.setVisibility(View.VISIBLE);
                    tipoEvento.setSelection(2);
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
                DatePickerDialog dpd = new DatePickerDialog(EditarEventoActivity.this,
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

        Button agregarBtn = findViewById(R.id.btnEditarEvento);
        agregarBtn.setOnClickListener(view -> onEditarEvento());
        findViewById(R.id.btnEliminarEvento).setOnClickListener(view -> onEliminar());
    }

    public void onEditarEvento(){
        //Obtengo las variables de pantalla
        EditText campoTitulo = (EditText) findViewById(R.id.textTitulo);
        String titulo = campoTitulo.getText().toString();
        Intent intent;

        EditText campoKilometros = (EditText) findViewById(R.id.textKilometros);
        int kilometros;
        Boolean esPorKm;
        //Asigno segun el tipo para cargar en la tabla
        if (tipoEvento.getSelectedItem().toString() == "Por Kilometros"){
            esPorKm = Boolean.TRUE;
            fechaDeEvento = "0";
            kilometros = Integer.parseInt(campoKilometros.getText().toString());
        } else if (tipoEvento.getSelectedItem().toString() == "Por Kilometros"){
            esPorKm = Boolean.FALSE;
            kilometros = 0;
            fechaDeEvento = "" + yearEvento + (Integer.toString(monthEvento).length() == 1 ? ("0" + monthEvento) : monthEvento) + dayEvento;
        } else {
            esPorKm = Boolean.FALSE;
            fechaDeEvento = "" + yearEvento + (Integer.toString(monthEvento).length() == 1 ? ("0" + monthEvento) : monthEvento) + dayEvento;
            kilometros = Integer.parseInt(campoKilometros.getText().toString());
        }
        //Creo el evento
        //TODO debe agregar kilometros totales + los ingresados
        Eventos nuevoEvento = new Eventos(titulo,esPorKm,Integer.parseInt(fechaDeEvento), kilometros);

        //Lo agrego a la base y actualizo la lista

        nuevoEvento.id = id;

        this.viewModel.updateEvento(nuevoEvento);

        //Limpio los campos
        campoTitulo.getText().clear();
        campoKilometros.getText().clear();

        //Vuelvo a la pantalla anterior
        intent = new Intent(this, MostrarEventosActivity.class);
        startActivity(intent);
    }

    public void onEliminar(){
        this.viewModel.deleteEvento(id);
        Intent intent;
        intent = new Intent(this, MostrarEventosActivity.class);
        startActivity(intent);
    }

    private String formateoFecha(int fecha){
        //Paso la fecha a string para poder manejarla
        String stringFecha = Integer.toString(fecha);
        //Intento darle formato, si no puedo la devuelvo tal cual
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyyMMdd");
            Date stringFechaSinFormato = formatoEntrada.parse(stringFecha);
            SimpleDateFormat formatoSalida = new SimpleDateFormat("dd-MM-yyyy");
            String fechaFormateada = formatoSalida.format(stringFechaSinFormato);
            return fechaFormateada;
        } catch (Exception e) {
            return stringFecha;
        }
    }

}
