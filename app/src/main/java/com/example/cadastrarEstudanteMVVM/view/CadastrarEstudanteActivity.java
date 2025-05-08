package com.example.cadastrarEstudanteMVVM.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cadastrarEstudanteMVVM.databinding.ActivityCadastrarEstudanteBinding;
import com.example.cadastrarEstudanteMVVM.model.Estudante;
import com.example.cadastrarEstudanteMVVM.util.CadastrarEstudanteViewModel;

public class CadastrarEstudanteActivity extends AppCompatActivity {
    private ActivityCadastrarEstudanteBinding binding;
    private CadastrarEstudanteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastrarEstudanteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViewModel();
        setupButtonClickListener();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(CadastrarEstudanteViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
    }

    private void setupButtonClickListener() {
        binding.btnSalvar.setOnClickListener(v -> cadastrarEstudante());
    }

    private void cadastrarEstudante() {
        String nome = binding.editTextNome.getText().toString().trim();
        String idadeStr = binding.editTextIdade.getText().toString().trim();

        if (!validarCampos(nome, idadeStr)) {
            return;
        }

        try {
            int idade = Integer.parseInt(idadeStr);
            Estudante estudante = new Estudante(nome, idade);
            executarCadastro(estudante);
        } catch (NumberFormatException e) {
            showToast("Idade inválida");
        }
    }

    private boolean validarCampos(String nome, String idadeStr) {
        if (nome.isEmpty() || idadeStr.isEmpty()) {
            showToast("Preencha todos os campos");
            return false;
        }
        return true;
    }

    private void executarCadastro(Estudante estudante) {
        viewModel.cadastrarEstudante(estudante, new CadastrarEstudanteViewModel.OnEstudanteCadastradoListener() {
            @Override
            public void onSuccess(Estudante estudanteCadastrado) {
                runOnUiThread(() -> {
                    showToast("Estudante cadastrado com sucesso");
                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(String mensagem) {
                runOnUiThread(() -> showToast(mensagem));
            }
        });
    }

    private void showToast(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}


