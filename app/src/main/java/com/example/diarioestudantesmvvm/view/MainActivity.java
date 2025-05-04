package com.example.diarioestudantesmvvm.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.example.diarioestudantesmvvm.R;
import com.example.diarioestudantesmvvm.databinding.ActivityMainBinding;
import com.example.diarioestudantesmvvm.model.Estudante;
import com.example.diarioestudantesmvvm.util.EstudantesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Activity principal que exibe a lista de estudantes
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private EstudantesViewModel estudantesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        estudantesViewModel = new ViewModelProvider(this).get(EstudantesViewModel.class);

        // Registrar o observador do ciclo de vida
        getLifecycle().addObserver(estudantesViewModel);

        binding.setViewModel(estudantesViewModel);
        binding.setActivity(this);
        binding.setLifecycleOwner(this);

        setupRecyclerView();

        setupFloatingActionButton();

        // Observar as mudanças na lista de estudantes
        estudantesViewModel.getEstudantes().observe(this, estudantes -> {
            // A atualização da lista é feita automaticamente pelo BindingAdapter
        });
    }

    private void setupRecyclerView() {
        EstudantesAdapter adapter = new EstudantesAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(this::onEstudanteClicado);
        binding.recyclerView.setAdapter(adapter);
    }

    public void onEstudanteClicado(Estudante estudante) {
        if (estudante != null) {
            Intent intent = new Intent(this, DetalhesEstudanteActivity.class);
            intent.putExtra("ESTUDANTE_ID", estudante.getId());
            startActivityForResult(intent, 1);
        }
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab = binding.fabEstatisticas;
        fab.setOnClickListener(view -> {
            // Navega para a EstatisticasActivity
            Intent intent = new Intent(MainActivity.this, EstatisticasActivity.class);
            startActivityForResult(intent, 2);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica se é o retorno da EstatisticasActivity (requestCode == 2)
        // ou DetalhesEstudanteActivity (requestCode == 1)
        if (resultCode == RESULT_OK) {
            estudantesViewModel.recarregarEstudantes();
        }
    }
}