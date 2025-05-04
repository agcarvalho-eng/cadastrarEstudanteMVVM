package com.example.diarioestudantesmvvm.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diarioestudantesmvvm.databinding.ItemEstudanteBinding;
import com.example.diarioestudantesmvvm.model.Estudante;
import java.util.List;

/**
 * Adapter para o RecyclerView que exibe a lista de estudantes
 */
public class EstudantesAdapter extends RecyclerView.Adapter<EstudantesAdapter.EstudanteViewHolder> {

    private List<Estudante> estudantes;
    private OnItemClickListener listener;

    /**
     * Interface para comunicação de cliques nos itens
     */
    public interface OnItemClickListener {
        void onItemClick(Estudante estudante);
    }

    /**
     * Construtor do adapter
     */
    public EstudantesAdapter(List<Estudante> estudantes) {
        this.estudantes = estudantes;
    }

    /**
     * Define o listener para cliques nos itens
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Atualiza os dados do adapter
     */
    public void atualizarEstudantes(List<Estudante> novosEstudantes) {
        this.estudantes = novosEstudantes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EstudanteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout usando Data Binding
        ItemEstudanteBinding binding = ItemEstudanteBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new EstudanteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EstudanteViewHolder holder, int position) {
        // Obtém o estudante na posição atual
        Estudante estudante = estudantes.get(position);

        // Vincula o estudante ao layout
        holder.binding.setEstudante(estudante);

        // Configura o clique
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(estudante);
            }
        });
    }

    @Override
    public int getItemCount() {
        return estudantes != null ? estudantes.size() : 0;
    }

    /**
     * ViewHolder que mantém a referência ao binding
     */
    static class EstudanteViewHolder extends RecyclerView.ViewHolder {
        final ItemEstudanteBinding binding;

        EstudanteViewHolder(ItemEstudanteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}