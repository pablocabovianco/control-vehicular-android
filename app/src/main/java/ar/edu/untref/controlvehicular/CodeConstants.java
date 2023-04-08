package ar.edu.untref.controlvehicular;

public final class CodeConstants {

    private CodeConstants() {}

    public static final byte APAGAR_LUZ_POSICION = (byte) 0b00000000;
    public static final byte ENCENDER_LUZ_POSICION	= (byte) 0b00000001;
    public static final byte APAGAR_LUZ_BAJA = (byte) 0b00000010;
    public static final byte ENCENDER_LUZ_BAJA = (byte) 0b00000011;
    public static final byte APAGAR_LUZ_ALTA = (byte) 0b00000100;
    public static final byte ENCENDER_LUZ_ALTA = (byte) 0b00000101;
    public static final byte APAGAR_LUZ_REFLECTOR = (byte) 0b00000110;
    public static final byte ENCENDER_LUZ_REFLECTOR = (byte) 0b00000111;
    public static final byte APAGAR_LUZ_INTERIOR = (byte) 0b00001000;
    public static final byte ENCENDER_LUZ_INTERIOR = (byte) 0b00001001;
    public static final byte DESBLOQUEAR_PUERTAS= (byte) 0b00001010;
    public static final byte BLOQUEAR_PUERTAS = (byte) 0b00001011;
    public static final byte SILENCIAR_BOCINA	= (byte) 0b00001100;
    public static final byte SONAR_BOCINA	= (byte) 0b00001101;

}
