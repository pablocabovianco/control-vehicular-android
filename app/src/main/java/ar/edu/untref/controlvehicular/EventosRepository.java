package ar.edu.untref.controlvehicular;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EventosRepository {
    EventoDao eventoDao;

    EventosRepository(Application application){
        EventosDB db = EventosDB.getDatabase(application);
        eventoDao = db.eventoPorKilometrajeDao();
    }
    LiveData<List<Eventos>> getEvento(){
        return eventoDao.getAllEventoPorKilometraje();
    }

    void insert(Eventos eventos){
        new insertAsyncTask(eventoDao).execute(eventos);
    }
    void delete(){
        new insertAsyncTaskDelete(eventoDao).execute();
    }

    private static class insertAsyncTask extends AsyncTask<Eventos, Void, Void> {
        private EventoDao taskDao;

        insertAsyncTask(EventoDao eventoDao){
            taskDao = eventoDao;
        }

        @Override
        protected Void doInBackground(Eventos... eventosEnTabla) {
            taskDao.insertAll(eventosEnTabla[0]);
            return null;
        }

    }

    private static class insertAsyncTaskDelete extends AsyncTask<Eventos, Void, Void> {
        private EventoDao taskDao;

        insertAsyncTaskDelete(EventoDao eventoDao){
            taskDao = eventoDao;
        }

        @Override
        protected Void doInBackground(Eventos... eventosEnTabla) {
            taskDao.deleteAll();
            return null;
        }

    }
}
