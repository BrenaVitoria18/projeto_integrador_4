package com.ads.jobsjop20.apiExterna;

import com.google.gson.annotations.SerializedName;

public class ImgBBResponse {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }
    }
}
