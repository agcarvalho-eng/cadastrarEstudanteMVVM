package com.example.cadastrarEstudanteMVVM.util;

import android.util.Log;

import com.example.cadastrarEstudanteMVVM.model.Estudante;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EstudantesRepository {
    private static EstudantesRepository instance;
    private List<Estudante> estudantes = new ArrayList<>();
    private final Conexao conexao = new Conexao();
    private final String URL = "https://10.0.2.2:8080/estudantes/";
    private final Gson gson = new Gson();

    private EstudantesRepository() {}

    /** Implementa o padrão Singleton, garantindo que apenas uma instância da classe
     *  EstudantesRepository seja criada e acessada de forma thread-safe.
     */
    public static synchronized EstudantesRepository getInstance() {
        if (instance == null) {
            instance = new EstudantesRepository();
        }
        return instance;
    }

    // Atualiza ou cria nova lista de estudantes
    public void setEstudantes(List<Estudante> estudantes) {
        this.estudantes = estudantes;
    }

    // Método para buscar todos os estudantes (id, nome e idade)
    public List<Estudante> buscarTodosEstudantes() {
        try {
            InputStream resposta = conexao.obterRespostaHTTPS(URL);
            if (resposta == null) {
                Log.e("EstudantesRepo", "Resposta nula ao buscar estudantes!");
                return null;
            }

            // Converte o JSON em uma nova lista de estudantes
            List<Estudante> novaListaEstudantes = new ArrayList<>();
            String json = conexao.converter(resposta);
            Type listType = new TypeToken<List<Estudante>>() {}.getType();
            List<Estudante> estudantes = gson.fromJson(json, listType);
            for (Estudante estudante : estudantes) {
                novaListaEstudantes.add(estudante);
            }

            this.estudantes = novaListaEstudantes;
            return estudantes;
        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao buscar estudantes!", e);
            return null;
        }
    }

    // Método para buscar dados completos de um estudante pelo Id
    public Estudante buscarDadosEstudante(int id) {
        try {
            String url = URL + id;
            InputStream resposta = conexao.obterRespostaHTTPS(url);

            if (resposta == null) {
                Log.e("EstudantesRepo", "Resposta nula ao buscar estudante ID: " + id);
                return null;
            }

            String json = conexao.converter(resposta);
            Estudante estudante = gson.fromJson(json, Estudante.class);
            return estudante;

        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao buscar dados do estudante ID: " + id, e);
            return null;
        }
    }

    // Método buscar todos os estudantes completos
    public List<Estudante> buscarTodosEstudantesCompletos() {
        try {
            InputStream resposta = conexao.obterRespostaHTTPS(URL);
            if (resposta == null) {
                Log.e("EstudantesRepo", "Resposta nula ao buscar estudantes!");
                return null;
            }

            String json = conexao.converter(resposta);
            Type listType = new TypeToken<List<Estudante>>() {}.getType();
            List<Estudante> estudantes = gson.fromJson(json, listType);
            List<Estudante> estudantesCompletos = new ArrayList<>();
            for (Estudante estudante : estudantes) {
                estudante = buscarEstudantePorId(estudante.getId());
                estudantesCompletos.add(estudante);
            }

            setEstudantes(estudantesCompletos);
            return estudantesCompletos;
        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao buscar estudantes completos!", e);
            return null;
        }
    }

    // Método buscar estudante pelo Id
    public Estudante buscarEstudantePorId(int id) {
        try {
            String url = URL + id;
            InputStream resposta = conexao.obterRespostaHTTPS(url);
            if (resposta == null) {
                Log.e("EstudantesRepo", "Resposta nula para estudante ID: " + id);
                return null;
            }

            String json = conexao.converter(resposta);
            return gson.fromJson(json, Estudante.class);
        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao buscar estudante ID: " + id, e);
            return null;
        }
    }

    // Cadastra um novo estudante via requisição HTTP POST
    public void cadastrarEstudante(Estudante estudante) {
        try {
            // Converte o objeto Estudante para JSON usando Gson
            String jsonEstudante = gson.toJson(estudante);

            // Adicione logging para depuração
            Log.d("EstudantesRepo", "JSON a ser enviado: " + jsonEstudante);

            // Envia uma requisição POST para o servidor com os dados do estudante
            conexao.enviarPost(URL, jsonEstudante);
            Log.i("EstudantesRepo", "Estudante cadastrado com sucesso: " + estudante.getNome());

        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao cadastrar estudante", e);
        }
    }


}
