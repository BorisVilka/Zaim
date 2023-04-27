package com.app.hroshidozarplatni.api;

import com.app.hroshidozarplatni.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CRCUaAPIClient {

    private CRCUaAPIService service;

    private static CRCUaAPIClient sInstance;

    public static CRCUaAPIService getInstance() {
        if (sInstance == null) {
            sInstance = new CRCUaAPIClient();
        }

        return sInstance.service;
    }

    private CRCUaAPIClient() {
        OkHttpClient okHttpClient = null;
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            okHttpClientBuilder.addInterceptor(logging);
        }

        okHttpClient = okHttpClientBuilder
                .hostnameVerifier((hostname, session) -> true)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://salon-kiev.online/site/")
           //   .baseUrl("http://zaim-odobreno.info/site/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        service = retrofit.create(CRCUaAPIService.class);
    }
}
