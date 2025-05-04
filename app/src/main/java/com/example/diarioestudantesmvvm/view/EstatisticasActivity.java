package com.example.diarioestudantesmvvm.view;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.diarioestudantesmvvm.databinding.ActivityEstatisticasBinding;
import com.example.diarioestudantesmvvm.util.EstatisticasViewModel;

import java.util.ArrayList;

public class EstatisticasActivity extends AppCompatActivity {
    private ActivityEstatisticasBinding binding;
    private EstatisticasViewModel viewModel;
    private EstudantesAdapter aprovadosAdapter;
    private EstudantesAdapter reprovadosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEstatisticasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(EstatisticasViewModel.class);
        getLifecycle().addObserver(viewModel);

        setupRecyclerViews();
        setupObservers();
        setupToolbar();

        binding.btnVoltar.setOnClickListener(v -> {
            // Define o resultado para recarregar a lista
            setResult(RESULT_OK);
            finish(); // Fecha a activity e retorna para MainActivity
        });
    }

    // Inicializa e configura os RecyclerViews para exibirem as listas de aprovados e reprovados
    private void setupRecyclerViews() {
        // Cria o adaptador para a lista de estudantes aprovados com uma lista inicial vazia
        aprovadosAdapter = new EstudantesAdapter(new ArrayList<>());
        // Cria o adaptador para a lista de estudantes reprovados com uma lista inicial vazia
        reprovadosAdapter = new EstudantesAdapter(new ArrayList<>());

        // Define o layout em forma de lista vertical para o RecyclerView de aprovados
        binding.recyclerViewAprovados.setLayoutManager(new LinearLayoutManager(this));
        // Associa o adaptador de aprovados ao RecyclerView correspondente
        binding.recyclerViewAprovados.setAdapter(aprovadosAdapter);

        // Define o layout em forma de lista vertical para o RecyclerView de reprovados
        binding.recyclerViewReprovados.setLayoutManager(new LinearLayoutManager(this));
        // Associa o adaptador de reprovados ao RecyclerView correspondente
        binding.recyclerViewReprovados.setAdapter(reprovadosAdapter);
    }

    // Observa dados do ViewModel e atualiza a interface automaticamente quando os dados mudam
    private void setupObservers() {
        // Observa a média geral das notas e atualiza o texto correspondente
        viewModel.getMediaGeral().observe(this, media -> {
            binding.textMediaGeral.setText(String.format("Média geral: %.2f", media));
        });

        // Observa o nome do aluno com a maior nota e atualiza o texto correspondente
        viewModel.getAlunoMaiorNota().observe(this, nome -> {
            binding.textMaiorNota.setText("Maior nota: " + nome);
        });

        // Observa o nome do aluno com a menor nota e atualiza o texto correspondente
        viewModel.getAlunoMenorNota().observe(this, nome -> {
            binding.textMenorNota.setText("Menor nota: " + nome);
        });

        // Observa a média de idade dos estudantes e atualiza o texto correspondente
        viewModel.getMediaIdade().observe(this, media -> {
            binding.textMediaIdade.setText(String.format("Média de idade: %.1f anos", media));
        });

        // Observa a lista de aprovados e atualiza a interface com base no conteúdo
        viewModel.getAprovados().observe(this, aprovados -> {
            // Se a lista não for nula nem vazia, exibe os componentes e atualiza o adaptador
            if (aprovados != null && !aprovados.isEmpty()) {
                binding.textViewAprovados.setVisibility(View.VISIBLE);
                binding.recyclerViewAprovados.setVisibility(View.VISIBLE);
                aprovadosAdapter.atualizarEstudantes(aprovados);
            } else {
                // Caso contrário, oculta os componentes relacionados aos aprovados
                binding.textViewAprovados.setVisibility(View.GONE);
                binding.recyclerViewAprovados.setVisibility(View.GONE);
            }
        });

        // Observa a lista de reprovados e atualiza a interface com base no conteúdo
        viewModel.getReprovados().observe(this, reprovados -> {
            // Se a lista não for nula nem vazia, exibe os componentes e atualiza o adaptador
            if (reprovados != null && !reprovados.isEmpty()) {
                binding.textViewReprovados.setVisibility(View.VISIBLE);
                binding.recyclerViewReprovados.setVisibility(View.VISIBLE);
                reprovadosAdapter.atualizarEstudantes(reprovados);
            } else {
                // Caso contrário, oculta os componentes relacionados aos reprovados
                binding.textViewReprovados.setVisibility(View.GONE);
                binding.recyclerViewReprovados.setVisibility(View.GONE);
            }
        });
    }

    // Configura a toolbar com botão de voltar e título da tela
    private void setupToolbar() {
        // Verifica se há uma ActionBar disponível
        if (getSupportActionBar() != null) {
            // Ativa o botão de "voltar" na ActionBar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Define o título da ActionBar como "Estatísticas"
            getSupportActionBar().setTitle("Estatísticas");
        }
    }

    // Trata o clique no botão "voltar" da toolbar
    @Override
    public boolean onSupportNavigateUp() {
        // Encerra a activity atual
        finish();
        // Retorna true indicando que o evento foi tratado
        return true;
    }

}
