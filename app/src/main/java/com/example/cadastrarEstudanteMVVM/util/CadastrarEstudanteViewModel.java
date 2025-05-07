package com.example.cadastrarEstudanteMVVM.util;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.cadastrarEstudanteMVVM.model.Estudante;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CadastrarEstudanteViewModel extends ViewModel {

    // Repositório onde os dados serão persistidos
    private final EstudantesRepository estudantesRepository = EstudantesRepository.getInstance();

    // Executor para operações assíncronas (evita bloquear a UI)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Método chamado pelo fragmento para adicionar um novo estudante
    public void adicionarEstudante(Estudante estudante) {
        executorService.execute(() -> {
            try {
                estudantesRepository.cadastrarEstudante(estudante);
                // Atualiza a lista de estudantes após cadastrar
                List<Estudante> listaAtualizada = estudantesRepository.buscarTodosEstudantes();
            } catch (Exception e) {
                Log.e("CadastrarEstudanteVM", "Erro ao adicionar estudante", e);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown(); // Libera recursos
    }
}
