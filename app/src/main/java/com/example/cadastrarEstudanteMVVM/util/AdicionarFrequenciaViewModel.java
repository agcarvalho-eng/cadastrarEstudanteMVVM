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

// Classe ViewModel que observar mudanças quando adicionar frequência
public class AdicionarFrequenciaViewModel extends ViewModel implements DefaultLifecycleObserver {

    // Obtém a instância singleton do repositório dos estudantes
    private final EstudantesRepository repository = EstudantesRepository.getInstance();

    // Cria um executor de uma única thread para executar tarefas assíncronas
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Mantém a referência da tarefa agendada, permitindo controle sobre ela
    private ScheduledFuture<?> manipulador;

    // Interface para retornar o resultado da operação de adicionar frequência
    public interface OnFrequenciaAdicionadaListener {
        void onSuccess();                     // Chamado em caso de sucesso
        void onError(String mensagem);        // Chamado em caso de erro com uma mensagem
    }

    // Método chamado quando o ciclo de vida entra no estado "STARTED"
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        // Pode ser usado para iniciar operações periódicas se necessário
    }

    // Método que adiciona a frequência de um estudante de forma assíncrona
    public void adicionarFrequencia(int estudanteId, boolean presente, OnFrequenciaAdicionadaListener listener) {

        // Cancela qualquer tarefa anterior agendada que ainda não terminou
        if (manipulador != null && !manipulador.isDone()) {
            manipulador.cancel(false);
        }

        // Agenda a execução da lógica após 0 milissegundos (executa imediatamente)
        manipulador = executor.schedule(() -> {
            try {
                // Busca os dados do estudante pelo ID
                Estudante estudante = repository.buscarDadosEstudante(estudanteId);

                // Verifica se o estudante foi encontrado
                if (estudante != null) {
                    // Adiciona a nova presença (true ou false) à lista de presenças do estudante
                    estudante.getPresenca().add(presente);

                    // Atualiza o estudante no repositório com a nova presença adicionada
                    boolean sucesso = repository.atualizarEstudante(estudante);

                    // Se a atualização foi bem-sucedida, notifica sucesso
                    if (sucesso) {
                        listener.onSuccess();
                    } else {
                        // Se falhou ao atualizar no repositório, notifica erro
                        listener.onError("Falha ao atualizar frequência");
                    }

                } else {
                    // Se o estudante não foi encontrado, notifica erro
                    listener.onError("Estudante não encontrado");
                }

            } catch (Exception e) {
                // Captura qualquer exceção e notifica erro com a mensagem
                listener.onError("Erro ao adicionar frequência: " + e.getMessage());
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    // Método chamado quando o ciclo de vida entra no estado "STOPPED"
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        // Cancela qualquer tarefa agendada para evitar operações desnecessárias ou vazamento de memória
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    // Método chamado quando o ViewModel é destruído
    @Override
    protected void onCleared() {
        super.onCleared();
        // Encerra o executor para liberar os recursos da thread
        executor.shutdown();
    }
}
