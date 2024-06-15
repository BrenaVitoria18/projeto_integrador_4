package com.ads.jobsjop20.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.ads.jobsjop20.ContratoServico;
import com.ads.jobsjop20.R;
import com.ads.jobsjop20.model.PostagemModel;
import com.ads.jobsjop20.service.PostagemService;
import com.ads.jobsjop20.service.RetrofitService;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostagemAdapter extends RecyclerView.Adapter<PostagemAdapter.PostagemViewHolder> {
    private List<PostagemModel> postagens;
    private Context context;
    ImageButton btnLike, btnCompartilhar;

    public PostagemAdapter(Context context, List<PostagemModel> postagens) {
        this.context = context;
        this.postagens = postagens;
    }

    @NonNull
    @Override
    public PostagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_postagem, parent, false);
        return new PostagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostagemViewHolder holder, int position) {
        PostagemModel postagem = postagens.get(position);
        holder.bind(postagem);

    }

    @Override
    public int getItemCount() {
        return postagens.size();
    }

    public class PostagemViewHolder extends RecyclerView.ViewHolder {

        private  TextView nome, titulo,editDescription, editLocal;
        private ImageView imagem;
        private Button btnContratar;


        public PostagemViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nomeUsuario);
            titulo = itemView.findViewById(R.id.editDescription);
            editDescription = itemView.findViewById(R.id.descricaoS);
            editLocal = itemView.findViewById(R.id.localS);
            imagem = itemView.findViewById(R.id.imageService);
            btnContratar = itemView.findViewById(R.id.buttonContratar);
            btnContratar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        PostagemModel postagem = postagens.get(position);

                        // Criar um Intent
                        Intent intent = new Intent(context, ContratoServico.class);

                        // Adicionar os dados da postagem como extras
                        intent.putExtra("nome_usuario", postagem.getUsuario().getNome());
                        intent.putExtra("titulo", postagem.getNomeServico());
                        intent.putExtra("descricao", postagem.getDescricaoPost());
                        intent.putExtra("local", postagem.getLocal());
                        intent.putExtra("imagem_url", postagem.getImagem());
                        intent.putExtra("usuario", postagem.getIdUsuario());


                        // Iniciar a próxima Activity
                        context.startActivity(intent);

                    }
                }
            });
            btnLike = itemView.findViewById(R.id.buttonIcon1);
            btnCompartilhar = itemView.findViewById(R.id.buttonIcon3);

        }

        public void bind(PostagemModel postagem) {
            nome.setText(postagem.getUsuario().getNome());
            titulo.setText(postagem.getNomeServico());
            editDescription.setText(postagem.getDescricaoPost());
            editLocal.setText(postagem.getLocal());
            Glide.with(context)
                    .load(postagem.getImagem())
                    .into(imagem);

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drawable drawable = btnLike.getDrawable();
                    if (drawable != null) {
                        if (drawable.getConstantState().equals(ContextCompat.getDrawable(context, R.drawable.icon_like).getConstantState())) {
                            like(v, postagem.getIdPost());
                        } else if (drawable.getConstantState().equals(ContextCompat.getDrawable(context, R.drawable.icons8_heart_24).getConstantState())) {
                            deslike(v, postagem.getIdPost());
                        }
                    }
                }
            });
            btnCompartilhar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    compartilharPost(postagem.getNomeServico(), postagem.getDescricaoPost(), postagem.getUsuario().getNumero());
                }
            });


        }

        public void like (View v, Long id){
            Retrofit retrofit = RetrofitService.getClient();
            PostagemService postagemService = retrofit.create(PostagemService.class);
            Call<PostagemModel> call = postagemService.likePost(id);
            call.enqueue(new Callback<PostagemModel>() {
                @Override
                public void onResponse(Call<PostagemModel> call, Response<PostagemModel> response) {
                    if (response.isSuccessful()) {
                        PostagemModel postLike = response.body();
                        if (postLike != null) {
                            Snackbar snackbar = Snackbar.make(v, "Like com sucesso", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.GREEN);
                            snackbar.setTextColor(Color.WHITE);
                            snackbar.show();

                            Glide.with(context)
                                    .load(R.drawable.icons8_heart_24)
                                    .into(btnLike);


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
                            Snackbar snackbar = Snackbar.make(v, "Erro ao salvar o like: " + errorResponse, Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.RED);
                            snackbar.setTextColor(Color.WHITE);
                            snackbar.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }



                @Override
                public void onFailure(Call<PostagemModel> call, Throwable t) {
                    Log.e("API_FAILURE", t.getMessage(), t);
                    Snackbar snackbar = Snackbar.make(v, "Erro ao conectar ao servidor", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            });
        }
        public void deslike (View v, Long id){
            Retrofit retrofit = RetrofitService.getClient();
            PostagemService postagemService = retrofit.create(PostagemService.class);
            Call<PostagemModel> call = postagemService.deslikePost(id);
            call.enqueue(new Callback<PostagemModel>() {
                @Override
                public void onResponse(Call<PostagemModel> call, Response<PostagemModel> response) {
                    if (response.isSuccessful()) {
                        PostagemModel postLike = response.body();
                        if (postLike != null) {
                            Snackbar snackbar = Snackbar.make(v, "Deslike com sucesso", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.GREEN);
                            snackbar.setTextColor(Color.WHITE);
                            snackbar.show();

                            Glide.with(context)
                                    .load(R.drawable.icon_like)
                                    .into(btnLike);


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
                            Snackbar snackbar = Snackbar.make(v, "Erro ao salvar o deslike: " + errorResponse, Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.RED);
                            snackbar.setTextColor(Color.WHITE);
                            snackbar.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PostagemModel> call, Throwable t) {
                    Log.e("API_FAILURE", t.getMessage(), t);
                    Snackbar snackbar = Snackbar.make(v, "Erro ao conectar ao servidor", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            });
        }

        private void compartilharPost(String servico, String descricao, String contato) {
            String conteudo = "Jobshop: " + "\nServiço: " + servico + "\nDescrição: " + descricao + "\nContato: " + contato;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, conteudo);
            context.startActivity(Intent.createChooser(intent, "Compartilhar via"));
        }
    }
}
