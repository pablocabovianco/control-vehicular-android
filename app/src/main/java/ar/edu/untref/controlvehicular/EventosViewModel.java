package ar.edu.untref.controlvehicular;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EventosViewModel extends AndroidViewModel {
    EventosRepository repository;
    LiveData<List<Eventos>> listaEventosPorKilometraje;

    public EventosViewModel(Application application){
        super(application);
        repository = new EventosRepository(application);
        listaEventosPorKilometraje = repository.getEvento();
    }

    LiveData<List<Eventos>> getListaEventosPorKilometraje(){
        return listaEventosPorKilometraje;
    }

    public void insertEvento(Eventos eventos){
        repository.insert(eventos);
    }
    public void deleteAllEventos(){
        repository.delete();
    }


}
