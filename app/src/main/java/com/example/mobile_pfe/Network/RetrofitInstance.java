package com.example.mobile_pfe.Network;

import com.example.mobile_pfe.model.Globals.AppGlobals;
import com.example.mobile_pfe.sevices.MatchService;
import com.example.mobile_pfe.sevices.SportifService;
import com.example.mobile_pfe.sevices.TeamService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitInstance {
    private static final String AUTH_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzA1MzQwMDkyLCJleHAiOjE3MDUzNDg3MzJ9.eKkVm9vw7YPVk4FqC6hanZL4YBXnaxThpteMsA8QgQz2XBR_WG8uvFpwY5gqCyBfG7erm30GMmssxZtfpfK6zw";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new TokenInterceptor(AUTH_TOKEN));

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
    private static Retrofit retrofit;
    public static final String BASE_URL = "http://192.168.0.102:8080/";

    public static SportifService getSportifService() {
        return getRetrofitInstance().create(SportifService.class);
    }
    public static TeamService getTeamService() {
        return getRetrofitInstance().create(TeamService.class);
    }

    // Create an instance of MatchService
    public static MatchService getMatchService() {
        return getRetrofitInstance().create(MatchService.class);
    }
}