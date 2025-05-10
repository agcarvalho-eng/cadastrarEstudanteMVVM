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

// Classe ViewModel para observar as mudanças quando cadastrar um novo estudante
public class CadastrarEstudanteViewModel extends ViewModel implements DefaultLifecycleObserver {

    // Obtém a instância singleton do repositório responsável pelas operações com estudantes
    private final EstudantesRepository repository = EstudantesRepository.getInstance();

    // Executor que usa apenas uma thread para executar tarefas assíncronas em background
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Referência para a tarefa agendada, útil para cancelamento se necessário
    private ScheduledFuture<?> manipulador;

    // Interface de callback que notifica sucesso ou erro do processo de cadastro
    public interface OnEstudanteCadastradoListener {
        void onSuccess(Estudante estudante);       // Chamado quando o cadastro for bem-sucedido
        void onError(String mensagem);             // Chamado quando ocorre algum erro com mensagem explicativa
    }

    // Método executado quando a Activity entra no estado STARTED
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        // Pode ser utilizado para agendar tarefas recorrentes, se necessário
    }

    // Método para cadastrar um novo estudante de forma assíncrona
    public void cadastrarEstudante(Estudante estudante, OnEstudanteCadastradoListener listener) {

        // Se houver uma tarefa agendada anterior ainda em execução, ela é cancelada
        if (manipulador != null && !manipulador.isDone()) {
            manipulador.cancel(false);
        }

        // Agenda a execução da tarefa imediatamente (sem atraso)
        manipulador = executor.schedule(() -> {
            try {
                // Tenta cadastrar o estudante através do repositório
                boolean sucesso = repository.cadastrarEstudante(estudante);

                // Verifica o resultado do cadastro
                if (sucesso) {
                    // Se bem-sucedido, notifica o listener com o estudante cadastrado
                    listener.onSuccess(estudante);
                } else {
                    // Caso contrário, informa falha ao cadastrar
                    listener.onError("Falha ao cadastrar estudante");
                }

            } catch (Exception e) {
                // Em caso de exceção, informa erro com a mensagem detalhada
                listener.onError("Erro ao cadastrar: " + e.getMessage());
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    // Método chamado quando o ciclo de vida entra no estado STOPPED
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        // Cancela a tarefa agendada, se existir, para evitar operações desnecessárias
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    // Método chamado quando o ViewModel está sendo destruído permanentemente
    @Override
    protected void onCleared() {
        super.onCleared();
        // Encerra o executor para liberar os recursos da thread criada
        executor.shutdown();
    }
}

