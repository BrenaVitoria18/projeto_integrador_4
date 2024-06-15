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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.jobsjop20.apiExterna.ImgBBResponse;
import com.ads.jobsjop20.apiExterna.ImgBBService;
import com.ads.jobsjop20.apiExterna.ViaCep;
import com.ads.jobsjop20.mascara.MaskEditTextCep;
import com.ads.jobsjop20.model.CEPResponse;
import com.ads.jobsjop20.model.PostagemModel;
import com.ads.jobsjop20.model.UsuarioModel;
import com.ads.jobsjop20.service.PostagemService;
import com.ads.jobsjop20.service.RetrofitService;
import com.ads.jobsjop20.service.UserService;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class newPosting extends AppCompatActivity {

    private EditText edtDescricao, edtLocal, edtServico;
    private Button btnMidias, btnProx, btnPesquisa;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    private Retrofit retrofitImgBb;
    private ImgBBService imgBBService;

    String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_posting);

        // Inicialização dos elementos de interface
        edtDescricao = findViewById(R.id.edtDescricao);
        edtLocal = findViewById(R.id.informarLocal);
        edtServico = findViewById(R.id.informarServico);
        btnMidias = findViewById(R.id.btnMidias);
        btnProx = findViewById(R.id.btnProx);
        btnPesquisa = findViewById(R.id.btnCEP);


        EditText editCep = findViewById(R.id.editCep);
        editCep.addTextChangedListener(MaskEditTextCep.insert("#####-###", editCep));


        // inicialização do retrofit - imgBB
        retrofitImgBb = new Retrofit.Builder()
                .baseUrl("https://api.imgbb.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        imgBBService = retrofitImgBb.create(ImgBBService.class);


        btnPesquisa.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                consultarCEP(editCep.getText().toString());
            }
        });



        // Configuração do clique do botão "Próximo"
        btnProx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descricaoPost = edtDescricao.getText().toString();
                String locaPost = edtLocal.getText().toString();
                String servicoPost = edtServico.getText().toString(); // Corrigi para usar edtServico

                // Verificar se todos os campos estão preenchidos
                if (descricaoPost.isEmpty() || locaPost.isEmpty() || servicoPost.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                    return;
                }

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
                                        String imageUrl = response.body().getData().getUrl();

                                        // SALVAR POST
                                        Retrofit retrofit = RetrofitService.getClient();

                                        // Recuperando os dados do SharedPreferences - de atvCadastro
                                        SharedPreferences sharedPreferences = getSharedPreferences(
                                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                        String usuarioShared = sharedPreferences.getString("usuarioModel", "No data found");

                                        Gson gson = new Gson();
                                        UsuarioModel usuarioModelShared = gson.fromJson(usuarioShared, new TypeToken<UsuarioModel>() {}.getType());

                                        PostagemService postagemService = retrofit.create(PostagemService.class);
                                        UsuarioModel usuarioModel = new UsuarioModel();
                                        usuarioModel.setIdUsuario(Long.valueOf(usuarioModelShared.getIdUsuario()));
                                        PostagemModel postagemModel = new PostagemModel();
                                        postagemModel.setNomeServico(servicoPost);
                                        postagemModel.setDescricaoPost(descricaoPost);
                                        postagemModel.setImagem(imageUrl);
                                        postagemModel.setLocal(locaPost);
                                        postagemModel.setUsuario(usuarioModel);

                                        Call<PostagemModel> callPost = postagemService.postarPostagem(postagemModel);

                                        callPost.enqueue(new Callback<PostagemModel>() {
                                            @Override
                                            public void onResponse(Call<PostagemModel> callPost, Response<PostagemModel> response) {
                                                if (response.isSuccessful()) {
                                                    Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                                                    snackbar.setBackgroundTint(Color.GREEN);
                                                    snackbar.setTextColor(Color.WHITE);
                                                    snackbar.show();

                                                    Intent intent = new Intent(newPosting.this, atvPosting.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Snackbar snackbar = Snackbar.make(v, "Erro ao cadastrar usuário", Snackbar.LENGTH_SHORT);
                                                    snackbar.setBackgroundTint(Color.RED);
                                                    snackbar.setTextColor(Color.WHITE);
                                                    snackbar.show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<PostagemModel> call, Throwable t) {
                                                Snackbar snackbar = Snackbar.make(v, "Erro ao conectar ao servidor", Snackbar.LENGTH_SHORT);
                                                snackbar.setBackgroundTint(Color.RED);
                                                snackbar.setTextColor(Color.WHITE);
                                                snackbar.show();
                                            }
                                        });

                                        Log.d("IMG_URL", "Uploaded Image URL: " + imageUrl);
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
                        Log.e("IMAGE_PATH_NULL", "Image path is null");
                    }
                } else {
                    Toast.makeText(newPosting.this, "Selecione uma imagem primeiro", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Configuração do clique do botão "Incluir mídia(s)"
        btnMidias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    // Método para abrir a galeria de mídia
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

    private void consultarCEP(String cep) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/") // Base URL do serviço ViaCEP
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ViaCep service = retrofit.create(ViaCep.class);
        Call<CEPResponse> call = service.getCEP(cep);
        call.enqueue(new Callback<CEPResponse>() {
            @Override
            public void onResponse(Call<CEPResponse> call, Response<CEPResponse> response) {
                if (response.isSuccessful()) {
                    CEPResponse cepResponse = response.body();
                    if (cepResponse != null) {
                        // Preencher os campos do layout com os dados do CEPResponse
                        edtLocal.setText(cepResponse.getLocalidade() + " - " + cepResponse.getUf());
                        // Preencher outros campos, se necessário
                    } else {
                        // Handle null response body
                        Log.e("CEP Error", "CEPResponse body is null");
                        // Optionally, display an error message to the user
                    }
                } else {
                    // Handle unsuccessful response (e.g., HTTP status code other than 200)
                    Log.e("CEP Error", "Falha para buscar dados. HTTP status code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CEPResponse> call, Throwable t) {
                // Handle network request failure
                Log.e("CEP Error", "Failed to fetch data. Error: " + t.getMessage());
                // Optionally, display an error message to the user
            }
        });
    }






}
