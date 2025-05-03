package com.example.diarioestudantesmvvm.util;

import android.net.SSLCertificateSocketFactory;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

// Classe Conexao lida com operações relacionadas à conexão HTTP e conversão de dados recebidos
public class Conexao {

    // Método para obter a resposta de uma URL através de uma requisição HTTP (Aqui será tipo GET)
    public InputStream obterRespostaHTTPS(String end) {
        try {
            // Cria um objeto URL a partir da string fornecida (URL do servidor)
            URL url = new URL(end);

            // Abre a conexão HTTP com a URL fornecida
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
            con.setHostnameVerifier(new AllowAllHostnameVerifier());

            // Define o método de requisição HTTP como GET (padrão para obter dados)
            con.setRequestMethod("GET");

            // Retorna o fluxo de entrada da resposta da requisição HTTP
            return con.getInputStream();
        } catch (MalformedURLException e) {
            // Exceção lançada quando a URL fornecida é inválida, imprime o erro
            e.printStackTrace();
        } catch (IOException e) {
            // Exceção lançada em caso de erro de entrada/saída (falha de rede ou na requisição)
            e.printStackTrace();
        }

        // Caso haja algum erro, retorna null
        return null;
    }

    // Método para converter o conteúdo de um InputStream (fluxo de dados) em uma String
    public String converter(InputStream inputStream) {
        // Cria um InputStreamReader para ler o fluxo de bytes e convertê-lo em caracteres
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        // Cria um BufferedReader para ler o conteúdo de maneira mais eficiente
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // Construir a String final
        StringBuilder stringBuilder = new StringBuilder();

        // Variável para armazenar cada linha lida do BufferedReader
        String conteudo = "";

        try {
            // Enquanto houver linhas para ler, continua lendo e adicionando ao StringBuilder
            while ((conteudo = bufferedReader.readLine()) != null) {
                stringBuilder.append(conteudo).append("\n");
            }
        } catch (IOException e) {
            // Exceção lançada em caso de erro de leitura do InputStream, imprime o erro
            e.printStackTrace();
        }

        // Retorna o conteúdo completo como uma String
        return stringBuilder.toString();
    }
}