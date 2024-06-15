package com.ads.jobsjop20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ads.jobsjop20.model.UsuarioModel;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ContratoServico extends AppCompatActivity {

    ImageView btnVoltar;
    Button btnWhatsapp;
    private ImageView imagemServico;
    private TextView nomeUsuario, tituloServico, descricaoServico, localServico, testeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contrato_servico);


        btnVoltar = findViewById(R.id.btnVoltar);
        imagemServico = findViewById(R.id.imgServico);
        nomeUsuario = findViewById(R.id.editAnuciante);
        tituloServico = findViewById(R.id.editServico);
        descricaoServico = findViewById(R.id.descricaoServico);
        localServico = findViewById(R.id.txtCidade);

        btnWhatsapp = findViewById(R.id.btnWhatsapp);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String usuarioShared = sharedPreferences.getString("usuarioModel", "No data found");

        Gson gson = new Gson();
        UsuarioModel usuarioModelShared = gson.fromJson(usuarioShared, new TypeToken<UsuarioModel>() {}.getType());


        Intent intent = getIntent();
        if (intent != null) {
            String nome = intent.getStringExtra("nome_usuario");
            String titulo = intent.getStringExtra("titulo");
            String descricao = intent.getStringExtra("descricao");
            String local = intent.getStringExtra("local");
            String imagemUrl = intent.getStringExtra("imagem_url");


            nomeUsuario.setText(nome);
            tituloServico.setText(titulo);
            descricaoServico.setText(descricao);
            localServico.setText(local);
            Glide.with(this)
                    .load(imagemUrl)
                    .into(imagemServico);

        }
        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroFormatado = "+55" + usuarioModelShared.getNumero();
                String numero = removerMascaraNumero(numeroFormatado);
                String url = "https://api.whatsapp.com/send?phone=" + numero;

                if (whatsappInstalado()) {
                    Log.e("WhatsAppRedirect", "URL: " + url);
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ContratoServico.this, "Erro ao abrir o WhatsApp.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ContratoServico.this, "WhatsApp não está instalado. Entre em contato pelo chat", Toast.LENGTH_LONG).show();
                }
            }
        });



        btnVoltar = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContratoServico.this, atvPosting.class);
                startActivity(intent);
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public static String removerMascaraNumero(String numero) {
        return numero.replaceAll("[^\\d]", "");
    }

    private boolean whatsappInstalado() {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}