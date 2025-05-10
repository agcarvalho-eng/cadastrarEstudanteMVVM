package com.example.cadastrarEstudanteMVVM.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cadastrarEstudanteMVVM.model.Estudante;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// Classe ViewModel que observa e calcula as estatísticas dos estudantes
public class EstatisticasViewModel extends ViewModel implements DefaultLifecycleObserver {

    // LiveData que armazena a média geral das notas dos estudantes.
    private final MutableLiveData<Double> mediaGeral = new MutableLiveData<>();

    // LiveData com o nome do aluno com a maior média.
    private final MutableLiveData<String> alunoMaiorNota = new MutableLiveData<>();

    // LiveData com o nome do aluno com a menor média.
    private final MutableLiveData<String> alunoMenorNota = new MutableLiveData<>();

    // LiveData que armazena a média de idade dos estudantes.
    private final MutableLiveData<Double> mediaIdade = new MutableLiveData<>();

    // LiveData com a lista de estudantes aprovados.
    private final MutableLiveData<List<Estudante>> aprovados = new MutableLiveData<>();

    // LiveData com a lista de estudantes reprovados.
    private final MutableLiveData<List<Estudante>> reprovados = new MutableLiveData<>();

    // Executor que gerencia tarefas em background (thread separada).
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Manipulador para controlar a tarefa agendada que calcula estatísticas periodicamente.
    private ScheduledFuture<?> manipulador;

    // Instância do repositório para acessar os dados dos estudantes.
    private final EstudantesRepository repository = EstudantesRepository.getInstance();

    // Getters públicos para expor os LiveData para a UI observar.
    public LiveData<Double> getMediaGeral() { return mediaGeral; }
    public LiveData<String> getAlunoMaiorNota() { return alunoMaiorNota; }
    public LiveData<String> getAlunoMenorNota() { return alunoMenorNota; }
    public LiveData<Double> getMediaIdade() { return mediaIdade; }
    public LiveData<List<Estudante>> getAprovados() { return aprovados; }
    public LiveData<List<Estudante>> getReprovados() { return reprovados; }

    // Método chamado automaticamente quando o ciclo de vida entra em estado STARTED.
    @Override
    public void onStart(@NonNull LifecycleOwner lifecycleOwner) {
        // Se já existe uma tarefa em execução, evita duplicação.
        if (manipulador != null && !manipulador.isCancelled()) return;

        // Agenda tarefa para rodar a cada 30 segundos, iniciando imediatamente.
        manipulador = executor.scheduleWithFixedDelay(() -> {
            try {
                // Busca a lista completa dos estudantes (com notas e presença).
                List<Estudante> estudantesCompletos = repository.buscarTodosEstudantesCompletos();

                // Se houver estudantes válidos, calcula as estatísticas.
                if (estudantesCompletos != null && !estudantesCompletos.isEmpty()) {
                    calcularEAtualizarEstatisticas(estudantesCompletos);
                }
            } catch (Exception e) {
                Log.e("EstatisticasVM", "Erro ao calcular estatísticas!", e);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    // Método chamado automaticamente quando o ciclo de vida entra em estado STOPPED.
    @Override
    public void onStop(@NonNull LifecycleOwner lifecycleOwner) {
        // Cancela a tarefa periódica se estiver ativa.
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    // Método que processa os dados dos estudantes e atualiza os LiveData com os resultados.
    private void calcularEAtualizarEstatisticas(List<Estudante> estudantes) {
        // Verifica se a lista está vazia ou nula.
        if (estudantes == null || estudantes.isEmpty()) {
            Log.w("EstatisticasVM", "Lista de estudantes vazia!");
            return;
        }

        try {
            // Calcula a média geral e publica no LiveData.
            mediaGeral.postValue(CalculoEstatisticas.calcularMediaGeral(estudantes));

            // Busca o aluno com maior nota e publica o nome + média no LiveData.
            Estudante maiorNota = CalculoEstatisticas.encontrarMaiorNota(estudantes);
            alunoMaiorNota.postValue(maiorNota != null ?
                    String.format("%s (%.2f)", maiorNota.getNome(), maiorNota.calcularMedia()) : "Nenhum");

            // Busca o aluno com menor nota e publica o nome + média no LiveData.
            Estudante menorNota = CalculoEstatisticas.encontrarMenorNota(estudantes);
            alunoMenorNota.postValue(menorNota != null ?
                    String.format("%s (%.2f)", menorNota.getNome(), menorNota.calcularMedia()) : "Nenhum");

            // Calcula a média de idade e atualiza o LiveData.
            mediaIdade.postValue(CalculoEstatisticas.calcularMediaIdade(estudantes));

            // Atualiza a lista de estudantes aprovados.
            List<Estudante> listaAprovados = CalculoEstatisticas.getAprovados(estudantes);
            aprovados.postValue(listaAprovados != null ? listaAprovados : new ArrayList<>());

            // Atualiza a lista de estudantes reprovados.
            List<Estudante> listaReprovados = CalculoEstatisticas.getReprovados(estudantes);
            reprovados.postValue(listaReprovados != null ? listaReprovados : new ArrayList<>());

        } catch (Exception e) {
            Log.e("EstatisticasVM", "Erro ao calcular estatísticas", e);
        }
    }

    // Método chamado quando o ViewModel for destruído — finaliza o executor para liberar a thread.
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}

