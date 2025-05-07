package com.example.cadastrarEstudanteMVVM.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.cadastrarEstudanteMVVM.R;
import com.example.cadastrarEstudanteMVVM.databinding.ActivityCadastrarEstudanteBinding;
import com.example.cadastrarEstudanteMVVM.model.Estudante;
import com.example.cadastrarEstudanteMVVM.util.CadastrarEstudanteViewModel;

public class CadastrarEstudanteActivity extends AppCompatActivity {

    private ActivityCadastrarEstudanteBinding binding;
    private CadastrarEstudanteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cadastrar_estudante);
        viewModel = new ViewModelProvider(this).get(CadastrarEstudanteViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        binding.btnSalvar.setOnClickListener(v -> {
            String nome = binding.editTextNome.getText().toString();
            String idadeString = binding.editTextIdade.getText().toString();

            if (!nome.isEmpty() && !idadeString.isEmpty()) {
                int idade = Integer.parseInt(idadeString);
                Estudante novoEstudante = new Estudante(nome, idade);
                viewModel.adicionarEstudante(novoEstudante);
                finish(); // Fecha a activity após cadastrar
            }
        });
    }
}



