package com.example.diarioestudantesmvvm.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class Diario {

    // Nome da chave Json
    @SerializedName("estudantes")
    @Expose // Este campo deve ser incluído no documento
    private List<Estudante> estudantes;

    // Construtores.
    public Diario() {
    }

    public Diario(List<Estudante> estudantes) {
        super();
        this.estudantes = estudantes;
    }

    // Getters e setters
    public List<Estudante> getEstudantes() {
        return estudantes;
    }

    public void setEstudantes(List<Estudante> estudantes) {
        this.estudantes = estudantes;
    }

    @Override
    public String toString() {
        return "Diario{" +
                "estudantes=" + estudantes +
                '}';
    }
}
