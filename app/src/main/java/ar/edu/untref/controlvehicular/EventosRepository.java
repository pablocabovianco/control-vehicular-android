package ar.edu.untref.controlvehicular;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EventosRepository {
    EventoPorKilometrajeDao eventoPorKilometrajeDao;

    EventosRepository(Application application){
        EventosDB db = EventosDB.getDatabase(application);
        eventoPorKilometrajeDao = db.eventoPorKilometrajeDao();
    }
    LiveData<List<EventoPorKilometraje>> getEvento(){
        return eventoPorKilometrajeDao.getAllEventoPorKilometraje();
    }

    void insert(EventoPorKilometraje eventoPorKilometraje){
        new insertAsyncTask(eventoPorKilometrajeDao).execute(eventoPorKilometraje);
    }

    private static class insertAsyncTask extends AsyncTask<EventoPorKilometraje, Void, Void>{
        private EventoPorKilometrajeDao taskDao;

        insertAsyncTask(EventoPorKilometrajeDao eventoPorKilometrajeDao){
            taskDao = eventoPorKilometrajeDao;
        }

        @Override
        protected Void doInBackground(EventoPorKilometraje... eventoPorKilometrajes) {
            taskDao.insertAll(eventoPorKilometrajes[0]);
            return null;
        }
    }
}
