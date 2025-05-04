package com.example.diarioestudantesmvvm.util;

import com.example.diarioestudantesmvvm.model.Estudante;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CalculoEstatisticas {

    // Calcula a média geral das notas de todos os estudantes
    public static double calcularMediaGeral(List<Estudante> estudantes) {
        // Verifica se a lista é nula ou vazia
        if (estudantes == null || estudantes.isEmpty()) return 0;

        double soma = 0;
        int contador = 0;

        // Percorre todos os estudantes para somar as médias individuais
        for (Estudante estudante : estudantes) {
            // Calcula a média do estudante atual
            double media = estudante.calcularMedia();
            soma += media;
            contador++;
        }

        // Retorna a média geral (soma das médias / quantidade de estudantes)
        return soma / contador;
    }

    // Encontra o estudante com a maior média de notas
    public static Estudante encontrarMaiorNota(List<Estudante> estudantes) {
        // Verifica se a lista é válida
        if (estudantes == null || estudantes.isEmpty()) return null;

        // Usa Collections.max com um comparador baseado na média
        return Collections.max(estudantes, Comparator.comparingDouble(Estudante::calcularMedia));
    }

    // Encontra o estudante com a menor média de notas
    public static Estudante encontrarMenorNota(List<Estudante> estudantes) {
        // Verifica se a lista é válida
        if (estudantes == null || estudantes.isEmpty()) return null;

        // Usa Collections.min com um comparador baseado na média
        return Collections.min(estudantes, Comparator.comparingDouble(Estudante::calcularMedia));
    }

    // Calcula a média de idade da turma
    public static double calcularMediaIdade(List<Estudante> estudantes) {
        // Verifica se a lista é válida
        if (estudantes == null || estudantes.isEmpty()) return 0;

        double soma = 0;
        // Soma todas as idades
        for (Estudante estudante : estudantes) {
            soma += estudante.getIdade();
        }

        // Retorna a média (soma das idades / quantidade de estudantes)
        return soma / estudantes.size();
    }

    // Retorna lista de estudantes aprovados
    public static List<Estudante> getAprovados(List<Estudante> estudantes) {
        List<Estudante> aprovados = new ArrayList<>();
        // Verifica se a lista é nula
        if (estudantes == null) return aprovados;

        // Filtra os estudantes aprovados
        for (Estudante estudante : estudantes) {
            if (estudante.verificarSituacao().equals("Aprovado")) {
                aprovados.add(estudante);
            }
        }
        return aprovados;
    }

    // Retorna lista de estudantes reprovados
    public static List<Estudante> getReprovados(List<Estudante> estudantes) {
        List<Estudante> reprovados = new ArrayList<>();
        // Verifica se a lista é nula
        if (estudantes == null) return reprovados;

        // Filtra os estudantes reprovados
        for (Estudante estudante : estudantes) {
            if (estudante.verificarSituacao().equals("Reprovado")) {
                reprovados.add(estudante);
            }
        }
        return reprovados;
    }
}
