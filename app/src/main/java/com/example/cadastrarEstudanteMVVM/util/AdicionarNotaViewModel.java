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

// Classe ViewModel que observa mudanças ao adicionar uma nota ao estudante
public class AdicionarNotaViewModel extends ViewModel implements DefaultLifecycleObserver {

    // Obtém a instância singleton do repositório de estudantes
    private final EstudantesRepository repository = EstudantesRepository.getInstance();

    // Executor de thread única para executar tarefas assíncronas (fora da UI thread)
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Referência para controlar e cancelar tarefas agendadas se necessário
    private ScheduledFuture<?> manipulador;

    // Interface para comunicar o sucesso ou falha da adição de nota
    public interface OnNotaAdicionadaListener {
        void onSuccess();                     // Chamado quando a nota é adicionada com sucesso
        void onError(String mensagem);        // Chamado em caso de falha, com mensagem de erro
    }

    // Método chamado quando o ciclo de vida entra no estado "STARTED"
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        // Pode ser usado para iniciar verificações ou atualizações periódicas, se necessário
    }

    // Método que adiciona uma nota a um estudante de forma assíncrona
    public void adicionarNota(int estudanteId, double nota, OnNotaAdicionadaListener listener) {

        // Cancela qualquer tarefa anterior ainda pendente antes de iniciar uma nova
        if (manipulador != null && !manipulador.isDone()) {
            manipulador.cancel(false);
        }

        // Agenda uma nova tarefa para execução imediata (0 ms de atraso)
        manipulador = executor.schedule(() -> {
            try {
                // Busca o estudante pelo ID fornecido
                Estudante estudante = repository.buscarDadosEstudante(estudanteId);

                // Se o estudante for encontrado, adiciona a nota à lista de notas
                if (estudante != null) {
                    estudante.getNotas().add(nota);

                    // Atualiza o estudante com os novos dados (incluindo a nova nota)
                    boolean sucesso = repository.atualizarEstudante(estudante);

                    // Se a atualização for bem-sucedida, informa sucesso
                    if (sucesso) {
                        listener.onSuccess();
                    } else {
                        // Caso contrário, informa erro de atualização
                        listener.onError("Falha ao atualizar notas do estudante");
                    }

                } else {
                    // Se o estudante não for encontrado, informa erro
                    listener.onError("Estudante não encontrado");
                }

            } catch (Exception e) {
                // Captura exceções e informa erro com a mensagem da exceção
                listener.onError("Erro ao adicionar nota: " + e.getMessage());
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    // Método chamado quando o ciclo de vida entra no estado "STOPPED"
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        // Cancela qualquer tarefa agendada que ainda não terminou, liberando recursos
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    // Método chamado quando o ViewModel é destruído permanentemente
    @Override
    protected void onCleared() {
        super.onCleared();
        // Encerra o executor para liberar a thread utilizada
        executor.shutdown();
    }
}

