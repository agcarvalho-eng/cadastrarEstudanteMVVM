package com.example.diarioestudantesmvvm.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diarioestudantesmvvm.model.Estudante;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EstatisticasViewModel extends ViewModel implements DefaultLifecycleObserver {
    private final MutableLiveData<Double> mediaGeral = new MutableLiveData<>();
    private final MutableLiveData<String> alunoMaiorNota = new MutableLiveData<>();
    private final MutableLiveData<String> alunoMenorNota = new MutableLiveData<>();
    private final MutableLiveData<Double> mediaIdade = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> aprovados = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> reprovados = new MutableLiveData<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> manipulador;
    private final EstudantesRepository repository = EstudantesRepository.getInstance();

    public LiveData<Double> getMediaGeral() { return mediaGeral; }
    public LiveData<String> getAlunoMaiorNota() { return alunoMaiorNota; }
    public LiveData<String> getAlunoMenorNota() { return alunoMenorNota; }
    public LiveData<Double> getMediaIdade() { return mediaIdade; }
    public LiveData<List<Estudante>> getAprovados() { return aprovados; }
    public LiveData<List<Estudante>> getReprovados() { return reprovados; }

    @Override
    public void onStart(@NonNull LifecycleOwner lifecycleOwner) {
        if (manipulador != null && !manipulador.isCancelled()) return;

        manipulador = executor.scheduleWithFixedDelay(() -> {
            try {
                List<Estudante> estudantesCompletos = repository.buscarTodosEstudantesCompletos();

                if (estudantesCompletos != null && !estudantesCompletos.isEmpty()) {
                    calcularEAtualizarEstatisticas(estudantesCompletos);
                }
            } catch (Exception e) {
                Log.e("EstatisticasVM", "Erro ao calcular estatísticas", e);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner lifecycleOwner) {
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    private void calcularEAtualizarEstatisticas(List<Estudante> estudantes) {
        if (estudantes == null || estudantes.isEmpty()) {
            Log.w("EstatisticasVM", "Lista de estudantes vazia");
            return;
        }

        try {
            mediaGeral.postValue(CalculoEstatisticas.calcularMediaGeral(estudantes));

            Estudante maiorNota = CalculoEstatisticas.encontrarMaiorNota(estudantes);
            alunoMaiorNota.postValue(maiorNota != null ?
                    String.format("%s (%.2f)", maiorNota.getNome(), maiorNota.calcularMedia()) : "Nenhum");

            Estudante menorNota = CalculoEstatisticas.encontrarMenorNota(estudantes);
            alunoMenorNota.postValue(menorNota != null ?
                    String.format("%s (%.2f)", menorNota.getNome(), menorNota.calcularMedia()) : "Nenhum");

            mediaIdade.postValue(CalculoEstatisticas.calcularMediaIdade(estudantes));

            List<Estudante> listaAprovados = CalculoEstatisticas.getAprovados(estudantes);
            aprovados.postValue(listaAprovados != null ? listaAprovados : new ArrayList<>());

            List<Estudante> listaReprovados = CalculoEstatisticas.getReprovados(estudantes);
            reprovados.postValue(listaReprovados != null ? listaReprovados : new ArrayList<>());

        } catch (Exception e) {
            Log.e("EstatisticasVM", "Erro ao calcular estatísticas", e);
        }
    }

    public void recarregarEstatisticas() {
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
