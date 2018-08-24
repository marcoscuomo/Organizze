package br.com.californiamobile.organizze.ui.activity;

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

public class DespesaActivity extends AppCompatActivity {

    //Aributos
    private TextInputEditText txtData, txtCategoria, txtDescricao;
    private EditText txtValor;
    private FloatingActionButton fabDespesa;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;
    //private Double despesaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa);

        //FindViewByIds
        txtValor     = findViewById(R.id.despesa_txtValor);
        txtData      = findViewById(R.id.despesa_txtData);
        txtCategoria = findViewById(R.id.despesa_txtCategoria);
        txtDescricao = findViewById(R.id.despesa_txtDescricao);
        fabDespesa   = findViewById(R.id.despesa_FAB);


        txtData.setText(DateCustom.daraAtual());

        recuperarDespesaTotal();


        fabDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarDespesa();
            }
        });


    }

    public void salvarDespesa() {

        if(validarCamposDespesa()){


            movimentacao = new Movimentacao();
            String data = txtData.getText().toString();
            Double valorRecuperado = Double.parseDouble(txtValor.getText().toString());
            movimentacao.setValor(valorRecuperado);
            movimentacao.setData( data );
            movimentacao.setCategoria(txtCategoria.getText().toString());
            movimentacao.setDescricao(txtDescricao.getText().toString());
            movimentacao.setTipo("d");

            Double despesaAtualizada = despesaTotal + valorRecuperado;

            atualizaDespesa(despesaAtualizada);

            movimentacao.salvar(data);

            finish();

        }

    }

    private void atualizaDespesa(Double despesaAtualizada) {

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64Custom(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("despesaTotal").setValue(despesaAtualizada);


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

    public void recuperarDespesaTotal(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64Custom(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
