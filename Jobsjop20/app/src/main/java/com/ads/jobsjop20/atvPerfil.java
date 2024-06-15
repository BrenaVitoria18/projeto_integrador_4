package com.ads.jobsjop20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.jobsjop20.apiExterna.ImgBBResponse;
import com.ads.jobsjop20.apiExterna.ImgBBService;
import com.ads.jobsjop20.mascara.MaskEditTextPhone;
import com.ads.jobsjop20.model.UsuarioModel;
import com.ads.jobsjop20.service.RetrofitService;
import com.ads.jobsjop20.service.UserService;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class atvPerfil extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    EditText descUser, editNumero;
    TextView txtNameuser, txtDescUserEmail;
    CircleImageView imageUser;
    Button btnAtualizar;
    private Uri selectedImageUri;
    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};

    String imageUrl;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_atv_perfil);

        txtNameuser = findViewById(R.id.txtNameuser);
        txtDescUserEmail = findViewById(R.id.txtDescUserEmail);
        descUser = findViewById(R.id.descUser);
        imageUser = findViewById(R.id.imageUser);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnAtualizar = findViewById(R.id.btnUpdateInfo);
        editNumero = findViewById(R.id.editNumero);
        //APLICAÇÃO DA MASCARA PARA FORMATO DE NUMERO
        editNumero.addTextChangedListener(MaskEditTextPhone.insert(editNumero));


        //RECUPERAÇÃO DO DOS DADOS SHAREDPREFERENCES DE ATVPOSTING
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String usuarioShared = sharedPreferences.getString("usuarioModel", "No data found");

        Gson gson = new Gson();
        UsuarioModel usuarioModelShared = gson.fromJson(usuarioShared, new TypeToken<UsuarioModel>() {}.getType());

        // TESTE DE RECUPERAÇÃO DE DADOS DO SHAREDPREFERENCES
        String idUsuario = String.valueOf(usuarioModelShared.getIdUsuario());
        String nome = usuarioModelShared.getNome();
        String email = usuarioModelShared.getEmail();
        String descricao = usuarioModelShared.getDescricao();
        String numero = usuarioModelShared.getNumero();
        String imagemUsuario = usuarioModelShared.getImagemUsuario();

        //VALIDA SE O CAMPO É NULO
        //SE FOR DIFERENTE DE NULO, EXIBE OS DADOS DO SHAREDPREFERENCES
        //CASO CONTRARIO, EXIBE UM VALOR PADRÃO
        if (nome != null) txtNameuser.setText(nome);
        if (email != null) txtDescUserEmail.setText(email);
        if (descricao != null) descUser.setText(descricao);
        if (numero != null) editNumero.setText(numero);
        if (descricao == null || descricao.isEmpty()) descUser.setText("Sem descrição disponível");
        if (numero == null || numero.isEmpty()) editNumero.setText("");

        if (imagemUsuario == null || imagemUsuario.isEmpty()) {
            Glide.with(this)
                    .load(R.drawable.img_default_user) // Imagem padrão
                    .into(imageUser);
        } else {
            Glide.with(this)
                    .load(imagemUsuario)
                    .into(imageUser);
        }


        bottomNavigationView.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(atvPerfil.this, atvPosting.class));
                finish();
            }
        });

        bottomNavigationView.findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(atvPerfil.this, newPosting.class));
            }
        });

        bottomNavigationView.findViewById(R.id.perfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No action needed, already in atvPerfil
            }
        });

        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descricao = descUser.getText().toString();
                String numero = editNumero.getText().toString();

                if (descricao.isEmpty() || numero.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {

                    // inicialização do retrofit - imgBB
                    Retrofit retrofitImgBb = new Retrofit.Builder()
                            .baseUrl("https://api.imgbb.com")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    ImgBBService imgBBService = retrofitImgBb.create(ImgBBService.class);

                    // Verificar se uma imagem foi selecionada
                    if (selectedImageUri != null) {
                        Log.d("URI", "Selected Image URI: " + selectedImageUri);
                        Log.d("URI", "Selected Image URI String: " + selectedImageUri.toString());

                        // Subir imagem para o retrofit
                        String imagePath = getRealPathFromURI(selectedImageUri);
                        if (imagePath != null) {
                            File imageFile = new File(imagePath);

                            // Verificar se o arquivo existe e é válido
                            if (imageFile.exists() && imageFile.isFile()) {
                                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
                                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody);

                                // Enviar a imagem para o serviço ImgBB
                                Call<ImgBBResponse> call = imgBBService.uploadImage(imagePart);
                                call.enqueue(new Callback<ImgBBResponse>() {
                                    @Override
                                    public void onResponse(Call<ImgBBResponse> call, Response<ImgBBResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            imageUrl = response.body().getData().getUrl();
                                            Glide.with(atvPerfil.this)
                                                    .load(imageUrl)
                                                    .into(imageUser);

                                            AtualizarUsuario(v, Long.valueOf(idUsuario), descricao, numero, imageUrl);
                                        } else {
                                            Log.e("IMG_UPLOAD_ERROR", "Failed to upload image to ImgBB");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ImgBBResponse> call, Throwable t) {
                                        Log.e("IMG_UPLOAD_FAILURE", "Failed to upload image to ImgBB: " + t.getMessage());
                                    }
                                });
                            } else {
                                Log.e("INVALID_IMAGE_FILE", "Invalid image file: " + imagePath);
                            }
                        } else {
                            Log.e("INVALID_IMAGE_PATH", "Failed to get real path for URI: " + selectedImageUri);
                        }
                    } else {
                        Log.e("IMAGE_PATH_NULL", "Image path is null");
                        Toast.makeText(atvPerfil.this, "Selecione uma imagem primeiro", Toast.LENGTH_SHORT).show();
                    }



                }
            }
        });

    }
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Obter o URI da imagem selecionada
            selectedImageUri = data.getData();


        }
    }

    private String getRealPathFromURI(Uri uri) {
        String realPath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            realPath = cursor.getString(columnIndex);
            cursor.close();
        }
        return realPath;
    }


    private void AtualizarUsuario(View v, Long userId, String descricao, String numero, String imagemUrl) {
        Retrofit retrofit = RetrofitService.getClient();

        UserService userService = retrofit.create(UserService.class);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("descricao", descricao);
        parametros.put("numero", numero);
        parametros.put("imagemUsuario", imagemUrl);

        Call<UsuarioModel> call = userService.atualizarUsuario(userId, parametros);

        call.enqueue(new Callback<UsuarioModel>() {
            @Override
            public void onResponse(Call<UsuarioModel> call, Response<UsuarioModel> response) {
                if (response.isSuccessful()) {
                    UsuarioModel usuarioAtualizado = response.body();
                    if (usuarioAtualizado != null) {
                        Snackbar snackbar = Snackbar.make(v, "Usuário atualizado com sucesso", Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.GREEN);
                        snackbar.setTextColor(Color.WHITE);
                        snackbar.show();

                        Intent intent = new Intent(atvPerfil.this, atvPosting.class);

                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sharedPreferences.edit();

                        UsuarioModel usuarioModelSharedUpdate = new UsuarioModel();
                        usuarioModelSharedUpdate.setIdUsuario(response.body().getIdUsuario());
                        usuarioModelSharedUpdate.setNome(response.body().getNome());
                        usuarioModelSharedUpdate.setEmail(response.body().getEmail());
                        usuarioModelSharedUpdate.setSenha(response.body().getSenha());
                        usuarioModelSharedUpdate.setDescricao(response.body().getDescricao());
                        usuarioModelSharedUpdate.setImagemUsuario(response.body().getImagemUsuario());
                        usuarioModelSharedUpdate.setNumero(response.body().getNumero());
                        usuarioModelSharedUpdate.setCidade(response.body().getCidade());
                        usuarioModelSharedUpdate.setEstado(response.body().getEstado());

                        Gson gson = new Gson();
                        String usuarioModelSharedConvertido = gson.toJson(usuarioModelSharedUpdate);
                        editor1.putString("usuarioModel", usuarioModelSharedConvertido);
                        editor1.clear();
                        editor1.commit();
                        startActivity(intent);

                    } else {
                        Snackbar snackbar = Snackbar.make(v, "Erro ao processar resposta do servidor", Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.RED);
                        snackbar.setTextColor(Color.WHITE);
                        snackbar.show();
                    }
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e("API_ERROR", errorResponse);
                        Snackbar snackbar = Snackbar.make(v, "Erro ao salvar usuário: " + errorResponse, Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.RED);
                        snackbar.setTextColor(Color.WHITE);
                        snackbar.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }



            @Override
            public void onFailure(Call<UsuarioModel> call, Throwable t) {
                Log.e("API_FAILURE", t.getMessage(), t);
                Snackbar snackbar = Snackbar.make(v, "Erro ao conectar ao servidor", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.RED);
                snackbar.setTextColor(Color.WHITE);
                snackbar.show();
            }
        });
    }


}