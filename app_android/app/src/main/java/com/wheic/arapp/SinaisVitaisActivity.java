package com.wheic.arapp;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.examples.iotservice.Dado;
import io.grpc.examples.iotservice.Parametros;
import io.grpc.examples.iotservice.SensorServiceGrpc;
import io.grpc.stub.MetadataUtils;

public class SinaisVitaisActivity extends AppCompatActivity {
    private static final String HOST = "146.148.42.190";
    private static final int PORT = 50051;
    public TextView nomePaciente, situacaoPaciente, queixaPaciente;
    private TextView dadoVital1, dadoVital2, dadoVital3;
    public ImageView prioridadePaciente;
    public ImageButton arButton;
    private Paciente paciente;

    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinais_vitais);
        Intent intent = getIntent();
        paciente = new Paciente();

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

        dadoVital1 = (TextView) findViewById(R.id.dadoVital1TextView);
        dadoVital2 = (TextView) findViewById(R.id.dadoVital2TextView);
        dadoVital3 = (TextView) findViewById(R.id.dadoVital3TextView);

        consultarDados("sensor de temperatura");
        consultarDados("sensor de umidade");
        consultarDados("sensor de luminosidade");
    }

    private void consultarDados(String nomedispositivo) {
        new GrpcTaskConsultaSensor(this).execute("ambulancia", nomedispositivo);
    }

    private void preencherMonitorDadosVitais(String resultado) {
        String[] dados = resultado.split("|");

        switch (dados[0].toLowerCase()) {
            case "sensor de temperatura":
                dadoVital1.setText(dados[1]);
                break;
            case "sensor de umidade":
                dadoVital2.setText(dados[1]);
                break;
            case "sensor de luminosidade":
                dadoVital3.setText(dados[1]);
                break;
            default:
                break;
        }
    }

    private static class GrpcTaskConsultaSensor extends AsyncTask<String, Void, String> {
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;


        private GrpcTaskConsultaSensor(Activity activity) {
            this.activityReference = new WeakReference<Activity>(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            String localizacao = params[0];
            String nomeDispositivo = params[1];

            Metadata.Key<String> usuarioKey = Metadata.Key.of("username", ASCII_STRING_MARSHALLER);
            Metadata.Key<String> senhaKey = Metadata.Key.of("password", ASCII_STRING_MARSHALLER);
            Metadata metadata = new Metadata();

            // Adicionando um valor para a chave criada na metadata
            metadata.put(usuarioKey, "ariel");
            metadata.put(senhaKey, "senha123456");

            try {
                channel = ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext().build();
                SensorServiceGrpc.SensorServiceBlockingStub stub = SensorServiceGrpc.newBlockingStub(channel);
                Parametros parametros = Parametros.newBuilder().setLocalizacao(localizacao).setNomeDispositivo(nomeDispositivo).build();
                //stub = MetadataUtils.attachHeaders(stub, new Metadata());
                stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
                Dado resposta = stub.consultarUltimaLeituraSensor(parametros);

                return resposta.getNomeDispositivo() + "|" + resposta.getValor();

            } catch (Exception e){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return String.format("Failed... : %n%s", sw);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Activity activity = activityReference.get();
            if (activity == null) {
                return;
            }
            if (activity instanceof SinaisVitaisActivity) {
                ((SinaisVitaisActivity) activity).preencherMonitorDadosVitais(result);
            }
        }
    }
}