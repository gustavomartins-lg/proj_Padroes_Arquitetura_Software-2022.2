package com.wheic.arapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SinaisVitaisActivity extends AppCompatActivity {

    public TextView nomePaciente, situacaoPaciente, queixaPaciente;
    public ImageView prioridadePaciente;
    public ImageButton arButton;

    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinais_vitais);
        Intent intent = getIntent();

        nomePaciente = (TextView) findViewById(R.id.tv_paciente_name_cardview);
        nomePaciente.setText(intent.getStringExtra("NOME_PACIENTE"));

        situacaoPaciente = (TextView) findViewById(R.id.tv_paciente_situacao_cardview);
        situacaoPaciente.setText(intent.getStringExtra("SITUACAO_PACIENTE"));

        queixaPaciente = (TextView) findViewById(R.id.tv_paciente_queixa_cardview);
        queixaPaciente.setText(intent.getStringExtra("QUEIXA_PACIENTE"));


        prioridadePaciente = (ImageView) findViewById(R.id.iv_paciente_prioridade_cardview);
        switch (intent.getIntExtra("PRIORIDADE_PACIENTE", 0)){
            case 1: //prioridade vermelha
                prioridadePaciente.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_circle_red));
                break;
            case 2://prioridade amarela
                prioridadePaciente.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_circle_yellow));
                break;
            case 3://prioridade azul
                prioridadePaciente.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.round_circle_blue));
                break;
            default:
                break;
        }

        arButton = (ImageButton) findViewById(R.id.ARButton);
        arButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SinaisVitaisActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    //private void
}