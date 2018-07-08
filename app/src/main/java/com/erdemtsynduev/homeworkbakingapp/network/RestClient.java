package com.erdemtsynduev.homeworkbakingapp.network;

import com.erdemtsynduev.homeworkbakingapp.BuildConfig;
import com.erdemtsynduev.homeworkbakingapp.network.api.Api;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static RestClient ourInstance = new RestClient();
    private static Api api;

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    public static RestClient getInstance() {
        return ourInstance;
    }

    private RestClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel((BuildConfig.DEBUG)
                        ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();

        api = retrofit.create(Api.class);
    }

    public Api getApi() {
        return api;
    }

}
