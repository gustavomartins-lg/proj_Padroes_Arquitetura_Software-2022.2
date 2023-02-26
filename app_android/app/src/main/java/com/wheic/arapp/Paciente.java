package com.wheic.arapp;

public class Paciente {

    private String nome;
    private String situacao;
    private String queixa;
    private int prioridade;

    public Paciente(String nome, String situacao, String queixa, int prioridade) {
        this.nome = nome;
        this.situacao = situacao;
        this.queixa = queixa;
        this.prioridade = prioridade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getQueixa() {
        return queixa;
    }

    public void setQueixa(String queixa) {
        this.queixa = queixa;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }
}
