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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arduino = new Arduino(this);

        ToggleButton posBtn = findViewById(R.id.tbPosicion);
        ToggleButton bajBtn = findViewById(R.id.tbBajas);
        ToggleButton altBtn = findViewById(R.id.tbAltas);
        ToggleButton refBtn = findViewById(R.id.tbReflector);
        ToggleButton intBtn = findViewById(R.id.tbInterior);

        posBtn.setOnClickListener(new ToggleButtonClickListener(posBtn, ENCENDER_LUZ_POSICION, APAGAR_LUZ_POSICION, arduino));
        bajBtn.setOnClickListener(new ToggleButtonClickListener(bajBtn, ENCENDER_LUZ_BAJA, APAGAR_LUZ_BAJA, arduino));
        altBtn.setOnClickListener(new ToggleButtonClickListener(altBtn, ENCENDER_LUZ_ALTA, APAGAR_LUZ_ALTA, arduino));
        refBtn.setOnClickListener(new ToggleButtonClickListener(refBtn, ENCENDER_LUZ_REFLECTOR, APAGAR_LUZ_REFLECTOR, arduino));
        intBtn.setOnClickListener(new ToggleButtonClickListener(intBtn, ENCENDER_LUZ_INTERIOR, APAGAR_LUZ_INTERIOR, arduino));

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