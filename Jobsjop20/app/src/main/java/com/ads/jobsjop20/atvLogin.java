package com.ads.jobsjop20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.jobsjop20.model.UsuarioModel;
import com.ads.jobsjop20.service.RetrofitService;
import com.ads.jobsjop20.service.UserService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class atvLogin extends AppCompatActivity {

    private Button btnCadastrar;
    private Button btnAcessar;
    private EditText email, senha;

    private static final String TAG = "atvLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_atv_login);

        IniciarComponentes();



        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(atvLogin.this, atvCadastro.class);
                startActivity(intent);
            }
        });

        btnAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailE = email.getText().toString();
                String senhaE = senha.getText().toString();
                if (!emailE.isEmpty() && !senhaE.isEmpty()) {
                    validarUsuario(emailE, senhaE);

                } else {
                    Toast.makeText(atvLogin.this, "Por favor, insira o seu email e senha", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void validarUsuario(String email, String senha) {
        Retrofit retrofit = RetrofitService.getClient();

        UserService userService = retrofit.create(UserService.class);

        Call<UsuarioModel> call = userService.usuarioExiste(email, senha);

        call.enqueue(new Callback<UsuarioModel>() {
            @Override
            public void onResponse(Call<UsuarioModel> call, @NonNull Response<UsuarioModel> response) {
                if (response.isSuccessful()) {
                    UsuarioModel usuarioModel = response.body();

                    if (usuarioModel != null) {
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        Gson gson = new Gson();
                        String usuarioModelSharedConvertido = gson.toJson(usuarioModel);
                        editor.putString("usuarioModel", usuarioModelSharedConvertido);
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(atvLogin.this, atvPosting.class);
                        startActivity(intent);
                    } else {
                        // Usuário não existe
                        Toast.makeText(getApplicationContext(), "Usuário não existe", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Log.e(TAG, "Resposta servidor " + response);
                    Log.e(TAG, "Erro na resposta do servidor: " + response.code());
                    Log.e(TAG, "URL da requisição: " + call.request().url());
                    Toast.makeText(getApplicationContext(), "Erro na resposta do servidor", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UsuarioModel> call, Throwable t) {

                Log.e(TAG, "Falha na comunicação com o servidor: " + t.getMessage(), t);
                Toast.makeText(getApplicationContext(), "Falha na comunicação com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void IniciarComponentes() {
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnAcessar = findViewById(R.id.btnAcessar);
        email = findViewById(R.id.descUser);
        senha = findViewById(R.id.editSenha);
    }
}
