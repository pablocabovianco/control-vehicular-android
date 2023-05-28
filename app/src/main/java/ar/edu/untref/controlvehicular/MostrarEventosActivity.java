package ar.edu.untref.controlvehicular;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MostrarEventosActivity extends AppCompatActivity {

    EventosViewModel viewModel;

    private ListView listaEventos;
    private ArrayAdapter<String> mAdapterItem;
    private ArrayAdapter<String> mAdapterSubItem;
    public final static String EXTRA_TEXT = "com.ar.edu.untref.controlvehicular.EXTRA_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_eventos);

        this.viewModel = ViewModelProviders.of(this).get(EventosViewModel.class);
        listaEventos = findViewById(R.id.listaEventos);
        mostrarEventosPorKilometraje();

        FloatingActionButton agregarBtn = findViewById(R.id.btnAgregarEvento);
        agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarAgregarEventoActivity();
            }
        });
        //Se ejecuta al seleccionar un elemento de la lista
        listaEventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                mostrarEditarEventoActivity(position);
            }
        });
    }

    public void mostrarEventosPorKilometraje(){
        viewModel.getListaEventos().observe(this, lista -> {
            if(lista == null || lista.size() == 0) {
                return;
            }
            //Muestro titulos y vencimiento
            List<String> titulosDeEventos = new ArrayList<>();
            List<String> vencimientos = new ArrayList<>();
                    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            for(Eventos listaEntera: lista){
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("datos1", listaEntera.titulo);
                datum.put("datos2", listaEntera.porKilometros ? Integer.toString(listaEntera.kilometros) : formateoFecha(listaEntera.fecha));
                data.add(datum);
            }
            SimpleAdapter adapter = new SimpleAdapter(this, data,
                        android.R.layout.simple_list_item_2,
                        new String[] {"datos1", "datos2"},
                        new int[] {android.R.id.text1,
                                android.R.id.text2});
            listaEventos.setAdapter(adapter);

        });
    }
    public void mostrarEditarEventoActivity(int posicion){
        Intent intent = new Intent(this, EditarEventoActivity.class);
        viewModel.getListaEventos().observe(this, lista -> {
            if(lista == null || lista.size() == 0) {
                return;
            }
            else{
                if(lista.get(posicion) != null){

                }
            }
        });
        String message = "abc";
        intent.putExtra(EXTRA_TEXT, message);
        startActivity(intent);
    }
    public void mostrarAgregarEventoActivity(){
        Intent intent = new Intent(this, AgregarEventoActivity.class);
        startActivity(intent);
    }
    public String formateoFecha(int fecha){
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