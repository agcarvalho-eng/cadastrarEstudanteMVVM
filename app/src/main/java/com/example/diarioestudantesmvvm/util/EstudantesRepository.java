package com.example.diarioestudantesmvvm.util;

import com.example.diarioestudantesmvvm.model.Estudante;

import java.util.ArrayList;
import java.util.List;

public class EstudantesRepository {
    private static EstudantesRepository instance;
    private List<Estudante> estudantes = new ArrayList<>();

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
}
