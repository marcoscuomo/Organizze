package br.com.californiamobile.organizze.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import br.com.californiamobile.organizze.config.ConfiguracaoFirebase;
import br.com.californiamobile.organizze.helper.Base64Custom;
import br.com.californiamobile.organizze.helper.DateCustom;

public class Movimentacao {

    private String data, categoria, descricao, tipo, key;
    private Double valor;

    public Movimentacao() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public void salvar(String dataEscolhida) {

        String mesAno = DateCustom.mesAnoEscolhido(dataEscolhida);

        FirebaseAuth autenticacao =
                ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario =
                Base64Custom.codificarBase64Custom(autenticacao
                        .getCurrentUser().getEmail());

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("movimentacao")
                .child(idUsuario)
                .child(mesAno)
                .push()
                .setValue(this);
    }
}
