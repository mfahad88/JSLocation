package com.example.muhammadfahad.jslocation.service;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StatusService {
    private static final String baseUrl="https://mfahad88.000webhostapp.com/js/";
    static Retrofit retrofit=null;

    private StatusService() {
    }

    public static Retrofit getInstance(){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(provideOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();
        okhttpClientBuilder.connectTimeout(5, TimeUnit.SECONDS);
        okhttpClientBuilder.readTimeout(5, TimeUnit.SECONDS);
        okhttpClientBuilder.writeTimeout(5, TimeUnit.SECONDS);
        return okhttpClientBuilder.build();
    }
}
