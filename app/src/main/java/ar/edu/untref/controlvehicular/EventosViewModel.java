package ar.edu.untref.controlvehicular;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EventosViewModel extends AndroidViewModel {
    EventosRepository repository;
    LiveData<List<EventoPorKilometraje>> listaEventosPorKilometraje;

    public EventosViewModel(Application application){
        super(application);
        repository = new EventosRepository(application);
        listaEventosPorKilometraje = repository.getEvento();
    }

    LiveData<List<EventoPorKilometraje>> getListaEventosPorKilometraje(){
        return listaEventosPorKilometraje;
    }

    public void insertEvento(EventoPorKilometraje eventoPorKilometraje){
        repository.insert(eventoPorKilometraje);
    }
}
