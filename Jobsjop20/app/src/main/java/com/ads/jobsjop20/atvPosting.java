package com.ads.jobsjop20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ads.jobsjop20.adapters.PostagemAdapter;
import com.ads.jobsjop20.model.PostagemModel;
import com.ads.jobsjop20.model.UsuarioModel;
import com.ads.jobsjop20.service.PostagemService;
import com.ads.jobsjop20.service.RetrofitService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class atvPosting extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostagemAdapter adapter;
    private List<PostagemModel> postagens = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private PostagemService postagemService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_atv_posting);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostagemAdapter(this, postagens);
        recyclerView.setAdapter(adapter);


        // Inicializar o serviço Retrofit
        Retrofit retrofit = RetrofitService.getClient();
        postagemService = retrofit.create(PostagemService.class);

        // Obter postagens da API e atualizar RecyclerView
        getPostagensFromAPI();




        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String usuarioShared = sharedPreferences.getString("usuarioModel", "No data found");

        Gson gson = new Gson();
        UsuarioModel usuarioModelShared = gson.fromJson(usuarioShared, new TypeToken<UsuarioModel>() {}.getType());


        // Configurar o ouvinte de clique para o item "Home"
        bottomNavigationView.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        // Configurar o ouvinte de clique para o item "Perfil"
        bottomNavigationView.findViewById(R.id.perfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_perfil = new Intent(atvPosting.this,atvPerfil.class);
                startActivity(intent_perfil);

            }
        });

        ImageView iconPost = findViewById(R.id.iconMais);
        iconPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_post = new Intent(atvPosting.this,newPosting.class);
                startActivity(intent_post);
                finish();
            }
        });



    }

    private void getPostagensFromAPI() {
        Call<List<PostagemModel>> call = postagemService.getPostagens();
        call.enqueue(new Callback<List<PostagemModel>>() {
            @Override
            public void onResponse(Call<List<PostagemModel>> call, Response<List<PostagemModel>> response) {
                if (response.isSuccessful()) {
                    postagens.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<PostagemModel>> call, Throwable t) {

            }
        });
    }


}
