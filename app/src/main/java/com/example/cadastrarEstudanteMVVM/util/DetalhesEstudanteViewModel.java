package com.example.cadastrarEstudanteMVVM.util;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.cadastrarEstudanteMVVM.model.Estudante;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DetalhesEstudanteViewModel extends ViewModel implements DefaultLifecycleObserver {
    private final MutableLiveData<Estudante> estudanteLiveData = new MutableLiveData<>();
    private final EstudantesRepository repository = EstudantesRepository.getInstance();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> manipulador;
    private int estudanteIdAtual = -1;

    public LiveData<Estudante> getEstudante() {
        return estudanteLiveData;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner lifecycleOwner) {
        if (manipulador != null && !manipulador.isCancelled() || estudanteIdAtual == -1) {
            return;
        }

        manipulador = executor.scheduleWithFixedDelay(() -> {
            try {
                carregarDadosEstudante(estudanteIdAtual);
            } catch (Exception e) {
                Log.e("DetalhesEstVM", "Erro ao carregar estudante", e);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner lifecycleOwner) {
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    public void setEstudanteId(int estudanteId) {
        this.estudanteIdAtual = estudanteId;
        recarregarEstudante();
    }

    public void recarregarEstudante() {
        if (manipulador != null) {
            manipulador.cancel(false);
        }
        onStart(null);
    }

    private void carregarDadosEstudante(int estudanteId) {
        Estudante estudante = repository.buscarDadosEstudante(estudanteId);
        if (estudante != null) {
            estudanteLiveData.postValue(estudante);
        }
    }

    // Novo método para deletar estudante
    public boolean deletarEstudante() {
        if (estudanteIdAtual != -1) {
            return repository.deletarEstudante(estudanteIdAtual);
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
