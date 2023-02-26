package com.wheic.arapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PacienteListAdapter extends RecyclerView.Adapter<PacienteListAdapter.PacienteViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    List<Paciente> pacientes;
    Context context;

    public PacienteListAdapter(RecyclerViewInterface recyclerViewInterface, List<Paciente> pacientes, Context context) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.pacientes = pacientes;
        this.context = context;
    }

    @NonNull
    @Override
    public PacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.paciente_view, parent, false);
        return new PacienteViewHolder(view, recyclerViewInterface);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull PacienteViewHolder holder, int position) {
        Paciente paciente = pacientes.get(position);
        holder.nomePaciente.setText(paciente.getNome());
        holder.situacaoPaciente.setText(paciente.getSituacao());
        holder.queixaPaciente.setText(paciente.getQueixa());

        switch (paciente.getPrioridade()){
            case 1: //prioridade vermelha
                holder.prioridadePaciente.setBackground(context.getResources().getDrawable(R.drawable.round_circle_red));
                break;
            case 2://prioridade amarela
                holder.prioridadePaciente.setBackground(context.getResources().getDrawable(R.drawable.round_circle_yellow));
                break;
            case 3://prioridade azul
                holder.prioridadePaciente.setBackground(context.getResources().getDrawable(R.drawable.round_circle_blue));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return pacientes.size();
    }

    public static class PacienteViewHolder extends RecyclerView.ViewHolder {

        public TextView nomePaciente, situacaoPaciente, queixaPaciente;
        public ImageView prioridadePaciente;

        public PacienteViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            nomePaciente = itemView.findViewById(R.id.tv_paciente_name);
            situacaoPaciente = itemView.findViewById(R.id.tv_paciente_situacao);
            queixaPaciente = itemView.findViewById(R.id.tv_paciente_queixa);
            prioridadePaciente = itemView.findViewById(R.id.iv_paciente_prioridade);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface!= null){
                        int position = getBindingAdapterPosition();

                        if(position!= RecyclerView.NO_POSITION){
                            recyclerViewInterface.onRecyclerViewItemClick(position);
                        }
                    }
                }
            });
        }


    }
}
