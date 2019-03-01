package com.example.maro.weatherapp;

import com.example.maro.weatherapp.models.OpenWeatherResponse;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public abstract class RetrofitClient {

    private static final String baseUrl = "http://api.openweathermap.org/";

    public static WeatherService getClient() {

        OkHttpClient okClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        WeatherService gitApiInterface = client.create(WeatherService.class);
        return gitApiInterface;
    }


    public interface WeatherService {
        @GET("data/2.5/weather?")
        Call<OpenWeatherResponse> getCurrentWeatherData(@Query("lat") String lat, @Query("lon") String lon, @Query("APPID") String appID);

        @GET("data/2.5/weather?")
        Observable<OpenWeatherResponse> getCurrentWeatherDataObservable(@Query("lat") String lat, @Query("lon") String lon, @Query("APPID") String appID);
    }
}
