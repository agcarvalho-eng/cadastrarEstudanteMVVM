package com.example.diarioestudantesmvvm.view;

import androidx.recyclerview.widget.RecyclerView;

import androidx.databinding.BindingAdapter;
import com.example.diarioestudantesmvvm.model.Estudante;
import com.example.diarioestudantesmvvm.view.EstudantesAdapter.OnItemClickListener;
import java.util.List;

// Classe utilitária que contém métodos de vinculação personalizados (Binding Adapters) para Data Binding
public class BindingAdapters {

    /**
     * Vincula a lista de estudantes ao RecyclerView
     */
    // Define um método de Binding Adapter chamado "app:items" para uso em layouts XML
    @BindingAdapter("app:items")
    public static void bindItems(RecyclerView recyclerView, List<Estudante> items) {
        // Se a lista de estudantes for nula, não faz nada
        if (items == null) return;

        // Obtém o adaptador atualmente associado ao RecyclerView
        EstudantesAdapter adapter = (EstudantesAdapter) recyclerView.getAdapter();

        // Se ainda não houver um adaptador, cria um novo com os itens e o define no RecyclerView
        if (adapter == null) {
            adapter = new EstudantesAdapter(items);
            recyclerView.setAdapter(adapter);
        } else {
            // Se já houver um adaptador, apenas atualiza os dados com os novos itens
            adapter.atualizarEstudantes(items);
        }
    }

    /**
     * Configura o listener de clique para os itens do RecyclerView
     */
    // Define um método de Binding Adapter chamado "app:itemClickListener" para uso em layouts XML
    @BindingAdapter("app:itemClickListener")
    public static void bindItemClickListener(RecyclerView recyclerView, OnItemClickListener listener) {
        // Obtém o adaptador associado ao RecyclerView
        EstudantesAdapter adapter = (EstudantesAdapter) recyclerView.getAdapter();

        // Se houver um adaptador, define o listener de clique para os itens
        if (adapter != null) {
            adapter.setOnItemClickListener(listener);
        }
    }
}
