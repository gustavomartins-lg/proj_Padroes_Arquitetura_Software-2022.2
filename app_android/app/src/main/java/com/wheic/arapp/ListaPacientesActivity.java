package com.wheic.arapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class ListaPacientesActivity extends AppCompatActivity implements RecyclerViewInterface {
    ArrayList<Paciente> pacientes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pacientes);

        RecyclerView recyclerViewDeviceList = findViewById(R.id.recyclerview_pacientes);
        mockDeviceList();
        recyclerViewDeviceList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDeviceList.setAdapter(new PacienteListAdapter(this, pacientes, this));
    }

    private void mockDeviceList() {
        pacientes = new ArrayList<>();
        pacientes.add(new Paciente("Maria", "em espera", "dor de barriga", 4));
        pacientes.add(new Paciente("Joao", "em atendimento", "facada no peito", 1));
        pacientes.add(new Paciente("Carlos", "em espera", "dor forte no peito", 2));
        pacientes.add(new Paciente("Carla", "Em atendimento", "covid", 3));
    }

    @Override
    public void onRecyclerViewItemClick(int position) {
        Intent intent = new Intent(ListaPacientesActivity.this, SinaisVitaisActivity.class);
        intent.putExtra("NOME_PACIENTE", pacientes.get(position).getNome());
        intent.putExtra("SITUACAO_PACIENTE", pacientes.get(position).getSituacao());
        intent.putExtra("QUEIXA_PACIENTE", pacientes.get(position).getQueixa());
        intent.putExtra("PRIORIDADE_PACIENTE", pacientes.get(position).getPrioridade());
        startActivity(intent);
    }
}