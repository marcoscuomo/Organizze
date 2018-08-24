package br.com.californiamobile.organizze.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

import br.com.californiamobile.organizze.R;
import br.com.californiamobile.organizze.config.ConfiguracaoFirebase;

public class MainActivity extends IntroActivity {

    //Atributos
    private FirebaseAuth autenticacao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        setButtonBackVisible(false);
        setButtonNextVisible(false);

        /*
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro1)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro2)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro3)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro4)
                .build()
        );
        */

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build()
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificaUsuarioLogado();
    }

    public void btnCadastrar(View view){
        Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
        startActivity(intent);

    }

    public void btnEntrar(View view){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }



    private void verificaUsuarioLogado() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut(); //Deslogar usuario
        if(autenticacao.getCurrentUser() != null ){
            abrirTelaPrincipal();
        }

    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(this, PrincipalActivity.class);
        startActivity(intent);
    }


}
