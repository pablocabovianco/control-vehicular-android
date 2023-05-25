package ar.edu.untref.controlvehicular;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
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


import android.content.pm.PackageManager;
import android.widget.Toast;

import static ar.edu.untref.controlvehicular.CodeConstants.*;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ArduinoListener {

    private Arduino arduino;
    private TextView displayTextView;
    private Speedometer speedometer;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    //Sonidos
    MediaPlayer mediaPlayer;
    PlaybackParams params;
    //Placeholder del kilometraje
    public int kilometrosTotales = 1600;

    //BBDD
    EventosViewModel viewModel;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mediaPlayer = MediaPlayer.create(this, R.raw.sonido_i4);;
        //Sonido constante
        mediaPlayer.setLooping(true);
        //Empieza a sonar el motor
        mediaPlayer.start();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        params = new PlaybackParams();

        arduino = new Arduino(this);
        displayTextView = findViewById(R.id.diplayTextView);
        displayTextView.setMovementMethod(new ScrollingMovementMethod());

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        speedometer = findViewById(R.id.speedometer) ;
        //Manejo de BBDD
        this.viewModel = ViewModelProviders.of(this).get(EventosViewModel.class);

        Button agregarBtn = findViewById(R.id.AgregarEventos);
        agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agregarDatosPrueba();
                mostrarMostrarEventosActivity();
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

        //GPS

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        Button gpsBtn = findViewById(R.id.gpsBtn);

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationTrack = new LocationTrack(MainActivity.this);
                abrirGoogleMaps();

            }
        });

    }

    //Pruebas para BBDD
    public void agregarDatosPrueba(){
        this.agregarDato(100, "Cargar Nafta", "En ypf");
        this.agregarDato(3000, "Hacer VTV", "Se necesita VTV anterior");
        this.agregarDato(13000, "Cambiar cubiertas", "Bridgestone Pilot Street");
    }

    public void mostrarMostrarEventosActivity(){
        Intent intent = new Intent(this, MostrarEventosActivity.class);
        startActivity(intent);
    }

    public void agregarDato(int kilometros,String titulo,String descripcion){
        //Creo el evento
        EventoPorKilometraje nuevoEvento = new EventoPorKilometraje(kilometros + this.kilometrosTotales, titulo, descripcion);
        //Lo agrego a la base
        this.viewModel.insertEvento(nuevoEvento);
    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    private void abrirGoogleMaps() {
        String label = "Ubicación Actual"; // Etiqueta para la ubicación actual

        // Obtener la ubicación actual del dispositivo en formato "latitud,longitud"
        String currentLocation = String.valueOf(locationTrack.latitude) + "," + String.valueOf(locationTrack.longitude);

        if (currentLocation != null) {
            Uri gmmIntentUri = Uri.parse("geo:" + currentLocation + "?q=" + Uri.encode(currentLocation + "(" + label + ")"));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps"); // Especifica que se debe abrir la aplicación Google Maps

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        }
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
        locationTrack.stopListener();
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

                //Ajusto pitch de reproduccion, con un audio ajustado
                try {
                    params.setPitch(1.0f + (float) speed * 0.025f);
                }catch (Exception e){
                    params.setPitch(1.0f);
                }
                mediaPlayer.setPlaybackParams(params);

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    //CODIGO IVAN

}