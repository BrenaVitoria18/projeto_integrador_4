package com.ads.jobsjop20.service;

import com.ads.jobsjop20.model.PostagemModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PostagemService {

    @Headers("Accept: application/json")
    @POST("/api/post")
    Call<PostagemModel> postarPostagem(@Body PostagemModel postagem);

    @Headers("Accept: application/json")
    @GET("/api/post")
    Call<List<PostagemModel>> getPostagens();

    @Headers("Accept: application/json")
    @POST("/api/post/{id}/like")
    Call<PostagemModel> likePost(@Path("id") long id);

    @Headers("Accept: application/json")
    @POST("/api/post/{id}/deslike")
    Call<PostagemModel> deslikePost(@Path("id") long id);
}

