package br.com.californiamobile.organizze.ui.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import br.com.californiamobile.organizze.R;
import br.com.californiamobile.organizze.model.Usuario;
import br.com.californiamobile.organizze.config.ConfiguracaoFirebase;
import br.com.californiamobile.organizze.helper.Base64Custom;

public class CadastroActivity extends AppCompatActivity {

    //Atributos
    private EditText txtNome;
    private EditText txtEmail;
    private EditText txtSenha;
    private Button btnCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //FindViewByIds
        txtNome = findViewById(R.id.cadastro_txtNome);
        txtEmail = findViewById(R.id.login_txtEmail);
        txtSenha = findViewById(R.id.cadastro_txtSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        //SetonClickListner do Botao Cadastrar
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textoNome = txtNome.getText().toString();
                String textoEmail = txtEmail.getText().toString();
                String textoSenha = txtSenha.getText().toString();

                if(!textoNome.isEmpty()){
                    if(!textoEmail.isEmpty()){
                        if(!textoSenha.isEmpty()){

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);
                            cadastrarUsuario();

                        }else{
                            Toast.makeText(CadastroActivity.this,
                                    "Por favor digite sua senha", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CadastroActivity.this,
                                "Digite seu Email", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this,
                            "Digite seu nome", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }

    private void cadastrarUsuario() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //Salvando o usuario no Firebase
                    String idUsuario = Base64Custom.codificarBase64Custom(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();

                    finish();

                    //Toast.makeText(CadastroActivity.this,
                      //     idUsuario, Toast.LENGTH_SHORT).show();
                }else{

                    //Tratamento de excesao ao criar uma conta
                    String excesao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excesao = "Digite uma senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excesao = "Digite um E-mail válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        excesao = "E-mail já cadastrado";
                    }
                    catch (Exception e) {
                        excesao = "Erro ca cadastrar o usuário " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this,
                            excesao, Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
