package ar.edu.untref.controlvehicular;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventoDao {
    @Insert
    void insertAll(Eventos... eventos);

    @Query("SELECT * FROM Eventos")
    LiveData<List<Eventos>> getAllEventoPorKilometraje();

    @Query("UPDATE Eventos SET titulo_evento = :titulo, kilometros_evento = :kilometros, por_kilometros = :porKilometros, fecha_evento = :fecha WHERE id = :id")
    void updateEvento(int id, String titulo, int kilometros, boolean porKilometros, int fecha);

    @Query("DELETE FROM Eventos WHERE id = :id")
    void delete(int id);

}
