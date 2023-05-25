package ar.edu.untref.controlvehicular;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MostrarEventosActivity extends AppCompatActivity {

    EventosViewModel viewModel;

    private ListView listaEventos;
    private ArrayAdapter<String> mAdapter;

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
    }

    public void mostrarEventosPorKilometraje(){
        viewModel.getListaEventosPorKilometraje().observe(this, lista -> {
            if(lista == null || lista.size() == 0) {
                return;
            }
            //Muestro solo los titulos
            List<String> titulosDeEventos = new ArrayList<>();
            for(Eventos listaEntera: lista){
                titulosDeEventos.add(listaEntera.titulo);
            }
            mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titulosDeEventos);
            listaEventos.setAdapter(mAdapter);
        });
    }
    public void mostrarAgregarEventoActivity(){
        Intent intent = new Intent(this, AgregarEventoActivity.class);
        startActivity(intent);
    }
}