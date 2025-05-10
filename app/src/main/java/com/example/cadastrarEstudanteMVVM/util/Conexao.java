package com.example.cadastrarEstudanteMVVM.util;

import android.net.SSLCertificateSocketFactory;
import android.util.Log;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

// Classe Conexao lida com operações relacionadas à conexão HTTP e conversão de dados recebidos
public class Conexao {

    // Método genérico para requisições HTTPS
    public InputStream fazerRequisicao(String urlString, String metodo, String json) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        // Configurações SSL para desenvolvimento
        con.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
        con.setHostnameVerifier(new AllowAllHostnameVerifier());

        // Configura o método HTTPS e cabeçalhos
        con.setRequestMethod(metodo);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(json != null);

        // Se houver corpo na requisição, envia os dados
        if (json != null) {
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        // Retorna o fluxo de entrada com a resposta
        return con.getInputStream();
    }

    // Método para enviar POST (criação)
    public void enviarPost(String urlString, String json) throws IOException {
        fazerRequisicao(urlString, "POST", json);
    }

    // Método para enviar PUT (atualização)
    public void enviarPut(String urlString, String json) throws IOException {
        fazerRequisicao(urlString, "PUT", json);
    }

    // Método para enviar DELETE (remoção)
    public void enviarDelete(String urlString) throws IOException {
        fazerRequisicao(urlString, "DELETE", null);
    }

    // Método para converter InputStream em String
    public String converter(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e("Conexao", "Erro ao converter InputStream", e);
        }

        return sb.toString();
    }

}