package com.example.diarioestudantesmvvm.util;

import android.util.Log;

import com.example.diarioestudantesmvvm.model.Estudante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EstudantesRepository {
    private static EstudantesRepository instance;
    private List<Estudante> estudantes = new ArrayList<>();
    private final Conexao conexao = new Conexao();
    private final String URL_BASE = "https://10.0.2.2:8080/estudantes/";

    private EstudantesRepository() {}

    public static synchronized EstudantesRepository getInstance() {
        if (instance == null) {
            instance = new EstudantesRepository();
        }
        return instance;
    }

    public void setEstudantes(List<Estudante> novosEstudantes) {
        this.estudantes = novosEstudantes != null ? new ArrayList<>(novosEstudantes) : new ArrayList<>();
    }

    public List<Estudante> getEstudantes() {
        return new ArrayList<>(estudantes);
    }

    // Método movido do EstatisticasViewModel
    public List<Estudante> buscarTodosEstudantesCompletos() {
        List<Estudante> estudantesCompletos = new ArrayList<>();

        try {
            InputStream resposta = conexao.obterRespostaHTTPS(URL_BASE);
            if (resposta == null) {
                Log.e("EstudantesRepo", "Resposta nula ao buscar estudantes");
                return null;
            }

            String json = conexao.converter(resposta);
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objBasico = jsonArray.getJSONObject(i);
                int id = objBasico.getInt("id");
                Estudante estudanteCompleto = buscarEstudantePorId(id);
                if (estudanteCompleto != null) {
                    estudantesCompletos.add(estudanteCompleto);
                }
            }

            // Atualiza o cache local
            setEstudantes(estudantesCompletos);

        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao buscar estudantes completos", e);
            return null;
        }

        return estudantesCompletos;
    }

    // Método movido do EstatisticasViewModel
    public Estudante buscarEstudantePorId(int id) {
        try {
            String url = URL_BASE + id;
            InputStream resposta = conexao.obterRespostaHTTPS(url);
            if (resposta == null) {
                Log.e("EstudantesRepo", "Resposta nula para estudante ID: " + id);
                return null;
            }

            String json = conexao.converter(resposta);
            JSONObject obj = new JSONObject(json);

            return new Estudante(
                    obj.getInt("id"),
                    obj.getString("nome"),
                    obj.getInt("idade"),
                    converterJsonParaListaDouble(obj.getJSONArray("notas")),
                    converterJsonParaListaBoolean(obj.getJSONArray("presenca"))
            );
        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao buscar estudante ID: " + id, e);
            return null;
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
}
