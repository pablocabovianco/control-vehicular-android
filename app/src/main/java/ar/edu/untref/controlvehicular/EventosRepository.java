package ar.edu.untref.controlvehicular;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EventosRepository {
    EventoDao eventoDao;

    EventosRepository(Application application){
        EventosDB db = EventosDB.getDatabase(application);
        eventoDao = db.eventoDao();
    }
    LiveData<List<Eventos>> getEvento(){
        return eventoDao.getAllEventoPorKilometraje();
    }

    void insert(Eventos eventos){
        new insertAsyncTask(eventoDao).execute(eventos);
    }
    void update(Eventos evento){
        new insertAsyncTaskUpdate(eventoDao).execute(evento);
    }
    void delete(int id){
        new insertAsyncTaskDelete(eventoDao).execute(id);
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

    private static class insertAsyncTaskUpdate extends AsyncTask<Eventos, Void, Void> {
        private EventoDao taskDao;

        protected insertAsyncTaskUpdate(EventoDao eventoDao) {
            taskDao = eventoDao;
        }
        @Override
        protected Void doInBackground(Eventos... eventosEnTabla) {
            taskDao.updateEvento(eventosEnTabla[0].id,eventosEnTabla[0].titulo,eventosEnTabla[0].kilometros,eventosEnTabla[0].porKilometros,eventosEnTabla[0].fecha);
            return null;
        }

    }

    private static class insertAsyncTaskDelete extends AsyncTask<Integer, Void, Void> {
        private EventoDao taskDao;

        insertAsyncTaskDelete(EventoDao eventoDao){
            taskDao = eventoDao;
        }

        @Override
        protected Void doInBackground(Integer... ids) {
            taskDao.delete(ids[0]);
            return null;
        }
    }
}
