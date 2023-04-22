package ar.edu.untref.controlvehicular;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class EventoPorKilometraje {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "kilometros_evento")
    public int kilometros;
    @ColumnInfo(name = "titulo_evento")
    public String titulo;
    @ColumnInfo(name = "descripcion_evento")
    public String descripcion;

    public EventoPorKilometraje(int kilometros, String titulo, String descripcion){
        this.kilometros = kilometros;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }
}
