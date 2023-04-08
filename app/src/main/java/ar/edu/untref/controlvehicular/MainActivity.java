package ar.edu.untref.controlvehicular;

import android.hardware.usb.UsbDevice;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

import static ar.edu.untref.controlvehicular.CodeConstants.*;

public class MainActivity extends AppCompatActivity implements ArduinoListener {

    private Arduino arduino;
    private ToggleButton posBtn;
    private ToggleButton bajBtn;
    private ToggleButton altBtn;
    private ToggleButton refBtn;
    private ToggleButton intBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        posBtn = findViewById(R.id.tbPosicion);
        bajBtn = findViewById(R.id.tbBajas);
        altBtn = findViewById(R.id.tbAltas);
        refBtn = findViewById(R.id.tbReflector);
        intBtn = findViewById(R.id.tbInterior);

        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte c = posBtn.isChecked() ? ENCENDER_LUZ_POSICION : APAGAR_LUZ_POSICION;
                arduino.send(new byte[]{c});
            }
        });

        bajBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte c = bajBtn.isChecked() ? ENCENDER_LUZ_BAJA : APAGAR_LUZ_BAJA;
                arduino.send(new byte[]{c});
            }
        });

        altBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte c = altBtn.isChecked() ? ENCENDER_LUZ_ALTA : APAGAR_LUZ_ALTA;
                arduino.send(new byte[]{c});
            }
        });

        refBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte c = refBtn.isChecked() ? ENCENDER_LUZ_REFLECTOR : APAGAR_LUZ_REFLECTOR;
                arduino.send(new byte[]{c});
            }
        });

        intBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte c = intBtn.isChecked() ? ENCENDER_LUZ_INTERIOR : APAGAR_LUZ_INTERIOR;
                arduino.send(new byte[]{c});
            }
        });
        arduino = new Arduino(this);
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
        display("arduino attached...");
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {
        display("arduino detached.");
    }

    @Override
    public void onArduinoMessage(byte[] bytes) {
        display(new String(bytes));
    }

    @Override
    public void onArduinoOpened() {
        String str = "arduino opened...";
        arduino.send(str.getBytes());
    }

    @Override
    public void onUsbPermissionDenied() {
        display("Permission denied. Attempting again in 3 sec...");
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
                //displayTextView.append(message);
            }
        });
    }
}