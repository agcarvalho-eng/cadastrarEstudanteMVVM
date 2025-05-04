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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DetalhesEstudanteViewModel extends ViewModel implements DefaultLifecycleObserver {
    // LiveData para os dados do estudante
    private final MutableLiveData<Estudante> estudanteLiveData = new MutableLiveData<>();
    private final Conexao conexao = new Conexao();
    private final String URL_BASE = "https://10.0.2.2:8080/estudantes/";

    // Executor para agendamento de tarefas
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> manipulador;
    private int estudanteIdAtual = -1;

    // Getter para o LiveData
    public LiveData<Estudante> getEstudante() {
        return estudanteLiveData;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner lifecycleOwner) {
        // Se já houver um manipulador ativo ou nenhum ID definido, não faz nada
        if (manipulador != null && !manipulador.isCancelled() || estudanteIdAtual == -1) {
            return;
        }

        // Agenda a tarefa para rodar imediatamente e a cada 30 segundos
        manipulador = executor.scheduleWithFixedDelay(() -> {
            try {
                carregarDadosEstudante(estudanteIdAtual);
            } catch (Exception e) {
                Log.e("DetalhesEstVM", "Erro ao carregar estudante", e);
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner lifecycleOwner) {
        // Cancela a tarefa agendada quando a activity para
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    // Método para configurar o ID do estudante e iniciar o carregamento
    public void setEstudanteId(int estudanteId) {
        this.estudanteIdAtual = estudanteId;
        recarregarEstudante();
    }

    // Força um recarregamento dos dados do estudante
    public void recarregarEstudante() {
        if (manipulador != null) {
            manipulador.cancel(false);
        }
        onStart(null);
    }

    // Método que realiza o carregamento dos dados do estudante
    private void carregarDadosEstudante(int estudanteId) {
        try {
            String url = URL_BASE + estudanteId;
            InputStream resposta = conexao.obterRespostaHTTPS(url);
            if (resposta == null) {
                Log.e("DetalhesEstVM", "Resposta nula para estudante ID: " + estudanteId);
                return;
            }

            String json = conexao.converter(resposta);
            JSONObject obj = new JSONObject(json);

            // Converte o JSON para objeto Estudante
            Estudante estudante = new Estudante(
                    obj.getInt("id"),
                    obj.getString("nome"),
                    obj.getInt("idade"),
                    converterJsonParaListaDouble(obj.getJSONArray("notas")),
                    converterJsonParaListaBoolean(obj.getJSONArray("presenca"))
            );

            // Atualiza o LiveData
            estudanteLiveData.postValue(estudante);

        } catch (Exception e) {
            Log.e("DetalhesEstVM", "Erro ao carregar estudante ID: " + estudanteId, e);
        }
    }

    // Métodos auxiliares para conversão de JSON
    private List<Double> converterJsonParaListaDouble(JSONArray jsonArray) throws JSONException {
        List<Double> lista = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            lista.add(jsonArray.getDouble(i));
        }
        return lista;
    }

    private List<Boolean> converterJsonParaListaBoolean(JSONArray jsonArray) throws JSONException {
        List<Boolean> lista = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            lista.add(jsonArray.getBoolean(i));
        }
        return lista;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
