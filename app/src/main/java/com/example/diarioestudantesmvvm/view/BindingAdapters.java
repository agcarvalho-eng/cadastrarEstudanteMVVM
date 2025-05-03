package com.example.diarioestudantesmvvm.view;

import androidx.recyclerview.widget.RecyclerView;

import androidx.databinding.BindingAdapter;
import com.example.diarioestudantesmvvm.model.Estudante;
import com.example.diarioestudantesmvvm.view.EstudantesAdapter.OnItemClickListener;
import java.util.List;

/**
 * Classe com adaptadores customizados para Data Binding
 * Contém os métodos necessários para vincular dados ao RecyclerView
 */
public class BindingAdapters {

    /**
     * Vincula a lista de estudantes ao RecyclerView
     * @param recyclerView RecyclerView a ser populado
     * @param items Lista de estudantes para exibição
     */
    @BindingAdapter("app:items")
    public static void bindItems(RecyclerView recyclerView, List<Estudante> items) {
        if (items == null) return;

        EstudantesAdapter adapter = (EstudantesAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new EstudantesAdapter(items);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.atualizarEstudantes(items);
        }
    }

    /**
     * Configura o listener de clique para os itens do RecyclerView
     * @param recyclerView RecyclerView que receberá o listener
     * @param listener Implementação do listener de clique
     */
    @BindingAdapter("app:itemClickListener")
    public static void bindItemClickListener(RecyclerView recyclerView, OnItemClickListener listener) {
        EstudantesAdapter adapter = (EstudantesAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setOnItemClickListener(listener);
        }
    }
}
