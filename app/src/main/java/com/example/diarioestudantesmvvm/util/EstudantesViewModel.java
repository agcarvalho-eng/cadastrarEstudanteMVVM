package com.example.diarioestudantesmvvm.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diarioestudantesmvvm.model.Estudante;

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
                Conexao conexao = new Conexao();
                InputStream resposta = conexao.obterRespostaHTTPS(URL);
                if (resposta == null) {
                    Log.e("Erro de Conexão", "Não foi possível obter a resposta da URL.");
                    return;
                }

                String json = conexao.converter(resposta);
                Log.d("JSON Resposta", json);

                JSONArray jsonArray = new JSONArray(json);
                List<Estudante> novosEstudantes = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Estudante estudante = new Estudante(
                            obj.getInt("id"),
                            obj.getString("nome"),
                            obj.getInt("idade")
                    );
                    novosEstudantes.add(estudante);
                }

                if (!novosEstudantes.equals(cacheEstudantes)) {
                    cacheEstudantes = novosEstudantes;
                    estudantesLiveData.postValue(novosEstudantes);
                    EstudantesRepository.getInstance().setEstudantes(novosEstudantes);
                }

            } catch (Exception e) {
                Log.e("Erro", "Exceção ao processar dados", e);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner lifecycleOwner) {
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    public void recarregarEstudantes() {
        if (manipulador != null) {
            manipulador.cancel(false); // Cancela a tarefa atual
        }
        onStart(null); // Força um novo carregamento
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
