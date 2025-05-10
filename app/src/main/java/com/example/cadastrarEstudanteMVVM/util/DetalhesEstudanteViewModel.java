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

// Classe ViewModel que observa mudanças nos detalhes de um estudante
public class DetalhesEstudanteViewModel extends ViewModel implements DefaultLifecycleObserver {

    // LiveData mutável que armazena o estudante atual e permite que a UI observe suas mudanças.
    private final MutableLiveData<Estudante> estudanteLiveData = new MutableLiveData<>();

    // Obtém a instância singleton do repositório de estudantes.
    private final EstudantesRepository repository = EstudantesRepository.getInstance();

    // Executor que roda uma thread dedicada para tarefas assíncronas.
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Controla tarefas periódicas agendadas (ex: atualização automática dos dados do estudante).
    private ScheduledFuture<?> manipulador;

    // Armazena o ID do estudante atualmente selecionado.
    private int estudanteIdAtual = -1;

    // Retorna o LiveData do estudante, usado pela UI para observar alterações.
    public LiveData<Estudante> getEstudante() {
        return estudanteLiveData;
    }

    // Método chamado quando a tela entra no estado STARTED.
    @Override
    public void onStart(@NonNull LifecycleOwner lifecycleOwner) {
        // Impede recomeçar a tarefa se já estiver agendada ou se nenhum estudante estiver selecionado.
        if (manipulador != null && !manipulador.isCancelled() || estudanteIdAtual == -1) {
            return;
        }

        // Agenda uma tarefa periódica que atualiza os dados do estudante a cada 30 segundos.
        manipulador = executor.scheduleWithFixedDelay(() -> {
            try {
                carregarDadosEstudante(estudanteIdAtual); // Busca e atualiza os dados no LiveData.
            } catch (Exception e) {
                Log.e("DetalhesEstVM", "Erro ao carregar estudante", e);
            }
        }, 0, 30, TimeUnit.SECONDS); // Começa imediatamente, repete a cada 30s.
    }

    // Método chamado quando a tela entra no estado STOPPED.
    @Override
    public void onStop(@NonNull LifecycleOwner lifecycleOwner) {
        // Cancela a tarefa periódica se estiver ativa.
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    // Define o ID do estudante que será trabalhado, e inicia o carregamento automático.
    public void setEstudanteId(int estudanteId) {
        this.estudanteIdAtual = estudanteId;
        recarregarEstudante(); // Inicia imediatamente após definir o ID.
    }

    // Recarrega manualmente os dados do estudante, cancelando a tarefa anterior (se houver).
    public void recarregarEstudante() {
        if (manipulador != null) {
            manipulador.cancel(false); // Cancela agendamento anterior.
        }
        onStart(null); // Reage como se estivesse reiniciando a tela.
    }

    // Carrega os dados do estudante a partir do repositório e atualiza o LiveData.
    private void carregarDadosEstudante(int estudanteId) {
        Estudante estudante = repository.buscarDadosEstudante(estudanteId);
        if (estudante != null) {
            estudanteLiveData.postValue(estudante); // Atualiza a UI reativamente.
        }
    }

    // Método que deleta um estudante, executado em background (off-thread).
    public void deletarEstudante(Callback callback) {
        if (estudanteIdAtual != -1) {
            // Executa em background para evitar bloqueio da UI (Thread principal).
            executor.execute(() -> {
                boolean sucesso = repository.deletarEstudante(estudanteIdAtual); // Tenta deletar
                callback.onResult(sucesso); // Retorna o resultado para quem chamou.
            });
        } else {
            callback.onResult(false); // ID inválido, falha imediata.
        }
    }

    // Interface para retorno assíncrono da operação de deleção.
    public interface Callback {
        void onResult(boolean sucesso); // Método que recebe se a operação teve sucesso ou não.
    }

    // Método chamado quando o ViewModel é destruído permanentemente.
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown(); // Encerra o executor e libera a thread usada.
    }
}

