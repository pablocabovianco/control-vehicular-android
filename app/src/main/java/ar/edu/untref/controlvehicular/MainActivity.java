package ar.edu.untref.controlvehicular;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;
import me.ibrahimsn.lib.Speedometer;

import static ar.edu.untref.controlvehicular.CodeConstants.*;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ArduinoListener {

    private Arduino arduino;
    private TextView displayTextView;
    private Speedometer speedometer;

    //Placeholder del kilometraje
    public int kilometrosTotales = 1600;

    //BBDD
    EventosViewModel viewModel;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arduino = new Arduino(this);
        displayTextView = findViewById(R.id.diplayTextView);
        displayTextView.setMovementMethod(new ScrollingMovementMethod());

        speedometer = findViewById(R.id.speedometer) ;
        //Manejo de BBDD
        this.viewModel = ViewModelProviders.of(this).get(EventosViewModel.class);

        Button agregarBtn = findViewById(R.id.AgregarEventos);
        agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarDatosPrueba();
            }
        });

        Button mostrarEventosBtn = findViewById(R.id.MostrarEventos);
        mostrarEventosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarEventosPorKilometraje();
            }
        });

        //Luces
        ToggleButton posBtn = findViewById(R.id.tbPosicion);
        ToggleButton bajBtn = findViewById(R.id.tbBajas);
        ToggleButton altBtn = findViewById(R.id.tbAltas);
        ToggleButton refBtn = findViewById(R.id.tbReflector);
        ToggleButton intBtn = findViewById(R.id.tbInterior);

        //Otros
        ToggleButton bocBtn = findViewById(R.id.tbBocina);
        ToggleButton blPBtn = findViewById(R.id.tbBloqPuertas);

        posBtn.setOnClickListener(new ToggleButtonClickListener(posBtn, ENCENDER_LUZ_POSICION, APAGAR_LUZ_POSICION, arduino));
        bajBtn.setOnClickListener(new ToggleButtonClickListener(bajBtn, ENCENDER_LUZ_BAJA, APAGAR_LUZ_BAJA, arduino));
        altBtn.setOnClickListener(new ToggleButtonClickListener(altBtn, ENCENDER_LUZ_ALTA, APAGAR_LUZ_ALTA, arduino));
        refBtn.setOnClickListener(new ToggleButtonClickListener(refBtn, ENCENDER_LUZ_REFLECTOR, APAGAR_LUZ_REFLECTOR, arduino));
        intBtn.setOnClickListener(new ToggleButtonClickListener(intBtn, ENCENDER_LUZ_INTERIOR, APAGAR_LUZ_INTERIOR, arduino));
        blPBtn.setOnClickListener(new ToggleButtonClickListener(blPBtn, BLOQUEAR_PUERTAS, DESBLOQUEAR_PUERTAS, arduino));

        //La bocina tiene un tratamiento especial ya que solo se activa mientras se mantiene presionada
        bocBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // El botón ha sido presionado
                        bocBtn.setChecked(true);
                        arduino.send(new byte[]{SONAR_BOCINA});
                        return true;

                    case MotionEvent.ACTION_UP:
                        // El botón ha sido soltado
                        bocBtn.setChecked(false);
                        arduino.send(new byte[]{SILENCIAR_BOCINA});
                        return true;
                }
                return false;
            }
        });


    }

    //Pruebas para BBDD
    public void agregarDatosPrueba(){
        this.agregarDato(100, "Cargar Nafta", "En ypf");
        this.agregarDato(3000, "Hacer VTV", "Se necesita VTV anterior");
        this.agregarDato(13000, "Cambiar cubiertas", "Bridgestone Pilot Street");
    }

    public void mostrarEventosPorKilometraje(){
       viewModel.getListaEventosPorKilometraje().observe(this, listaEventos -> {
            if(listaEventos == null){
                return;
            }
            for(EventoPorKilometraje lista: listaEventos){
                System.out.println(lista.titulo + " vence en " + lista.kilometros);
            }
        });
    }

    public void agregarDato(int kilometros,String titulo,String descripcion){
        //Creo el evento
        EventoPorKilometraje nuevoEvento = new EventoPorKilometraje(kilometros + this.kilometrosTotales, titulo, descripcion);
        //Lo agrego a la base
        this.viewModel.insertEvento(nuevoEvento);
    }

    @Override
    protected void onStart() {
        super.onStart();
        arduino.setArduinoListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        //display("arduino attached...");
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {

        //display("arduino detached.");
    }

    @Override
    public void onArduinoMessage(byte[] bytes) {
        //Al recibir información de arduino:
        String datos = new String(bytes);
        display(datos);
    }


    @Override
    public void onArduinoOpened() {
        String str = "arduino opened...";
        arduino.send(str.getBytes());
    }

    @Override
    public void onUsbPermissionDenied() {
        //display("Permission denied. Attempting again in 3 sec...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arduino.reopen();
            }
        }, 3000);
    }
    private void display(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //displayTextView.setText(message);

                //función auxiliar necesaria para actualizar el velocímetro
                Function0<Unit> onAnimationEnd = new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        // Código que se ejecuta al finalizar la animación
                        return null;
                    }
                };

                int speed = 0;

                try {
                    speed = Integer.parseInt(message.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Error en la conversión");
                }
                speedometer.setSpeed(speed, 500, onAnimationEnd);

            }
        });
    }

    //CODIGO IVAN

}