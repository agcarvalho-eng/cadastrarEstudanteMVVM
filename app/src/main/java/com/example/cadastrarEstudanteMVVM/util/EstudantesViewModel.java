package  com.example.cadastrarEstudanteMVVM.util;
//package com.example.cadastrarEstudanteMVVM.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cadastrarEstudanteMVVM.model.Estudante;
import com.example.cadastrarEstudanteMVVM.util.EstudantesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EstudantesViewModel extends ViewModel implements DefaultLifecycleObserver {
    private final MutableLiveData<List<Estudante>> estudantesLiveData = new MutableLiveData<>();
    private final EstudantesRepository repository = EstudantesRepository.getInstance();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> manipulador;
    private List<Estudante> cacheEstudantes = new ArrayList<>();

    public LiveData<List<Estudante>> getEstudantes() {
        return estudantesLiveData;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner lifecycleOwner) {
        if (manipulador != null && !manipulador.isCancelled()) {
            return;
        }

        manipulador = executor.scheduleWithFixedDelay(() -> {
            try {
                List<Estudante> novosEstudantes = repository.buscarTodosEstudantes();

                if (!novosEstudantes.equals(cacheEstudantes)) {
                    cacheEstudantes = novosEstudantes;
                    estudantesLiveData.postValue(novosEstudantes);
                    repository.setEstudantes(novosEstudantes);
                }
            } catch (Exception e) {
                Log.e("EstudantesVM", "Erro ao carregar estudantes", e);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner lifecycleOwner) {
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    public void recarregarEstudantes() {
        if (manipulador != null) {
            manipulador.cancel(false);
        }
        onStart(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
