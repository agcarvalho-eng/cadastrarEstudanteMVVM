package com.example.cadastrarEstudanteMVVM.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cadastrarEstudanteMVVM.databinding.ActivityAdicionarNotaBinding;
import com.example.cadastrarEstudanteMVVM.util.AdicionarNotaViewModel;

// Classe responsável por fazer a adição de uma nota a um estudante.
public class AdicionarNotaActivity extends AppCompatActivity {

    // View Binding para acessar os componentes da interface de forma segura.
    private ActivityAdicionarNotaBinding binding;

    // ViewModel responsável pela lógica de adicionar nota.
    private AdicionarNotaViewModel viewModel;

    // ID do estudante recebido da Intent.
    private int estudanteId;

    // Código de requisição, caso essa Activity seja chamada esperando retorno.
    public static final int REQUEST_CODE_ADD_NOTA = 1001;

    // Método chamado quando a Activity é criada.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa o binding da interface com o layout correspondente.
        binding = ActivityAdicionarNotaBinding.inflate(getLayoutInflater());

        // Define o conteúdo da Activity como a raiz do layout.
        setContentView(binding.getRoot());

        // Inicializa o ViewModel que será usado para adicionar a nota.
        initializeViewModel();

        // Recupera o ID do estudante a partir da Intent recebida.
        getEstudanteIdFromIntent();

        // Configura os componentes da interface (como cliques de botão).
        setupViews();
    }

    // Inicializa o ViewModel usando a arquitetura Android Jetpack.
    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(AdicionarNotaViewModel.class);
    }

    // Recupera o ID do estudante que foi passado pela Activity anterior.
    private void getEstudanteIdFromIntent() {
        estudanteId = getIntent().getIntExtra("ESTUDANTE_ID", -1);

        // Verifica se o ID é válido, senão mostra erro e finaliza a tela.
        if (estudanteId == -1) {
            showToast("ID do estudante inválido");
            finish();
        }
    }

    // Configura os eventos da interface, como o clique no botão de salvar nota.
    private void setupViews() {
        binding.btnSalvarNota.setOnClickListener(v -> salvarNota());
    }

    // Método chamado ao clicar no botão de salvar nota.
    private void salvarNota() {
        // Obtém o texto digitado pelo usuário no campo de nota.
        String notaStr = binding.editNota.getText().toString().trim();

        // Valida o conteúdo digitado antes de prosseguir.
        if (!validarNota(notaStr)) {
            return;
        }

        // Converte a string para double.
        double nota = Double.parseDouble(notaStr);

        // Chama o método que irá registrar a nota no ViewModel.
        registrarNota(nota);
    }

    // Valida se a nota digitada é um número válido entre 0 e 10.
    private boolean validarNota(String notaStr) {
        if (notaStr.isEmpty()) {
            showToast("Digite uma nota");
            return false;
        }

        try {
            double nota = Double.parseDouble(notaStr);

            // Verifica se a nota está no intervalo permitido.
            if (nota < 0 || nota > 10) {
                showToast("Nota deve estar entre 0 e 10");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            // Caso o valor digitado não seja um número válido.
            showToast("Nota inválida");
            return false;
        }
    }

    // Envia a nota para ser registrada via ViewModel.
    private void registrarNota(double nota) {
        viewModel.adicionarNota(estudanteId, nota, new AdicionarNotaViewModel.OnNotaAdicionadaListener() {

            // Callback de sucesso: exibe mensagem e redireciona para detalhes.
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showToast("Nota adicionada com sucesso");
                    redirecionarParaDetalhes();
                });
            }

            // Callback de erro: exibe a mensagem de erro ao usuário.
            @Override
            public void onError(String mensagem) {
                runOnUiThread(() -> showToast(mensagem));
            }
        });
    }

    // Redireciona para a tela de detalhes do estudante após adicionar a nota.
    private void redirecionarParaDetalhes() {
        Intent intent = new Intent(this, DetalhesEstudanteActivity.class);

        // Repassa o ID do estudante.
        intent.putExtra("ESTUDANTE_ID", estudanteId);

        // Garante que a Activity de detalhes não será duplicada na pilha.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Inicia a Activity de detalhes e finaliza a atual.
        startActivity(intent);
        finish();
    }

    // Exibe uma mensagem Toast com o texto informado.
    private void showToast(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}

