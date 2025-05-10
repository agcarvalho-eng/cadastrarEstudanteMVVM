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

// Classe ViewModel que cuida da lista de estudantes e observa suas alterações
public class EstudantesViewModel extends ViewModel implements DefaultLifecycleObserver {

    // LiveData que armazena e expõe a lista de estudantes para observação pela UI.
    private final MutableLiveData<List<Estudante>> estudantesLiveData = new MutableLiveData<>();

    // Repositório responsável por fornecer os dados dos estudantes.
    private final EstudantesRepository repository = EstudantesRepository.getInstance();

    // Executor que lida com tarefas em segundo plano.
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Manipulador que permite controlar a tarefa agendada.
    private ScheduledFuture<?> manipulador;

    // Cache local para evitar atualizações desnecessárias no LiveData.
    private List<Estudante> cacheEstudantes = new ArrayList<>();

    // Método para permitir que a UI observe a lista de estudantes.
    public LiveData<List<Estudante>> getEstudantes() {
        return estudantesLiveData;
    }

    // Chamado quando o ciclo de vida entra no estado STARTED.
    @Override
    public void onStart(@NonNull LifecycleOwner lifecycleOwner) {
        // Evita iniciar a tarefa se já estiver ativa.
        if (manipulador != null && !manipulador.isCancelled()) {
            return;
        }

        // Agenda a tarefa que busca estudantes a cada 30 segundos.
        manipulador = executor.scheduleWithFixedDelay(() -> {
            try {
                // Obtém nova lista de estudantes do repositório.
                List<Estudante> novosEstudantes = repository.buscarTodosEstudantes();

                // Verifica se a lista mudou em relação ao cache local.
                if (!novosEstudantes.equals(cacheEstudantes)) {
                    // Atualiza o cache.
                    cacheEstudantes = novosEstudantes;

                    // Atualiza o LiveData para notificar observadores.
                    estudantesLiveData.postValue(novosEstudantes);

                    // Atualiza a lista no repositório.
                    repository.setEstudantes(novosEstudantes);
                }
            } catch (Exception e) {
                // Loga erros em caso de falhas na requisição.
                Log.e("EstudantesVM", "Erro ao carregar estudantes", e);
            }
        }, 0, 30, TimeUnit.SECONDS); // Executa imediatamente e depois a cada 30 segundos.
    }

    // Chamado quando o ciclo de vida entra no estado STOPPED.
    @Override
    public void onStop(@NonNull LifecycleOwner lifecycleOwner) {
        // Cancela a tarefa agendada quando a UI não estiver mais visível.
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    // Permite que a UI force o recarregamento da lista de estudantes.
    public void recarregarEstudantes() {
        // Cancela tarefa atual, se houver.
        if (manipulador != null) {
            manipulador.cancel(false);
        }

        // Reinicia a tarefa de atualização.
        onStart(null);
    }

    // Método chamado quando o ViewModel é destruído.
    @Override
    protected void onCleared() {
        super.onCleared();

        // Encerra o executor para liberar recursos.
        executor.shutdown();
    }
}

