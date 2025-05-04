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

public class EstatisticasViewModel extends ViewModel implements DefaultLifecycleObserver {
    // LiveData para cada estatística
    private final MutableLiveData<Double> mediaGeral = new MutableLiveData<>();
    private final MutableLiveData<String> alunoMaiorNota = new MutableLiveData<>();
    private final MutableLiveData<String> alunoMenorNota = new MutableLiveData<>();
    private final MutableLiveData<Double> mediaIdade = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> aprovados = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> reprovados = new MutableLiveData<>();

    // Executor para agendamento de tarefas
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> manipulador;
    private final Conexao conexao = new Conexao();
    private final String URL_BASE = "https://10.0.2.2:8080/estudantes/";

    // Getters para os LiveData
    public LiveData<Double> getMediaGeral() { return mediaGeral; }
    public LiveData<String> getAlunoMaiorNota() { return alunoMaiorNota; }
    public LiveData<String> getAlunoMenorNota() { return alunoMenorNota; }
    public LiveData<Double> getMediaIdade() { return mediaIdade; }
    public LiveData<List<Estudante>> getAprovados() { return aprovados; }
    public LiveData<List<Estudante>> getReprovados() { return reprovados; }

    @Override
    public void onStart(@NonNull LifecycleOwner lifecycleOwner) {
        // Se já houver um manipulador ativo, não cria outro
        if (manipulador != null && !manipulador.isCancelled()) return;

        // Agenda a tarefa para rodar imediatamente e a cada 30 segundos
        manipulador = executor.scheduleWithFixedDelay(() -> {
            try {
                // Busca a lista completa de estudantes com todos os dados
                List<Estudante> estudantesCompletos = buscarTodosEstudantesCompletos();

                // Se obteve estudantes válidos, calcula as estatísticas
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
        // Cancela a tarefa agendada quando a activity para
        if (manipulador != null) {
            manipulador.cancel(false);
        }
    }

    // Método para buscar todos os estudantes com informações completas
    private List<Estudante> buscarTodosEstudantesCompletos() {
        List<Estudante> estudantesCompletos = new ArrayList<>();

        try {
            // Primeiro busca a lista básica de estudantes
            InputStream resposta = conexao.obterRespostaHTTPS(URL_BASE);
            if (resposta == null) {
                Log.e("EstatisticasVM", "Resposta nula ao buscar estudantes");
                return null;
            }

            String json = conexao.converter(resposta);
            JSONArray jsonArray = new JSONArray(json);

            // Para cada estudante, busca suas informações completas
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objBasico = jsonArray.getJSONObject(i);
                int id = objBasico.getInt("id");

                // Busca os dados completos do estudante
                Estudante estudanteCompleto = buscarEstudantePorId(id);
                if (estudanteCompleto != null) {
                    estudantesCompletos.add(estudanteCompleto);
                }
            }
        } catch (Exception e) {
            Log.e("EstatisticasVM", "Erro ao buscar estudantes completos", e);
            return null;
        }

        return estudantesCompletos;
    }

    // Busca um estudante específico por ID com todas as informações
    private Estudante buscarEstudantePorId(int id) {
        try {
            String url = URL_BASE + id;
            InputStream resposta = conexao.obterRespostaHTTPS(url);
            if (resposta == null) {
                Log.e("EstatisticasVM", "Resposta nula para estudante ID: " + id);
                return null;
            }

            String json = conexao.converter(resposta);
            JSONObject obj = new JSONObject(json);

            // Converte o JSON para objeto Estudante com todas as informações
            return new Estudante(
                    obj.getInt("id"),
                    obj.getString("nome"),
                    obj.getInt("idade"),
                    converterJsonParaListaDouble(obj.getJSONArray("notas")),
                    converterJsonParaListaBoolean(obj.getJSONArray("presenca"))
            );
        } catch (Exception e) {
            Log.e("EstatisticasVM", "Erro ao buscar estudante ID: " + id, e);
            return null;
        }
    }

    // Calcula todas as estatísticas e atualiza os LiveData
    private void calcularEAtualizarEstatisticas(List<Estudante> estudantes) {
        // Verifica se a lista de estudantes é válida
        if (estudantes == null || estudantes.isEmpty()) {
            Log.w("EstatisticasVM", "Lista de estudantes vazia - não é possível calcular estatísticas");
            return;
        }

        try {
            // Calcula e atualiza a média geral
            double media = CalculoEstatisticas.calcularMediaGeral(estudantes);
            mediaGeral.postValue(media);

            // Encontra e atualiza o aluno com maior nota
            Estudante maiorNota = CalculoEstatisticas.encontrarMaiorNota(estudantes);
            alunoMaiorNota.postValue(maiorNota != null ?
                    String.format("%s (%.2f)", maiorNota.getNome(), maiorNota.calcularMedia()) : "Nenhum");

            // Encontra e atualiza o aluno com menor nota
            Estudante menorNota = CalculoEstatisticas.encontrarMenorNota(estudantes);
            alunoMenorNota.postValue(menorNota != null ?
                    String.format("%s (%.2f)", menorNota.getNome(), menorNota.calcularMedia()) : "Nenhum");

            // Calcula e atualiza a média de idade
            double idadeMedia = CalculoEstatisticas.calcularMediaIdade(estudantes);
            mediaIdade.postValue(idadeMedia);

            // Obtém e atualiza a lista de aprovados
            List<Estudante> listaAprovados = CalculoEstatisticas.getAprovados(estudantes);
            aprovados.postValue(listaAprovados != null ? listaAprovados : new ArrayList<>());

            // Obtém e atualiza a lista de reprovados
            List<Estudante> listaReprovados = CalculoEstatisticas.getReprovados(estudantes);
            reprovados.postValue(listaReprovados != null ? listaReprovados : new ArrayList<>());

        } catch (Exception e) {
            Log.e("EstatisticasVM", "Erro ao calcular estatísticas", e);
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

    // Força um recálculo das estatísticas
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
