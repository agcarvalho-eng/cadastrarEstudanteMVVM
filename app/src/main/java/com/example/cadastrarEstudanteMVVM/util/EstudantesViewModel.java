package com.example.cadastrarEstudanteMVVM.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cadastrarEstudanteMVVM.model.Estudante;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EstudantesViewModel extends ViewModel implements DefaultLifecycleObserver {

    private final MutableLiveData<List<Estudante>> estudantesLiveData = new MutableLiveData<>();
    private final String URL = "https://10.0.2.2:8080/estudantes/";
    private EstudantesRepository estudantesRepository = EstudantesRepository.getInstance();
    // Executor responsável por agendamento de tarefas periódicas
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Referência da tarefa agendada para permitir cancelamento
    private ScheduledFuture<?> manipulador;

    private List<Estudante> cacheEstudantes = new ArrayList<>();

    public LiveData<List<Estudante>> getEstudantes() {
        return estudantesLiveData;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner lifecycleOwner) {
        if (manipulador != null && !manipulador.isCancelled()) {
            return; // Já está rodando
        }

        manipulador = executor.scheduleWithFixedDelay(() -> {
            try {

                List<Estudante> novosEstudantes = new ArrayList<>();
                novosEstudantes = estudantesRepository.buscarTodosEstudantes();

                if (!novosEstudantes.equals(cacheEstudantes)) {
                    cacheEstudantes = novosEstudantes;
                    estudantesLiveData.postValue(novosEstudantes);
                    // Seta os novos estudantes
                    EstudantesRepository.getInstance().setEstudantes(novosEstudantes);
                }

            } catch (Exception e) {
                Log.e("Erro", "Exceção ao processar dados", e);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    // Método onStop do ciclo de vida, que é chamado quando o LifecycleOwner (Activity/Fragment) entra no estado STOPPED.
    @Override
    public void onStop(@NonNull LifecycleOwner lifecycleOwner) {
        // Verifica se há uma tarefa assíncrona em andamento (manipulador != null)
        if (manipulador != null) {
            // Cancela a tarefa, mas sem interromper se já estiver executando (false = não forçar interrupção abrupta)
            manipulador.cancel(false);
        }
    }

    // Método público que permite forçar a recarga dos estudantes
    public void recarregarEstudantes() {
        // Se houver uma tarefa em execução, cancela-a antes de iniciar uma nova
        if (manipulador != null) {
            manipulador.cancel(false); // Cancela a tarefa atual
        }
        // Inicia novamente o processo de carregamento (simula o onStart manualmente)
        onStart(null); // Força um novo carregamento
    }

    // Método para limpeza de recursos quando o ViewModel é destruído
    @Override
    protected void onCleared() {
        // Chama a implementação padrão do método para garantir comportamento esperado
        super.onCleared();
        // Encerra o executor (gerenciador de tarefas assíncronas) liberando recursos
        executor.shutdown();
    }
}
