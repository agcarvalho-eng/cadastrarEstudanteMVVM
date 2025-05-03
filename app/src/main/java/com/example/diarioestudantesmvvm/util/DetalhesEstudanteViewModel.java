package com.example.diarioestudantesmvvm.util;

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

public class DetalhesEstudanteViewModel extends ViewModel {
    private final MutableLiveData<Estudante> estudanteLiveData = new MutableLiveData<>();
    private final Conexao conexao = new Conexao();

    public LiveData<Estudante> getEstudante() {
        return estudanteLiveData;
    }

    public void carregarEstudante(int estudanteId) {
        new Thread(() -> {
            try {
                String url = "https://10.0.2.2:8080/estudantes/" + estudanteId;
                InputStream resposta = conexao.obterRespostaHTTPS(url);
                String json = conexao.converter(resposta);

                JSONObject obj = new JSONObject(json);
                Estudante estudante = new Estudante(
                        obj.getInt("id"),
                        obj.getString("nome"),
                        obj.getInt("idade"),
                        // Converte JSONArray para List<Double>
                        // Implemente este método conforme sua necessidade
                        converterJsonParaListaDouble(obj.getJSONArray("notas")),
                        // Converte JSONArray para List<Boolean>
                        converterJsonParaListaBoolean(obj.getJSONArray("presenca"))
                );

                estudanteLiveData.postValue(estudante);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Métodos auxiliares para conversão
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
}
