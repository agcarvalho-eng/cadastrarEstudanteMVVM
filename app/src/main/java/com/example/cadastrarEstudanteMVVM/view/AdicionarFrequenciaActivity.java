package com.example.cadastrarEstudanteMVVM.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cadastrarEstudanteMVVM.R;
import com.example.cadastrarEstudanteMVVM.databinding.ActivityAdicionarFrequenciaBinding;
import com.example.cadastrarEstudanteMVVM.util.AdicionarFrequenciaViewModel;

public class AdicionarFrequenciaActivity extends AppCompatActivity {
    private ActivityAdicionarFrequenciaBinding binding;
    private AdicionarFrequenciaViewModel viewModel;
    private int estudanteId;
    public static final int REQUEST_CODE_ADD_FREQUENCIA = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdicionarFrequenciaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViewModel();
        getEstudanteIdFromIntent();
        setupViews();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(AdicionarFrequenciaViewModel.class);
    }

    private void getEstudanteIdFromIntent() {
        estudanteId = getIntent().getIntExtra("ESTUDANTE_ID", -1);
        if (estudanteId == -1) {
            showToast("ID do estudante inválido");
            finish();
        }
    }

    private void setupViews() {
        binding.btnSalvarFrequencia.setOnClickListener(v -> salvarFrequencia());
    }

    private void salvarFrequencia() {
        int selectedId = binding.radioGroupFrequencia.getCheckedRadioButtonId();

        if (!validarSelecao(selectedId)) {
            return;
        }

        boolean presente = selectedId == R.id.radioPresente;
        registrarFrequencia(presente);
    }

    private boolean validarSelecao(int selectedId) {
        if (selectedId == -1) {
            showToast("Selecione uma opção");
            return false;
        }
        return true;
    }

    private void registrarFrequencia(boolean presente) {
        viewModel.adicionarFrequencia(estudanteId, presente, new AdicionarFrequenciaViewModel.OnFrequenciaAdicionadaListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showToast("Frequência registrada");
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
