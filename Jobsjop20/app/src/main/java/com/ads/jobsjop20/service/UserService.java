package com.ads.jobsjop20.service;

import com.ads.jobsjop20.model.UsuarioModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @Headers("Accept: application/json")
    @POST("/api/usuario")
    Call<UsuarioModel> cadastrarUsuario(@Body UsuarioModel usuario);


        @Headers("Accept: application/json")
        @PUT("/api/usuario/{id}")
        Call<UsuarioModel> atualizarUsuario(@Path("id") Long id, @Body Map<String, Object> parametros);



    @GET("/api/usuario/exists")
    Call<UsuarioModel> usuarioExiste(@Query("email") String email, @Query("senha") String senha);


}
