package com.example.cadastrarEstudanteMVVM.util;

import android.util.Log;

import com.example.cadastrarEstudanteMVVM.model.Estudante;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// Classe repositório para acesso e manipulação de dados dos estudantes
public class EstudantesRepository {

    // Lista interna para armazenar estudantes em memória.
    private List<Estudante> estudantes;

    // Instância única (singleton) da classe.
    private static EstudantesRepository instance;

    // Objeto responsável por realizar conexões HTTP.
    private final Conexao conexao = new Conexao();

    // URL base da API de estudantes (ajustada para uso com emulador Android local).
    private final String URL = "https://10.0.2.2:8080/estudantes/";

    // Instância da biblioteca Gson para converter objetos Java em JSON e vice-versa.
    private final Gson gson = new Gson();

    // Construtor privado para garantir singleton.
    private EstudantesRepository() {}

    // Método para obter a instância única da classe.
    public static synchronized EstudantesRepository getInstance() {
        if (instance == null) {
            instance = new EstudantesRepository();
        }
        return instance;
    }

    // Define a lista local de estudantes.
    public void setEstudantes(List<Estudante> estudantes) {
        this.estudantes = estudantes;
    }

    // Busca todos os estudantes da API com dados básicos (nome, id, etc.).
    public List<Estudante> buscarTodosEstudantes() {
        try {
            // Realiza requisição GET.
            InputStream resposta = conexao.fazerRequisicao(URL, "GET", null);

            // Converte resposta em JSON.
            String json = conexao.converter(resposta);

            // Converte JSON para lista de objetos Estudante.
            Type listType = new TypeToken<List<Estudante>>(){}.getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            // Em caso de erro, loga a exceção e retorna uma lista vazia.
            Log.e("EstudantesRepo", "Erro ao buscar estudantes", e);
            return new ArrayList<>();
        }
    }

    // Busca informações detalhadas de um estudante específico a partir de seu ID.
    public Estudante buscarDadosEstudante(int id) {
        try {
            // Requisição GET para o endpoint do estudante específico.
            InputStream resposta = conexao.fazerRequisicao(URL + id, "GET", null);

            // Converte resposta JSON para objeto Estudante.
            String json = conexao.converter(resposta);
            return gson.fromJson(json, Estudante.class);
        } catch (Exception e) {
            // Loga erro e retorna null em caso de falha.
            Log.e("EstudantesRepo", "Erro ao buscar estudante ID: " + id, e);
            return null;
        }
    }

    // Cadastra um novo estudante enviando dados via POST.
    public boolean cadastrarEstudante(Estudante estudante) {
        try {
            // Converte estudante para JSON.
            String json = gson.toJson(estudante);

            // Envia requisição POST com o JSON.
            conexao.enviarPost(URL, json);
            return true;
        } catch (Exception e) {
            // Loga erro e retorna false em caso de falha.
            Log.e("EstudantesRepo", "Erro ao cadastrar estudante", e);
            return false;
        }
    }

    // Atualiza os dados de um estudante existente via PUT.
    public boolean atualizarEstudante(Estudante estudante) {
        try {
            // Converte estudante atualizado para JSON.
            String json = gson.toJson(estudante);

            // Envia requisição PUT para o endpoint correspondente ao ID.
            conexao.enviarPut(URL + estudante.getId(), json);
            return true;
        } catch (Exception e) {
            // Loga erro e retorna false se ocorrer exceção.
            Log.e("EstudantesRepo", "Erro ao atualizar estudante ID: " + estudante.getId(), e);
            return false;
        }
    }

    // Remove um estudante do sistema via DELETE.
    public boolean deletarEstudante(int id) {
        try {
            // Envia requisição DELETE para o ID correspondente.
            conexao.enviarDelete(URL + id);
            return true;
        } catch (Exception e) {
            // Loga erro e retorna false em caso de falha.
            Log.e("EstudantesRepo", "Erro ao deletar estudante ID: " + id, e);
            return false;
        }
    }

    // Busca todos os estudantes com seus dados completos, incluindo notas e presença.
    public List<Estudante> buscarTodosEstudantesCompletos() {
        try {
            // Requisição GET para buscar lista inicial de estudantes (dados básicos).
            InputStream resposta = conexao.fazerRequisicao(URL, "GET", null);

            // Verifica se houve resposta.
            if (resposta == null) {
                Log.e("EstudantesRepo", "Resposta nula ao buscar estudantes!");
                return null;
            }

            // Converte a resposta para JSON.
            String json = conexao.converter(resposta);

            // Converte JSON para lista inicial de estudantes.
            Type listType = new TypeToken<List<Estudante>>() {}.getType();
            List<Estudante> estudantes = gson.fromJson(json, listType);

            // Lista que será preenchida com estudantes detalhados.
            List<Estudante> estudantesCompletos = new ArrayList<>();

            // Para cada estudante, busca os dados completos e adiciona à nova lista.
            for (Estudante estudante : estudantes) {
                estudante = buscarDadosEstudante(estudante.getId());
                estudantesCompletos.add(estudante);
            }

            // Atualiza lista local e retorna.
            setEstudantes(estudantesCompletos);
            return estudantesCompletos;
        } catch (Exception e) {
            // Loga erro e retorna null em caso de exceção.
            Log.e("EstudantesRepo", "Erro ao buscar estudantes completos!", e);
            return null;
        }
    }
}

