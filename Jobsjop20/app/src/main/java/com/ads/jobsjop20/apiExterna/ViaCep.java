package com.ads.jobsjop20.apiExterna;

import com.ads.jobsjop20.model.CEPResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ViaCep {
    @GET("{cep}/json/")
    Call<CEPResponse> getCEP(@Path("cep") String cep);
}
