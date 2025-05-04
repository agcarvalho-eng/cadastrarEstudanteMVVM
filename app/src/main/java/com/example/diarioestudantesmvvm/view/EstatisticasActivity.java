package com.example.diarioestudantesmvvm.view;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.diarioestudantesmvvm.databinding.ActivityEstatisticasBinding;
import com.example.diarioestudantesmvvm.util.EstatisticasViewModel;
import com.example.diarioestudantesmvvm.util.EstudantesRepository;

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

    private void setupRecyclerViews() {
        aprovadosAdapter = new EstudantesAdapter(new ArrayList<>());
        reprovadosAdapter = new EstudantesAdapter(new ArrayList<>());

        binding.recyclerViewAprovados.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewAprovados.setAdapter(aprovadosAdapter);

        binding.recyclerViewReprovados.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewReprovados.setAdapter(reprovadosAdapter);
    }

    private void setupObservers() {
        viewModel.getMediaGeral().observe(this, media -> {
            binding.textMediaGeral.setText(String.format("Média geral: %.2f", media));
        });

        viewModel.getAlunoMaiorNota().observe(this, nome -> {
            binding.textMaiorNota.setText("Maior nota: " + nome);
        });

        viewModel.getAlunoMenorNota().observe(this, nome -> {
            binding.textMenorNota.setText("Menor nota: " + nome);
        });

        viewModel.getMediaIdade().observe(this, media -> {
            binding.textMediaIdade.setText(String.format("Média de idade: %.1f anos", media));
        });

        viewModel.getAprovados().observe(this, aprovados -> {
            if (aprovados != null && !aprovados.isEmpty()) {
                binding.textViewAprovadosLabel.setVisibility(View.VISIBLE);
                binding.recyclerViewAprovados.setVisibility(View.VISIBLE);
                aprovadosAdapter.atualizarEstudantes(aprovados);
            } else {
                binding.textViewAprovadosLabel.setVisibility(View.GONE);
                binding.recyclerViewAprovados.setVisibility(View.GONE);
            }
        });

        viewModel.getReprovados().observe(this, reprovados -> {
            if (reprovados != null && !reprovados.isEmpty()) {
                binding.textViewReprovadosLabel.setVisibility(View.VISIBLE);
                binding.recyclerViewReprovados.setVisibility(View.VISIBLE);
                reprovadosAdapter.atualizarEstudantes(reprovados);
            } else {
                binding.textViewReprovadosLabel.setVisibility(View.GONE);
                binding.recyclerViewReprovados.setVisibility(View.GONE);
            }
        });
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Estatísticas");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
