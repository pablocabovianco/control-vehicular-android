package ar.edu.untref.controlvehicular;

import android.view.View;
import android.widget.ToggleButton;

import me.aflak.arduino.Arduino;

public class ToggleButtonClickListener implements View.OnClickListener{

    private final byte codigoEncender;
    private final byte codigoApagar;
    private final ToggleButton button;
    private final Arduino arduino;

    public ToggleButtonClickListener(ToggleButton button, byte codigoEncender, byte codigoApagar, Arduino arduino) {
        this.button = button;
        this.codigoEncender = codigoEncender;
        this.codigoApagar = codigoApagar;
        this.arduino = arduino;
    }

    @Override
    public void onClick(View v) {
        byte c = button.isChecked() ? codigoEncender : codigoApagar;
        arduino.send(new byte[]{c});
    }
}
