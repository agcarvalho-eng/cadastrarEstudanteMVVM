package com.example.cadastrarEstudanteMVVM.util;

import android.util.Log;

import com.example.cadastrarEstudanteMVVM.model.Estudante;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EstudantesRepository {
    private List<Estudante> estudantes;
    private static EstudantesRepository instance;
    private final Conexao conexao = new Conexao();
    private final String URL = "https://10.0.2.2:8080/estudantes/";
    private final Gson gson = new Gson();

    private EstudantesRepository() {}

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

    // Busca todos os estudantes (apenas dados básicos)
    public List<Estudante> buscarTodosEstudantes() {
        try {
            InputStream resposta = conexao.fazerRequisicao(URL, "GET", null);
            String json = conexao.converter(resposta);
            Type listType = new TypeToken<List<Estudante>>(){}.getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao buscar estudantes", e);
            return new ArrayList<>();
        }
    }

    // Busca dados completos de um estudante específico
    public Estudante buscarDadosEstudante(int id) {
        try {
            InputStream resposta = conexao.fazerRequisicao(URL + id, "GET", null);
            String json = conexao.converter(resposta);
            return gson.fromJson(json, Estudante.class);
        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao buscar estudante ID: " + id, e);
            return null;
        }
    }

    // Cadastra um novo estudante
    public boolean cadastrarEstudante(Estudante estudante) {
        try {
            String json = gson.toJson(estudante);
            conexao.enviarPost(URL, json);
            return true;
        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao cadastrar estudante", e);
            return false;
        }
    }


    // Atualiza um estudante existente (novo método)
    public boolean atualizarEstudante(Estudante estudante) {
        try {
            String json = gson.toJson(estudante);
            conexao.enviarPut(URL + estudante.getId(), json);
            return true;
        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao atualizar estudante ID: " + estudante.getId(), e);
            return false;
        }
    }

    // Remove um estudante (novo método)
    public boolean deletarEstudante(int id) {
        try {
            conexao.enviarDelete(URL + id);
            return true;
        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao deletar estudante ID: " + id, e);
            return false;
        }
    }

    // Método buscar todos os estudantes completos
    public List<Estudante> buscarTodosEstudantesCompletos() {
        try {
            InputStream resposta = conexao.fazerRequisicao(URL, "GET", null);
            if (resposta == null) {
                Log.e("EstudantesRepo", "Resposta nula ao buscar estudantes!");
                return null;
            }

            String json = conexao.converter(resposta);
            Type listType = new TypeToken<List<Estudante>>() {}.getType();
            List<Estudante> estudantes = gson.fromJson(json, listType);
            List<Estudante> estudantesCompletos = new ArrayList<>();
            for (Estudante estudante : estudantes) {
                estudante = buscarDadosEstudante(estudante.getId());
                estudantesCompletos.add(estudante);
            }

            setEstudantes(estudantesCompletos);
            return estudantesCompletos;
        } catch (Exception e) {
            Log.e("EstudantesRepo", "Erro ao buscar estudantes completos!", e);
            return null;
        }
    }
}
