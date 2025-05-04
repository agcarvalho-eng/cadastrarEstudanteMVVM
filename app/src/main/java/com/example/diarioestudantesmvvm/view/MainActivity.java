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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

/**
 * Activity principal que exibe a lista de estudantes
 */
public class MainActivity extends AppCompatActivity {

    // Referência ao binding gerado automaticamente para o layout activity_main.xml
    private ActivityMainBinding binding;

    // ViewModel que gerencia os dados dos estudantes
    private EstudantesViewModel estudantesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Associa o layout XML à Activity usando data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Obtém uma instância da ViewModel associada a esta Activity
        estudantesViewModel = new ViewModelProvider(this).get(EstudantesViewModel.class);

        // Adiciona a ViewModel como observadora do ciclo de vida da Activity
        getLifecycle().addObserver(estudantesViewModel);

        // Define a ViewModel para ser usada no binding do layout
        binding.setViewModel(estudantesViewModel);

        // Define a Activity atual no binding (útil para chamadas a métodos no layout)
        binding.setActivity(this);

        // Define esta Activity como LifecycleOwner para que o data binding observe mudanças automaticamente
        binding.setLifecycleOwner(this);

        // Configura o RecyclerView com o adaptador e listener
        setupRecyclerView();

        // Configura o botão flutuante (FAB) para navegar para a tela de estatísticas
        setupBotaoFlutuante();

        // Observa mudanças na lista de estudantes (a atualização da UI é feita pelo BindingAdapter)
        estudantesViewModel.getEstudantes().observe(this, estudantes -> {
            // O adaptador é atualizado automaticamente via BindingAdapter, então nada precisa ser feito aqui
        });
    }

    // Configura o RecyclerView principal com um adaptador vazio e um listener de clique
    private void setupRecyclerView() {
        // Cria um adaptador vazio inicialmente
        EstudantesAdapter adapter = new EstudantesAdapter(new ArrayList<>());

        // Define o listener para tratar cliques em estudantes
        adapter.setOnItemClickListener(estudante -> onEstudanteClicado(estudante));

        // Associa o adaptador ao RecyclerView
        binding.recyclerView.setAdapter(adapter);
    }

    // Trata o evento de clique em um estudante
    public void onEstudanteClicado(Estudante estudante) {
        // Se o estudante não for nulo, inicia a tela de detalhes passando o ID do estudante
        if (estudante != null) {
            Intent intent = new Intent(this, DetalhesEstudanteActivity.class);
            intent.putExtra("ESTUDANTE_ID", estudante.getId());
            startActivityForResult(intent, 1); // Código 1 para retorno da DetalhesEstudanteActivity
        }
    }

    // Configura o botão flutuante que navega para a tela de estatísticas
    private void setupBotaoFlutuante() {
        // Obtém referência ao botão flutuante via binding
        FloatingActionButton fab = binding.fabEstatisticas;

        // Define a ação ao clicar no botão: navegar para EstatisticasActivity
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EstatisticasActivity.class);
            /**
             * Inicia a Activity informando qual foi a Activity que mandou o retorno.
             * O número "2" informa que foi a EstatisticasActivity
             */
            startActivityForResult(intent, 2);
        });
    }

    // Trata o retorno das Activities chamadas com startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Se o resultado for OK, recarrega a lista de estudantes no ViewModel
        // Isso pode ser necessário se dados foram alterados nas outras telas
        if (resultCode == RESULT_OK) {
            estudantesViewModel.recarregarEstudantes();
        }
    }
}
