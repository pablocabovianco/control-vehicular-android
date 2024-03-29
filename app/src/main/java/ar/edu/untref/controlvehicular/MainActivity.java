package ar.edu.untref.controlvehicular;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ArduinoListener {

    private Arduino arduino;
    private TextView indicadores;
    private TextView indicadorMarcha;
    private Speedometer speedometer;

    public int speed = 0;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    //Sonidos
    MediaPlayer mediaPlayer;
    PlaybackParams params;
    //Para que una funcion se ejecute continuamente
    private Handler mHandler = new Handler();

    SharedPreferences sharedPreferences;
    public float odometroTotal;
    //Testigo
    public int odometroAnterior;
    //Odometro que se muestra en la pantalla
    TextView odometroPantalla;

    //BBDD
    EventosViewModel viewModel;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verifica si mediaPlayer ya está inicializado
        if(mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.diesel_truck);
            mediaPlayer.setLooping(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            params = new PlaybackParams();
        }

        // Comienza a reproducir si no está reproduciendo
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        arduino = new Arduino(this);
        indicadores = findViewById(R.id.indicadores);
        indicadores.setMovementMethod(new ScrollingMovementMethod());

        indicadorMarcha = findViewById(R.id.indicadorMarcha);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);


        speedometer = findViewById(R.id.speedometer) ;
        //Manejo de BBDD
        this.viewModel = ViewModelProviders.of(this).get(EventosViewModel.class);
        odometroPantalla = findViewById(R.id.OdometroTotal) ;

        //Saco la variable odometroTotal del archivo odometros
        sharedPreferences = getSharedPreferences("odometros", Context.MODE_PRIVATE);
        odometroTotal = sharedPreferences.getFloat("odometroTotal", 100.0f);
        odometroAnterior = (int) Math.floor(odometroTotal);
        odometroPantalla.setText("TOTAL                        " + String.format("%.1f", odometroTotal) + "   Km");
        //Inicio funcion de ejecucion continua
        mHandler.postDelayed(mRunnable, 100);


        Button agregarBtn = findViewById(R.id.AgregarEventos);
        agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                //sendEmail();

            }
        });

    }

    private void sendEmail() {
        new EnviadorMailsActivity().execute();
    }

    //Funcion que se ejecuta cada 1 segundo
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //Reseteo sonido de motor antes de que termine para que no haya delay
            if(mediaPlayer.getCurrentPosition() > 30000){
                mediaPlayer.seekTo(0);
            }

            odometroTotal = (float) (odometroTotal + (speed/3600.0));
            odometroPantalla.setText("TOTAL                        " + String.format("%.1f", odometroTotal) + "   Km");
            odometroPantalla.invalidate();
            if((int) Math.floor(odometroTotal) != odometroAnterior){
                //Guardo y sobreescribo odometro anterior
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("odometroTotal", odometroTotal);
                editor.apply();
                odometroAnterior = (int) Math.floor(odometroTotal);
                try {
                    verificarEventosVencidos();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            // Vuelve a programar la ejecución después de 1000ms
            mHandler.postDelayed(this, 1000);
        }
    };

    private void verificarEventosVencidos() throws ParseException {
        List<Eventos> lista = this.viewModel.getListaEventos().getValue();
        Date fechaActual = new Date();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaEvento;

        for (Eventos evento: lista) {
            if(evento.porKilometros){
                if(evento.kilometros > odometroTotal){
                    sendEmail();
                }
            }else{
                fechaEvento = formatoFecha.parse(formateoFecha(evento.fecha));
                if(fechaActual.after(fechaEvento)){
                    sendEmail();
                }
            }
        }
    }

    public void mostrarMostrarEventosActivity(){
        Intent intent = new Intent(this, MostrarEventosActivity.class);
        startActivity(intent);
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
        //String datos = new String(bytes);
        //display(datos);

        // Convertir los bytes a una cadena
        String datos = new String(bytes);

        // Dividir la cadena en el carácter de dos puntos
        String[] partes = datos.split(":");

        // Si la cadena se dividió en dos partes
        if (partes.length == 2) {
            String tipo = partes[0].trim();
            String valor = partes[1].trim().toUpperCase();

            // Si el tipo es "KMH", actualiza el velocímetro
            if (tipo.equals("KMH")) {
                display(valor);
            }
            // Si el tipo es un código de estado, procesarlo
            else {
                procesarCodigoEstado(tipo, valor);
            }
        }
    }

    private void procesarCodigoEstado(final String tipo, final String valor) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tipo.equals("CAMBIO_D") || tipo.equals("CAMBIO_R")) {
                    if (valor.equals("1")) {
                        String marcha = "MARCHA: " + (tipo.equals("CAMBIO_D") ? "D" : "R");
                        indicadorMarcha.setText(marcha);
                    } else {
                        indicadorMarcha.setText("MARCHA: N");
                    }
                } else {
                    //indicadores.setText(tipo+':'+valor);
                    String mensaje;
                    switch (tipo) {
                        case "PUERTA_AB":
                            mensaje = "PUERTA ABIERTA";
                            break;
                        case "GUINO_IZQ":
                            mensaje = "GUIÑO IZQUIERDO";
                            break;
                        case "GUINO_DER":
                            mensaje = "GUIÑO DERECHO";
                            break;
                        case "CINTURON":
                            mensaje = "CINTURÓN DESABROCHADO";
                            break;
                        // agregar aquí más casos según se necesite
                        default:
                            mensaje = "";
                    }
                    if (valor.equals("1")) {
                        String previo = indicadores.getText().toString();
                        if (!previo.isEmpty()) {
                            previo += "\n";
                        }
                        indicadores.setText(previo + mensaje);
                    } else {
                        // elimina el mensaje de los indicadores
                        String previo = indicadores.getText().toString();
                        previo = previo.replace(mensaje, "").replace("\n\n", "\n").trim();
                        indicadores.setText(previo);
                    }
                }
            }
        });
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

                //función auxiliar necesaria para actualizar el velocímetro
                Function0<Unit> onAnimationEnd = new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        // Código que se ejecuta al finalizar la animación
                        return null;
                    }
                };

                speed = 0;

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

    //CODIGO IVAN

}