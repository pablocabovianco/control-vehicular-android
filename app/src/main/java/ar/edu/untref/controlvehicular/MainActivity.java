package ar.edu.untref.controlvehicular;

import android.hardware.usb.UsbDevice;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

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
                String mensaje = !posBtn.isChecked() ? "0" : "1";
                //char c = (char)(Integer.parseInt(mensaje,2));
                //arduino.send(new byte[]{(byte) c});
                arduino.send(mensaje.getBytes());
            }
        });

        bajBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = !bajBtn.isChecked() ? "2" : "3";
                arduino.send(mensaje.getBytes());
            }
        });

        altBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = !altBtn.isChecked() ? "4" : "5";
                arduino.send(mensaje.getBytes());
            }
        });

        refBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = !refBtn.isChecked() ? "6" : "7";
                arduino.send(mensaje.getBytes());
            }
        });

        intBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = !intBtn.isChecked() ? "8" : "9";
                arduino.send(mensaje.getBytes());
            }
        });
        arduino = new Arduino(this);
        arduino.addVendorId(0x10c4);
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