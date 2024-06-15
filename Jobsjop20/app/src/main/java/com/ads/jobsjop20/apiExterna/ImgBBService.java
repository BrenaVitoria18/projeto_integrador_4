package com.ads.jobsjop20.apiExterna;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImgBBService {
    @Multipart
    @POST("/1/upload?key=3d3797b31e1ff9304769c16e603f3f3f")
    Call<ImgBBResponse> uploadImage(@Part MultipartBody.Part image);
}
