package com.example.cadastrarEstudanteMVVM.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cadastrarEstudanteMVVM.databinding.ActivityDetalhesEstudanteBinding;
import com.example.cadastrarEstudanteMVVM.util.DetalhesEstudanteViewModel;

// Classe responsável por exibir os detalhes de um estudante.
public class DetalhesEstudanteActivity extends AppCompatActivity {

    // View Binding para acessar os elementos da interface.
    private ActivityDetalhesEstudanteBinding binding;

    // ViewModel responsável pela lógica de exibição dos detalhes do estudante.
    private DetalhesEstudanteViewModel viewModel;

    // ID do estudante que será exibido.
    private int estudanteId;

    // Método chamado ao criar a Activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa o binding usando o layout XML correspondente.
        binding = ActivityDetalhesEstudanteBinding.inflate(getLayoutInflater());

        // Define o conteúdo da tela com o layout inflado.
        setContentView(binding.getRoot());

        // Obtém o ViewModel para gerenciar os dados do estudante.
        viewModel = new ViewModelProvider(this).get(DetalhesEstudanteViewModel.class);

        // Adiciona o ViewModel como um observador de ciclo de vida.
        getLifecycle().addObserver(viewModel);

        // Obtém o ID do estudante passado pela Intent.
        estudanteId = getIntent().getIntExtra("ESTUDANTE_ID", -1);

        // Verifica se o ID é válido, e então seta no ViewModel.
        if (estudanteId != -1) {
            viewModel.setEstudanteId(estudanteId);
        }

        // Configura os botões e observa os dados.
        setupButtons();
        setupObservers();
    }

    // Configura os botões da interface.
    private void setupButtons() {
        // Botão Voltar
        binding.btnVoltar.setOnClickListener(v -> {
            setResult(RESULT_OK);  // Define o resultado como OK.
            finish();  // Finaliza a Activity e volta para a anterior.
        });

        // Botão Adicionar Nota
        binding.btnAdicionarNota.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdicionarNotaActivity.class);
            intent.putExtra("ESTUDANTE_ID", estudanteId);
            startActivityForResult(intent, 1);  // Inicia a Activity para adicionar nota.
        });

        // Botão Adicionar Frequência
        binding.btnAdicionarFrequencia.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdicionarFrequenciaActivity.class);
            intent.putExtra("ESTUDANTE_ID", estudanteId);
            startActivityForResult(intent, 2);  // Inicia a Activity para adicionar frequência.
        });

        // Botão Deletar Estudante
        binding.btnDeletarEstudante.setOnClickListener(v -> mostrarDialogoConfirmacao());
    }

    // Configura os observadores para exibir os dados do estudante.
    private void setupObservers() {
        // Observa as mudanças nos dados do estudante.
        viewModel.getEstudante().observe(this, estudante -> {
            if (estudante != null) {
                // Atualiza os dados na interface com as informações do estudante.
                binding.setEstudante(estudante);

                // Exibe a média do estudante.
                binding.textNotaFinal.setText(String.format("Nota Final: %.2f", estudante.calcularMedia()));

                // Exibe o percentual de presença do estudante.
                binding.textPresenca.setText(String.format("Presença: %.1f%%", estudante.calcularPercentualPresenca()));

                // Exibe a situação (aprovado, reprovado, etc.).
                binding.textSituacao.setText("Situação: " + estudante.verificarSituacao());
            }
        });
    }

    // Exibe um diálogo de confirmação antes de deletar o estudante.
    private void mostrarDialogoConfirmacao() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Deleção")
                .setMessage("Tem certeza que deseja deletar este estudante?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    // Chama o ViewModel para deletar o estudante.
                    viewModel.deletarEstudante(sucesso -> runOnUiThread(() -> {
                        if (sucesso) {
                            setResult(RESULT_OK);  // Define o resultado como OK.
                            finish();  // Finaliza a Activity e volta para a anterior.
                        } else {
                            // Exibe uma mensagem de erro se não foi possível deletar o estudante.
                            new AlertDialog.Builder(this)
                                    .setTitle("Erro")
                                    .setMessage("Não foi possível deletar o estudante.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }));
                })
                .setNegativeButton("Não", null)  // Se o usuário cancelar, nada acontece.
                .show();
    }

    // Método chamado quando uma Activity é resultado de um resultado (por exemplo, nota ou frequência).
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Se o resultado for OK, recarrega os dados do estudante.
        if (resultCode == RESULT_OK) {
            viewModel.recarregarEstudante();  // Recarrega os dados do estudante após adicionar nota ou frequência.
        }
    }
}

