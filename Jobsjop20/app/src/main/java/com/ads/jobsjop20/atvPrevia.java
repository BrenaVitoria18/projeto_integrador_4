package com.ads.jobsjop20;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.ads.jobsjop20.model.PostagemModel;
import com.ads.jobsjop20.model.ServicoModel;
import com.ads.jobsjop20.model.UsuarioModel;
import com.ads.jobsjop20.service.PostagemService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class atvPrevia extends AppCompatActivity {

    private TextView descricao_post, txtLocal, nome_Usuario, nome_Servico;
    private ImageView imgMidia;
    private Button btnPostar;
    private PostagemService postagemService;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri imagemUri; // Armazenar o URI da imagem selecionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_previa);

        descricao_post = findViewById(R.id.txtDescricao);
        txtLocal = findViewById(R.id.localS);
        imgMidia = findViewById(R.id.imgMidia);
        btnPostar = findViewById(R.id.btnPostar);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.14:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        postagemService = retrofit.create(PostagemService.class);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        if (intent != null) {
            String descricao = intent.getStringExtra("descricao");
            String local = intent.getStringExtra("local");
            String usuario = intent.getStringExtra("nomeUsuario");
            String servico = intent.getStringExtra("nomeServico");
            imagemUri = intent.getParcelableExtra("imagemUri");

            descricao_post.setText(descricao);
            txtLocal.setText(local);
            nome_Usuario.setText(usuario);
            nome_Servico.setText(servico);

            if (imagemUri != null) {
                imgMidia.setImageURI(imagemUri);
            }
        }
/*
        btnPostar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descricao = descricao_post.getText().toString();
                String local = txtLocal.getText().toString();
                String nomeServico;
                String nomeUsuario;

                if (imagemUri != null) {
                    uploadImagem(descricao, local, imagemUri, nomeServico, nomeUsuario); // Passando o URI da imagem para o método de upload
                } else {
                    Toast.makeText(atvPrevia.this, "Selecione uma imagem", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
    /*
    private void uploadImagem(String descricao, String local, Uri imagem, ServicoModel nomeServico, UsuarioModel nomeUsuario) {
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(imagem)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                PostagemModel postagem = new PostagemModel(descricao, imageUrl, local, nomeUsuario, nomeServico);
                                enviarPostagem(postagem);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(atvPrevia.this, "Falha ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                    }
                });
    }*/

    private void enviarPostagem(PostagemModel postagem) {
        Call<PostagemModel> call = postagemService.postarPostagem(postagem);
        call.enqueue(new Callback<PostagemModel>() {
            @Override
            public void onResponse(Call<PostagemModel> call, Response<PostagemModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(atvPrevia.this, "Postagem enviada com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Postagem", "Falha ao enviar postagem: " + response.message());
                    Toast.makeText(atvPrevia.this, "Falha ao enviar postagem", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostagemModel> call, Throwable t) {
                Toast.makeText(atvPrevia.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
