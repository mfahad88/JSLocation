package com.example.muhammadfahad.jslocation.service;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleService {
    private static final String baseUrl="https://maps.googleapis.com/maps/api/place/nearbysearch/";
    static Retrofit retrofit=null;

    private GoogleService() {
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
