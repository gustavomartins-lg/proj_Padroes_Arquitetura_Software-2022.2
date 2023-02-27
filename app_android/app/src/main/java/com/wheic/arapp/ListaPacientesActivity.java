package com.wheic.arapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;
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
        pacientes.add(new Paciente("Maria Carvalho Freitas", "Em espera", "Dor de barriga", 4));
        pacientes.add(new Paciente("João Paulo Souza", "Transferência de hospital", "Facada no peito", 1));
        pacientes.add(new Paciente("Carlos Andrade Silva", "Em espera", "Dor forte no peito", 2));
        pacientes.add(new Paciente("Carla de Jesus Costa", "Em atendimento", "Covid", 3));
        pacientes.add(new Paciente("Matheus Freitas Lima", "UTI", "Acidente de transito", 1));
        pacientes.add(new Paciente("Maria Aparecida Lima", "UTI", "Acidente de transito", 1));
        pacientes.add(new Paciente("Caio Henrique Teles", "Em espera", "Fratura na mão", 2));
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