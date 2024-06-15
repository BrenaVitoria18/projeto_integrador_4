package com.ads.jobsjop20;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.ads.jobsjop20.model.UsuarioModel;
import com.ads.jobsjop20.service.RetrofitService;
import com.ads.jobsjop20.service.UserService;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class atvCadastro extends AppCompatActivity {

    private EditText editNome, editEmail, editSenha;
    private Button btnCad;
    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_atv_cadastro);

        IniciarComponentes();

        btnCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = editNome.getText().toString();
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() ) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
                    CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(
                            params.width,
                            params.height
                    );
                    layoutParams.gravity = Gravity.TOP;
                    snackbar.getView().setLayoutParams(layoutParams);
                    snackbar.setAnchorView(null);
                    snackbar.show();

                } else{
                    CadastrarUsuario(v, nome, email, senha);

                }
            }
        });

    }

    private void CadastrarUsuario(View v, String nome, String email, String senha) {
        Retrofit retrofit = RetrofitService.getClient();

        UserService userService = retrofit.create(UserService.class);

        UsuarioModel novoUsuario = new UsuarioModel();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senha);

        Call<UsuarioModel> call = userService.cadastrarUsuario(novoUsuario);

        call.enqueue(new Callback<UsuarioModel>() {
            @Override
            public void onResponse(Call<UsuarioModel> call, Response<UsuarioModel> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.GREEN);
                    snackbar.setTextColor(Color.WHITE);
                    ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
                    CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(
                            params.width,
                            params.height
                    );
                    layoutParams.gravity = Gravity.TOP;
                    layoutParams.topMargin = 100;
                    snackbar.getView().setLayoutParams(layoutParams);
                    snackbar.setAnchorView(null);
                    snackbar.show();



                    SharedPreferences sharedPreferences = getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    UsuarioModel usuarioModelShared = new UsuarioModel();
                    usuarioModelShared.setIdUsuario(response.body().getIdUsuario());
                    usuarioModelShared.setNome(response.body().getNome());
                    usuarioModelShared.setEmail(response.body().getEmail());
                    usuarioModelShared.setSenha(response.body().getSenha());
                    usuarioModelShared.setDescricao(response.body().getDescricao());
                    usuarioModelShared.setImagemUsuario(response.body().getImagemUsuario());
                    usuarioModelShared.setNumero(response.body().getNumero());
                    usuarioModelShared.setCidade(response.body().getCidade());
                    usuarioModelShared.setEstado(response.body().getEstado());

                    Gson gson = new Gson();
                    String usuarioModelSharedConvertido = gson.toJson(usuarioModelShared);
                    editor.putString("usuarioModel", usuarioModelSharedConvertido);
                    editor.clear();
                    editor.commit();

                    Intent intent = new Intent(atvCadastro.this,atvPosting.class);
                    startActivity(intent);



                } else {
                    Snackbar snackbar = Snackbar.make(v, "Erro ao cadastrar usuário", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
                    CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(
                            params.width,
                            params.height
                    );
                    layoutParams.gravity = Gravity.TOP;
                    layoutParams.topMargin = 100;
                    snackbar.getView().setLayoutParams(layoutParams);
                    snackbar.setAnchorView(null);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioModel> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(v, "Erro ao conectar ao servidor", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.RED);
                snackbar.setTextColor(Color.WHITE);
                ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
                CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(
                        params.width,
                        params.height
                );
                layoutParams.gravity = Gravity.TOP;
                layoutParams.topMargin = 100;
                snackbar.getView().setLayoutParams(layoutParams);
                snackbar.setAnchorView(null);
                snackbar.show();
            }
        });
    }

    private void IniciarComponentes(){

        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.descUser);
        editSenha = findViewById(R.id.editSenha);
        btnCad = findViewById(R.id.btnCad);
    }
}