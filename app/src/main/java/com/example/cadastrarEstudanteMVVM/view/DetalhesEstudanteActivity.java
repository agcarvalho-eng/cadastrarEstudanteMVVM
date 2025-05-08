package com.example.cadastrarEstudanteMVVM.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cadastrarEstudanteMVVM.databinding.ActivityDetalhesEstudanteBinding;
import com.example.cadastrarEstudanteMVVM.util.DetalhesEstudanteViewModel;

public class DetalhesEstudanteActivity extends AppCompatActivity {
    private ActivityDetalhesEstudanteBinding binding;
    private DetalhesEstudanteViewModel viewModel;
    private int estudanteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesEstudanteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtém o ViewModel
        viewModel = new ViewModelProvider(this).get(DetalhesEstudanteViewModel.class);
        getLifecycle().addObserver(viewModel);

        // Obtém o ID do estudante da Intent
        estudanteId = getIntent().getIntExtra("ESTUDANTE_ID", -1);
        if (estudanteId != -1) {
            viewModel.setEstudanteId(estudanteId);
        }

        // Configura os botões e observadores
        setupButtons();
        setupObservers();
    }

    private void setupButtons() {
        // Botão voltar
        binding.btnVoltar.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        // Botão adicionar nota
        binding.btnAdicionarNota.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdicionarNotaActivity.class);
            intent.putExtra("ESTUDANTE_ID", estudanteId);
            startActivityForResult(intent, 1);
        });

        // Botão adicionar frequência
        binding.btnAdicionarFrequencia.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdicionarFrequenciaActivity.class);
            intent.putExtra("ESTUDANTE_ID", estudanteId);
            startActivityForResult(intent, 2);
        });

        // Botão deletar estudante
        binding.btnDeletarEstudante.setOnClickListener(v -> mostrarDialogoConfirmacao());
    }

    private void setupObservers() {
        // Observa as mudanças nos dados do estudante
        viewModel.getEstudante().observe(this, estudante -> {
            if (estudante != null) {
                binding.setEstudante(estudante);
                binding.textNotaFinal.setText(String.format("Nota Final: %.2f", estudante.calcularMedia()));
                binding.textPresenca.setText(String.format("Presença: %.1f%%", estudante.calcularPercentualPresenca()));
                binding.textSituacao.setText("Situação: " + estudante.verificarSituacao());
            }
        });
    }

    private void mostrarDialogoConfirmacao() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Deleção")
                .setMessage("Tem certeza que deseja deletar este estudante?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    if (viewModel.deletarEstudante()) {
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            viewModel.recarregarEstudante();
        }
    }
}
