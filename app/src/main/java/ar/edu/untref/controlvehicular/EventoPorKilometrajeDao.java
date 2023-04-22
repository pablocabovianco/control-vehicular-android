package ar.edu.untref.controlvehicular;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventoPorKilometrajeDao {
    @Insert
    void insertAll(EventoPorKilometraje... eventos);

    @Query("SELECT * FROM eventoPorKilometraje")
    LiveData<List<EventoPorKilometraje>> getAllEventoPorKilometraje();
}
