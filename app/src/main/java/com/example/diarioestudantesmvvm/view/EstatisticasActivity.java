package com.example.diarioestudantesmvvm.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.diarioestudantesmvvm.databinding.ActivityEstatisticasBinding;

public class EstatisticasActivity extends AppCompatActivity {
    private ActivityEstatisticasBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuração do Data Binding
        binding = ActivityEstatisticasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnVoltar.setOnClickListener(v -> {
            // Define o resultado para recarregar a lista
            setResult(RESULT_OK);
            finish(); // Fecha a activity e retorna para MainActivity
        });

        // Configura o botão de voltar (opcional)
        setupToolbar();
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Estatísticas");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Fecha a activity ao pressionar o botão de voltar
        return true;
    }
}
