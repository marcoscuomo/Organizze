package br.com.californiamobile.organizze.ui.activity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import br.com.californiamobile.organizze.R;
import br.com.californiamobile.organizze.model.Usuario;
import br.com.californiamobile.organizze.config.ConfiguracaoFirebase;

public class LoginActivity extends AppCompatActivity {

    //Atributos
    private EditText txtEmail;
    private EditText txtSenha;
    private Button btnLogin;
    private Usuario usuario;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //FindViewByIds
        txtEmail = findViewById(R.id.login_txtEmail);
        txtSenha = findViewById(R.id.login_txtSenha);
        btnLogin = findViewById(R.id.btnEntrar);


        //SetOnClickLitner do botao
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString();
                String senha = txtSenha.getText().toString();

                if(!email.isEmpty()){
                    if(!senha.isEmpty()){
                        usuario = new Usuario();
                        usuario.setEmail(email);
                        usuario.setSenha(senha);
                        
                        validarSenha();

                    }else{
                        Toast.makeText(LoginActivity.this, "Digite sua senha", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Digite o E-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void validarSenha() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    abrirTelaPrincipal();

                    //Toast.makeText(LoginActivity.this, "Sucesso ao se logar", Toast.LENGTH_SHORT).show();
                }else{

                    //Tratamento de excessoes ao se logar
                    String excessao = "";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        excessao = "Usuario não cadastrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excessao = "E-mail e Senha não correspondem";
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, excessao, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
        finish();

    }
}
