package com.example.diarioestudantesmvvm.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.diarioestudantesmvvm.databinding.ActivityDetalhesEstudanteBinding;
import com.example.diarioestudantesmvvm.model.Estudante;
import com.example.diarioestudantesmvvm.util.DetalhesEstudanteViewModel;

public class DetalhesEstudanteActivity extends AppCompatActivity {
    private ActivityDetalhesEstudanteBinding binding;
    private DetalhesEstudanteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesEstudanteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configura o ViewModel
        viewModel = new ViewModelProvider(this).get(DetalhesEstudanteViewModel.class);
        getLifecycle().addObserver(viewModel);

        // Obtém o ID do estudante da Intent
        int estudanteId = getIntent().getIntExtra("ESTUDANTE_ID", -1);
        if (estudanteId != -1) {
            viewModel.setEstudanteId(estudanteId);
        }

        // Configura o botão voltar
        binding.btnVoltar.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        // Observa as mudanças nos dados do estudante
        viewModel.getEstudante().observe(this, estudante -> {
            if (estudante != null) {
                binding.setEstudante(estudante);

                // Atualiza os cálculos
                binding.textNotaFinal.setText(String.format("Nota Final: %.2f", estudante.calcularMedia()));
                binding.textPresenca.setText(String.format("Presença: %.1f%%", estudante.calcularPercentualPresenca()));
                binding.textSituacao.setText("Situação: " + estudante.verificarSituacao());
            }
        });
    }
}
