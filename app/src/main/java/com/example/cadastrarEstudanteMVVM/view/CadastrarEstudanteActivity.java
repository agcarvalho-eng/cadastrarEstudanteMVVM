package com.example.cadastrarEstudanteMVVM.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cadastrarEstudanteMVVM.databinding.ActivityCadastrarEstudanteBinding;
import com.example.cadastrarEstudanteMVVM.model.Estudante;
import com.example.cadastrarEstudanteMVVM.util.CadastrarEstudanteViewModel;

// Classe responsável por cadastrar um novo estudante.
public class CadastrarEstudanteActivity extends AppCompatActivity {

    // View Binding para acessar os elementos da interface.
    private ActivityCadastrarEstudanteBinding binding;

    // ViewModel que contém a lógica de cadastro do estudante.
    private CadastrarEstudanteViewModel viewModel;

    // Método chamado ao criar a Activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa o binding usando o layout XML correspondente.
        binding = ActivityCadastrarEstudanteBinding.inflate(getLayoutInflater());

        // Define o conteúdo da tela com o layout inflado.
        setContentView(binding.getRoot());

        // Inicializa o ViewModel e configura binding de ciclo de vida.
        initializeViewModel();

        // Configura o clique do botão de salvar.
        setupButtonClickListener();
    }

    // Inicializa o ViewModel e conecta-o ao binding da interface.
    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(CadastrarEstudanteViewModel.class);

        // Define o ViewModel no binding para uso com data binding (caso aplicável).
        binding.setViewModel(viewModel);

        // Define o ciclo de vida da Activity para que o binding observe atualizações.
        binding.setLifecycleOwner(this);
    }

    // Configura o botão de salvar para chamar o método de cadastro.
    private void setupButtonClickListener() {
        binding.btnSalvar.setOnClickListener(v -> cadastrarEstudante());
    }

    // Método executado ao clicar no botão para cadastrar.
    private void cadastrarEstudante() {
        // Captura os dados digitados pelo usuário nos campos de nome e idade.
        String nome = binding.editTextNome.getText().toString().trim();
        String idadeStr = binding.editTextIdade.getText().toString().trim();

        // Valida se os campos foram preenchidos corretamente.
        if (!validarCampos(nome, idadeStr)) {
            return;
        }

        try {
            // Converte a string de idade para inteiro.
            int idade = Integer.parseInt(idadeStr);

            // Cria um objeto Estudante com os dados fornecidos.
            Estudante estudante = new Estudante(nome, idade);

            // Chama o método para realizar o cadastro via ViewModel.
            executarCadastro(estudante);
        } catch (NumberFormatException e) {
            // Caso a idade não seja um número válido.
            showToast("Idade inválida");
        }
    }

    // Verifica se os campos nome e idade foram preenchidos.
    private boolean validarCampos(String nome, String idadeStr) {
        if (nome.isEmpty() || idadeStr.isEmpty()) {
            showToast("Preencha todos os campos");
            return false;
        }
        return true;
    }

    // Realiza o cadastro do estudante chamando o ViewModel.
    private void executarCadastro(Estudante estudante) {
        viewModel.cadastrarEstudante(estudante, new CadastrarEstudanteViewModel.OnEstudanteCadastradoListener() {

            // Callback chamado em caso de sucesso no cadastro.
            @Override
            public void onSuccess(Estudante estudanteCadastrado) {
                runOnUiThread(() -> {
                    showToast("Estudante cadastrado com sucesso");

                    // Define o resultado como OK para a Activity que chamou esta.
                    setResult(RESULT_OK);

                    // Finaliza a Activity e retorna para a anterior.
                    finish();
                });
            }

            // Callback chamado em caso de erro no cadastro.
            @Override
            public void onError(String mensagem) {
                runOnUiThread(() -> showToast(mensagem));
            }
        });
    }

    // Exibe uma mensagem Toast para o usuário.
    private void showToast(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}



