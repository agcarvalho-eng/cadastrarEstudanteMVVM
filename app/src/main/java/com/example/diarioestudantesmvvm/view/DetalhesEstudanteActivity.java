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

        viewModel = new ViewModelProvider(this).get(DetalhesEstudanteViewModel.class);

        int estudanteId = getIntent().getIntExtra("ESTUDANTE_ID", -1);
        if (estudanteId != -1) {
            viewModel.carregarEstudante(estudanteId);
        }

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
