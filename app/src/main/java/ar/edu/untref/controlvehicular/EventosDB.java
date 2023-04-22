package ar.edu.untref.controlvehicular;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {EventoPorKilometraje.class}, version = 1)
public abstract class EventosDB extends RoomDatabase {
    public abstract EventoPorKilometrajeDao eventoPorKilometrajeDao();

    public static EventosDB INSTANCE;

    public static EventosDB getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (EventosDB.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EventosDB.class, "base-datos-eventos").fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
