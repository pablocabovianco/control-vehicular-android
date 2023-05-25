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

    @Query("DELETE FROM Eventos")
    void deleteAll();

}
