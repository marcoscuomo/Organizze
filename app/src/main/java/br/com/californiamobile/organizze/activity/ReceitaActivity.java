package br.com.californiamobile.organizze.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.californiamobile.organizze.R;
import br.com.californiamobile.organizze.model.Movimentacao;
import br.com.californiamobile.organizze.model.Usuario;
import br.com.californiamobile.organizze.config.ConfiguracaoFirebase;
import br.com.californiamobile.organizze.helper.Base64Custom;
import br.com.californiamobile.organizze.helper.DateCustom;

public class ReceitaActivity extends AppCompatActivity {

    //Atributos
    private TextInputEditText txtData, txtCategoria, txtDescricao;
    private EditText txtValor;
    private FloatingActionButton fabReceita;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;
    private Double getReceitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);

        //FindViewByids
        txtValor     = findViewById(R.id.receita_txtValor);
        txtCategoria = findViewById(R.id.receita_txtCategoria);
        txtDescricao = findViewById(R.id.receita_txtDescricao);
        txtData      = findViewById(R.id.receita_txtData);
        fabReceita   = findViewById(R.id.receita_FAB);



        txtData.setText(DateCustom.daraAtual());

        recuperarReceitaTotal();

        fabReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarReceita();
            }
        });



    }

    private void salvarReceita() {

        if(validarCamposDespesa()){

            Movimentacao movimentacao = new Movimentacao();

            String data = txtData.getText().toString();

            Double valorRecuperado = Double.parseDouble(txtValor.getText().toString());
            movimentacao.setValor(valorRecuperado);

            movimentacao.setData( data );
            movimentacao.setCategoria(txtCategoria.getText().toString());
            movimentacao.setDescricao(txtDescricao.getText().toString());
            movimentacao.setTipo("r");

            Double receitaAtualizada = receitaTotal + valorRecuperado;

            atualizaReceita(receitaAtualizada);

            movimentacao.salvar(data);

            finish();
        }


    }

    private void atualizaReceita(Double receitaAtualizada) {

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64Custom(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receitaAtualizada);

    }


    private void recuperarReceitaTotal() {

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64Custom(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean validarCamposDespesa() {

        String textoValor = txtValor.getText().toString();
        String textoData = txtData.getText().toString();
        String textoCategoria = txtCategoria.getText().toString();
        String textoDescricricao = txtDescricao.getText().toString();

        if(!textoValor.isEmpty()){
            if(!textoData.isEmpty()){
                if(!textoCategoria.isEmpty()){
                    if(!textoDescricricao.isEmpty()){
                        return true;
                    }else{
                        Toast.makeText(this, "Preencha a descrição da despesa",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(this, "Preencha a categoria da despesa",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(this, "Preencha a data da despesa",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(this, "Preencha o valor da despesa",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
