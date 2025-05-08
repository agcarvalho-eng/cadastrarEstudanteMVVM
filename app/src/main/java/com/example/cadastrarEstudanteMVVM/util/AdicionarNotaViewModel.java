package com.example.cadastrarEstudanteMVVM.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.example.cadastrarEstudanteMVVM.model.Estudante;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AdicionarNotaViewModel extends ViewModel implements DefaultLifecycleObserver {
    private final EstudantesRepository repository = EstudantesRepository.getInstance();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> manipulador;

    public interface OnNotaAdicionadaListener {
        void onSuccess();
        void onError(String mensagem);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        // Pode ser usado para iniciar operações periódicas se necessário
    }

    public void adicionarNota(int estudanteId, double nota, OnNotaAdicionadaListener listener) {
        if (manipulador != null && !manipulador.isDone()) {
            manipulador.cancel(false);
        }

        manipulador = executor.schedule(() -> {
            try {
                Estudante estudante = repository.buscarDadosEstudante(estudanteId);
                if (estudante != null) {
                    estudante.getNotas().add(nota);
                    boolean sucesso = repository.atualizarEstudante(estudante);
                    if (sucesso) {
                        listener.onSuccess();
                    } else {
                        listener.onError("Falha ao atualizar notas do estudante");
                    }
                } else {
                    listener.onError("Estudante não encontrado");
                }
            } catch (Exception e) {
                listener.onError("Erro ao adicionar nota: " + e.getMessage());
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
