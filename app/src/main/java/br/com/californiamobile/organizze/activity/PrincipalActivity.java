package br.com.californiamobile.organizze.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import br.com.californiamobile.organizze.R;
import br.com.californiamobile.organizze.model.Movimentacao;
import br.com.californiamobile.organizze.model.Usuario;
import br.com.californiamobile.organizze.adapter.AdapterMovimentacao;
import br.com.californiamobile.organizze.config.ConfiguracaoFirebase;
import br.com.californiamobile.organizze.helper.Base64Custom;

public class PrincipalActivity extends AppCompatActivity {

    //Atributos
    private TextView txtSaudacao;
    private TextView txtSaldo;
    private MaterialCalendarView calendarView;
    private RecyclerView rvMovimentos;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoTotal = 0.0;
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private DatabaseReference movimentacaoRef;
    private String mesAnoSelecionado;
    private ValueEventListener valueEventListenerMovimentacoes;
    private Movimentacao movimentacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Organizze");


        //FVBIds
        txtSaudacao   = findViewById(R.id.principal_txtSaudacao);
        txtSaldo      = findViewById(R.id.principal_txtSaldo);
        calendarView  = findViewById(R.id.principal_calender);
        rvMovimentos  = findViewById(R.id.principal_rvMovimentos);

        configuraCalendarView();
        configuraRV();
        swipe();
    }

    public void swipe() {

        final ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                excluirMovimento(viewHolder);

            }
        };


        new ItemTouchHelper(itemTouch).attachToRecyclerView(rvMovimentos);
    }

    public void excluirMovimento(final RecyclerView.ViewHolder viewHolder) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir movimentação da conta");
        alertDialog.setMessage("Confirma a exclusão do movimento? ");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();
                movimentacao = movimentacoes.get(position);

                String emailUsuario =  autenticacao.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64Custom(emailUsuario);
                movimentacaoRef = firebaseRef.child("movimentacao")
                        .child(idUsuario)
                        .child(mesAnoSelecionado);
                movimentacaoRef.child(movimentacao.getKey()).removeValue();
                adapterMovimentacao.notifyItemRemoved(position);
                atualizarSaldo();
                
            }
        });
        
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(PrincipalActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert =alertDialog.create();
        alert.show();



    }

    public void atualizarSaldo() {

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idusuario = Base64Custom.codificarBase64Custom(emailUsuario);
        usuarioRef = firebaseRef.child("usuarios").child(idusuario);

        //Atualizando caso a exclusào seja receita
        if(movimentacao.getTipo().equals("r")){
            receitaTotal = receitaTotal - movimentacao.getValor();
            usuarioRef.child("receitaTotal").setValue(receitaTotal);
        }

        //Atualizando caso a exclusao seja despesa
        if(movimentacao.getTipo().equals("d")){
             despesaTotal = despesaTotal - movimentacao.getValor();
             usuarioRef.child("despesaTotal").setValue(despesaTotal);
        }

    }

    public void recuperarMovimentos(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64Custom(emailUsuario);
        movimentacaoRef = firebaseRef.child("movimentacao")
                                     .child(idUsuario)
                                     .child(mesAnoSelecionado);
        Log.i("Data", "Mes e ano: " + mesAnoSelecionado + " " + idUsuario);

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movimentacoes.clear();
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    //Log.i("Dados", dados.toString());
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    //Log.i("DadosRetorno", "Dados: " + movimentacao.getDescricao());
                    movimentacao.setKey(dados.getKey());
                    movimentacoes.add(movimentacao);
                }
                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void configuraRV() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PrincipalActivity.this);
        rvMovimentos.setLayoutManager(layoutManager);
        rvMovimentos.setHasFixedSize(true);
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, PrincipalActivity.this);
        rvMovimentos.setAdapter(adapterMovimentacao);
    }

    private void recuperarResumo() {

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idusuario = Base64Custom.codificarBase64Custom(emailUsuario);
        usuarioRef = firebaseRef.child("usuarios").child(idusuario);

         valueEventListenerUsuario =  usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoTotal = receitaTotal - despesaTotal;
                txtSaudacao.setText("Olá " + usuario.getNome());

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format(resumoTotal);

                txtSaldo.setText("R$ " + resultadoFormatado);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void addReceita(View view){
        startActivity(new Intent(PrincipalActivity.this, ReceitaActivity.class));
    }

    public void addDespesa(View view){
        startActivity(new Intent(this, DespesaActivity.class));
    }


    public void configuraCalendarView(){

        //CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        CharSequence meses[] = {"Janeiro","Fevereiro", "Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};

        calendarView.setTitleMonths(meses);

        CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d",(dataAtual.getMonth() + 1));
        mesAnoSelecionado = String.valueOf(mesSelecionado + "" + dataAtual.getYear());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
                String mesSelecionado = String.format("%02d",(calendarDay.getMonth() + 1));
                mesAnoSelecionado = String.valueOf(mesSelecionado + "" + calendarDay.getYear());
                //Log.i("Data", "Mes e ano: " + mesAnoSelecionado);

                movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
                recuperarMovimentos();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sair, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId() ){
            case R.id.menuSair:
                //autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();//mudar aqui
                autenticacao.signOut();
                startActivity(new Intent(PrincipalActivity.this, MainActivity.class));
                finish();
                break;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentos();
    }

    @Override
    protected void onStop() {
        super.onStop();

        usuarioRef.removeEventListener(valueEventListenerUsuario);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
    }
}
