package com.example.cadastrarEstudanteMVVM.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cadastrarEstudanteMVVM.R;
import com.example.cadastrarEstudanteMVVM.databinding.ActivityAdicionarFrequenciaBinding;
import com.example.cadastrarEstudanteMVVM.util.AdicionarFrequenciaViewModel;

// Classe responsável por fazer o registro de frequência de um estudante.
public class AdicionarFrequenciaActivity extends AppCompatActivity {

    // View Binding para acessar os elementos da interface de forma segura.
    private ActivityAdicionarFrequenciaBinding binding;

    // ViewModel responsável por gerenciar a lógica de negócio para adicionar frequência.
    private AdicionarFrequenciaViewModel viewModel;

    // ID do estudante recebido via Intent.
    private int estudanteId;

    // Código de requisição usado para identificar o retorno desta Activity, se necessário.
    public static final int REQUEST_CODE_ADD_FREQUENCIA = 1002;

    // Método chamado quando a Activity é criada.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa o binding da UI com o layout correspondente.
        binding = ActivityAdicionarFrequenciaBinding.inflate(getLayoutInflater());

        // Define a visualização da Activity como a raiz do layout.
        setContentView(binding.getRoot());

        // Inicializa o ViewModel.
        initializeViewModel();

        // Recupera o ID do estudante a partir da Intent recebida.
        getEstudanteIdFromIntent();

        // Configura os componentes da interface (botões, cliques, etc.).
        setupViews();
    }

    // Inicializa o ViewModel associado a esta Activity.
    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(AdicionarFrequenciaViewModel.class);
    }

    // Recupera o ID do estudante enviado pela Activity anterior.
    private void getEstudanteIdFromIntent() {
        estudanteId = getIntent().getIntExtra("ESTUDANTE_ID", -1);

        // Se o ID não for válido, exibe uma mensagem e encerra a Activity.
        if (estudanteId == -1) {
            showToast("ID do estudante inválido");
            finish();
        }
    }

    // Configura os eventos da interface, como o clique no botão de salvar frequência.
    private void setupViews() {
        binding.btnSalvarFrequencia.setOnClickListener(v -> salvarFrequencia());
    }

    // Método chamado ao clicar no botão de salvar frequência.
    private void salvarFrequencia() {
        // Obtém o ID da opção selecionada (Presente ou Ausente).
        int selectedId = binding.radioGroupFrequencia.getCheckedRadioButtonId();

        // Valida se alguma opção foi selecionada.
        if (!validarSelecao(selectedId)) {
            return;
        }

        // Define se o estudante está presente com base na opção selecionada.
        boolean presente = selectedId == R.id.radioPresente;

        // Chama o método que registra a frequência no ViewModel.
        registrarFrequencia(presente);
    }

    // Verifica se o usuário selecionou alguma opção no grupo de rádio.
    private boolean validarSelecao(int selectedId) {
        if (selectedId == -1) {
            showToast("Selecione uma opção");
            return false;
        }
        return true;
    }

    // Realiza o registro da frequência utilizando o ViewModel.
    private void registrarFrequencia(boolean presente) {
        viewModel.adicionarFrequencia(estudanteId, presente, new AdicionarFrequenciaViewModel.OnFrequenciaAdicionadaListener() {

            // Callback de sucesso: exibe mensagem e redireciona para os detalhes do estudante.
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showToast("Frequência registrada");
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

    // Redireciona para a tela de detalhes do estudante após o registro da frequência.
    private void redirecionarParaDetalhes() {
        Intent intent = new Intent(this, DetalhesEstudanteActivity.class);

        // Passa novamente o ID do estudante para a tela de detalhes.
        intent.putExtra("ESTUDANTE_ID", estudanteId);

        // Garante que a Activity de destino será reusada, evitando múltiplas instâncias.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Inicia a nova Activity e finaliza a atual.
        startActivity(intent);
        finish();
    }

    // Exibe uma mensagem Toast simples.
    private void showToast(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}

