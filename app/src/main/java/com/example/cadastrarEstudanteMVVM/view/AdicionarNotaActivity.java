package com.example.cadastrarEstudanteMVVM.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cadastrarEstudanteMVVM.databinding.ActivityAdicionarNotaBinding;
import com.example.cadastrarEstudanteMVVM.util.AdicionarNotaViewModel;

public class AdicionarNotaActivity extends AppCompatActivity {
    private ActivityAdicionarNotaBinding binding;
    private AdicionarNotaViewModel viewModel;
    private int estudanteId;
    public static final int REQUEST_CODE_ADD_NOTA = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdicionarNotaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViewModel();
        getEstudanteIdFromIntent();
        setupViews();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(AdicionarNotaViewModel.class);
    }

    private void getEstudanteIdFromIntent() {
        estudanteId = getIntent().getIntExtra("ESTUDANTE_ID", -1);
        if (estudanteId == -1) {
            showToast("ID do estudante inválido");
            finish();
        }
    }

    private void setupViews() {
        binding.btnSalvarNota.setOnClickListener(v -> salvarNota());
    }

    private void salvarNota() {
        String notaStr = binding.editNota.getText().toString().trim();

        if (!validarNota(notaStr)) {
            return;
        }

        double nota = Double.parseDouble(notaStr);
        registrarNota(nota);
    }

    private boolean validarNota(String notaStr) {
        if (notaStr.isEmpty()) {
            showToast("Digite uma nota");
            return false;
        }

        try {
            double nota = Double.parseDouble(notaStr);
            if (nota < 0 || nota > 10) {
                showToast("Nota deve estar entre 0 e 10");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showToast("Nota inválida");
            return false;
        }
    }

    private void registrarNota(double nota) {
        viewModel.adicionarNota(estudanteId, nota, new AdicionarNotaViewModel.OnNotaAdicionadaListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showToast("Nota adicionada com sucesso");
                    redirecionarParaDetalhes();
                });
            }

            @Override
            public void onError(String mensagem) {
                runOnUiThread(() -> showToast(mensagem));
            }
        });
    }

    private void redirecionarParaDetalhes() {
        Intent intent = new Intent(this, DetalhesEstudanteActivity.class);
        intent.putExtra("ESTUDANTE_ID", estudanteId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void showToast(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}
