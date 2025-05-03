package com.example.diarioestudantesmvvm.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.example.diarioestudantesmvvm.R;
import com.example.diarioestudantesmvvm.databinding.ActivityMainBinding;
import com.example.diarioestudantesmvvm.model.Estudante;
import com.example.diarioestudantesmvvm.util.EstudantesViewModel;

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
            startActivity(intent);
        }
    }
}