package com.example.cadastrarEstudanteMVVM.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.cadastrarEstudanteMVVM.databinding.ItemEstudanteBinding;
import com.example.cadastrarEstudanteMVVM.model.Estudante;

import java.util.List;

// Classe Adapter para o RecyclerView que exibe os estudantes.
public class EstudantesAdapter extends RecyclerView.Adapter<EstudantesAdapter.EstudanteViewHolder> {

    // Lista de estudantes que será exibida na RecyclerView.
    private List<Estudante> estudantes;

    // Listener para capturar os cliques nos itens da lista.
    private OnItemClickListener listener;

    /**
     * Interface para comunicação de cliques nos itens da lista.
     * Ao implementar essa interface, você pode capturar o clique em cada item.
     */
    public interface OnItemClickListener {
        void onItemClick(Estudante estudante);  // Método chamado quando um item é clicado.
    }

    /**
     * Construtor do adapter, inicializa a lista de estudantes.
     */
    public EstudantesAdapter(List<Estudante> estudantes) {
        this.estudantes = estudantes;
    }

    /**
     * Define o listener para capturar cliques nos itens da lista.
     * Este método permite que a Activity ou Fragment passe um listener de clique.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Atualiza os dados do adapter com uma nova lista de estudantes.
     * Chama notifyDataSetChanged() para notificar o RecyclerView sobre a mudança de dados.
     */
    public void atualizarEstudantes(List<Estudante> novosEstudantes) {
        this.estudantes = novosEstudantes;
        notifyDataSetChanged();  // Notifica a RecyclerView para atualizar os itens visíveis.
    }

    // Cria o ViewHolder para cada item na RecyclerView
    @NonNull
    @Override
    public EstudanteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item de estudante usando o DataBinding
        ItemEstudanteBinding binding = ItemEstudanteBinding.inflate(
                LayoutInflater.from(parent.getContext()),  // Obtém o LayoutInflater
                parent,  // O ViewGroup pai
                false   // Não anexa o layout imediatamente
        );
        return new EstudanteViewHolder(binding);  // Retorna o ViewHolder com o binding
    }

    // Vincula os dados ao ViewHolder
    @Override
    public void onBindViewHolder(@NonNull EstudanteViewHolder holder, int position) {
        // Obtém o estudante na posição atual da lista
        Estudante estudante = estudantes.get(position);

        // Vincula os dados do estudante ao layout usando DataBinding
        holder.binding.setEstudante(estudante);  // Define o estudante no binding
        holder.binding.executePendingBindings();  // Executa qualquer binding pendente

        // Configura o clique no item da RecyclerView
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // Chama o método de clique passando o estudante clicado
                listener.onItemClick(estudante);
            }
        });
    }

    // Retorna o número total de itens na lista de estudantes
    @Override
    public int getItemCount() {
        return estudantes != null ? estudantes.size() : 0;  // Retorna a quantidade de estudantes
    }

    /**
     * ViewHolder que mantém a referência ao ItemEstudanteBinding.
     * Isso permite acesso fácil aos elementos do layout do item na RecyclerView.
     */
    static class EstudanteViewHolder extends RecyclerView.ViewHolder {
        final ItemEstudanteBinding binding;

        EstudanteViewHolder(ItemEstudanteBinding binding) {
            super(binding.getRoot());  // Inicializa o ViewHolder com a raiz do binding
            this.binding = binding;  // Atribui o binding à variável local
        }
    }
}
