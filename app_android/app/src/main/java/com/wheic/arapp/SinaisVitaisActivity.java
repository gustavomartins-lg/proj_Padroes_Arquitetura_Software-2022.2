package com.wheic.arapp;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import io.grpc.examples.iotservice.LedStatus;
import io.grpc.examples.iotservice.ListaLedStatus;
import io.grpc.examples.iotservice.Parametros;
import io.grpc.examples.iotservice.SensorServiceGrpc;
import io.grpc.stub.MetadataUtils;

public class SinaisVitaisActivity extends AppCompatActivity {
    private static final String HOST = "34.27.5.131";
    private static final int PORT = 50051;
    private TextView nomePaciente, situacaoPaciente, queixaPaciente;
    private TextView dadoVital1, dadoVital2, dadoVital3;
    private ImageView prioridadePaciente;
    private ImageButton arButton;
    private Button remedio1Btn, remedio2Btn;
    private int estadoAtualRemedio1, estadoAtualRemedio2;

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
                Intent intent = new Intent((Context) SinaisVitaisActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        dadoVital1 = (TextView) findViewById(R.id.dadoVital1TextView);
        dadoVital2 = (TextView) findViewById(R.id.dadoVital2TextView);
        dadoVital3 = (TextView) findViewById(R.id.dadoVital3TextView);

        consultarDados("sensor de temperatura");
        consultarDados("sensor de umidade");
        consultarDados("sensor de luminosidade");

        remedio1Btn = (Button) findViewById(R.id.remedio1Button);
        remedio2Btn = (Button) findViewById(R.id.remedio2Button);

        consultarEstadoMedicacao("remedio1");
        consultarEstadoMedicacao("remedio2");

        remedio1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aplicarMedicamento("remedio1", novoEstado(estadoAtualRemedio1));
            }
        });
        remedio2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aplicarMedicamento("remedio2", novoEstado(estadoAtualRemedio2));
            }
        });
    }

    private void consultarEstadoMedicacao(String medicamento) {
        new GrpcTaskConsultaEstadMedicacao(this).execute("ambulancia", medicamento);
    }

    private int novoEstado(int estadoAtual){
        //se o estado atual for igual a 0, então o novo estado é 1
        if(estadoAtual == 0){
            return 1;
        }
        //se o estado atual for igual a 1, então o novo estado é 0
        else{
            return 0;
        }
    }

    private void alterarEstadoMedicacao(String resultado) {
        String[] dados = resultado.split(":");

        switch (dados[0]) {
            case "remedio1":
                if(dados[1].equals("1")) {
                    remedio1Btn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.vermelha));
                } else{
                    remedio1Btn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.verde));
                }
                estadoAtualRemedio1 = Integer.parseInt(dados[1]);
                break;
            case "remedio2":
                if(dados[1].equals("1")) {
                    remedio2Btn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.vermelha));
                }else{
                    remedio2Btn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.verde));
                }
                estadoAtualRemedio2 = Integer.parseInt(dados[1]);
                break;
            default:
                break;
        }

    }

    private void aplicarMedicamento(String medicamento, int estado) {
        new GrpcTaskAplicaMedicacao(this).execute("ambulancia", medicamento, Integer.toString(estado));
    }

    private void consultarDados(String nomedispositivo) {
        new GrpcTaskConsultaSensor(this).execute("ambulancia", nomedispositivo);
    }

    private void preencherMonitorDadosVitais(String resultado) {
        String[] dados = resultado.split(":");

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
                stub = MetadataUtils.attachHeaders(stub, metadata);
                //stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
                Dado resposta = stub.consultarUltimaLeituraSensor(parametros);

                return resposta.getNomeDispositivo() + ":" + resposta.getValor();

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

    private static class GrpcTaskAplicaMedicacao extends AsyncTask<String, Void, String> {
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;


        private GrpcTaskAplicaMedicacao(Activity activity) {
            this.activityReference = new WeakReference<Activity>(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            String localizacao = params[0];
            String nomeDispositivo = params[1];
            int estado = Integer.parseInt(params[2]);

            Metadata.Key<String> usuarioKey = Metadata.Key.of("username", ASCII_STRING_MARSHALLER);
            Metadata.Key<String> senhaKey = Metadata.Key.of("password", ASCII_STRING_MARSHALLER);
            Metadata metadata = new Metadata();

            // Adicionando um valor para a chave criada na metadata
            metadata.put(usuarioKey, "ariel");
            metadata.put(senhaKey, "senha123456");

            try {
                channel = ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext().build();
                SensorServiceGrpc.SensorServiceBlockingStub stub = SensorServiceGrpc.newBlockingStub(channel);
                LedStatus ledStatus = LedStatus.newBuilder().setLocalizacao(localizacao).setNomeDispositivo(nomeDispositivo).setEstado(estado).build();
                stub = MetadataUtils.attachHeaders(stub, metadata);
                //stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
                LedStatus resposta = stub.acionarLed(ledStatus);

                return resposta.getNomeDispositivo() + ":" + resposta.getEstado();

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
                ((SinaisVitaisActivity) activity).alterarEstadoMedicacao(result);
            }
        }
    }

    private static class GrpcTaskConsultaEstadMedicacao extends AsyncTask<String, Void, String> {
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;


        private GrpcTaskConsultaEstadMedicacao(Activity activity) {
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
                LedStatus ledStatus = LedStatus.newBuilder().setLocalizacao(localizacao).setNomeDispositivo(nomeDispositivo).build();
                stub = MetadataUtils.attachHeaders(stub, metadata);
                //stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
                ListaLedStatus resposta = stub.listarLeds(ledStatus);


                return resposta.getStatus(0).getNomeDispositivo()+ ":" + resposta.getStatus(0).getEstado();

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
                ((SinaisVitaisActivity) activity).alterarEstadoMedicacao(result);
            }
        }
    }
}