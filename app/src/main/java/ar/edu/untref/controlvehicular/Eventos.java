package ar.edu.untref.controlvehicular;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity
public class Eventos {

    @PrimaryKey(autoGenerate = true)
    public int id;


    @ColumnInfo(name = "titulo_evento")
    public String titulo;
    @ColumnInfo(name = "por-kilometros")
    public Boolean porKilometros;
    @ColumnInfo(name = "kilometros_evento")
    public int kilometros;
    @ColumnInfo(name = "fecha-evento")
    public Date fecha;
    public Eventos(String titulo, Boolean porKilometros, Date fecha, int kilometros){
        this.kilometros = kilometros;
        this.titulo = titulo;
        this.porKilometros = porKilometros;
        this.fecha = fecha;
    }

}
