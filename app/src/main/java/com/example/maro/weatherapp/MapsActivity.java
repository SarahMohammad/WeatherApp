package com.example.maro.weatherapp;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maro.weatherapp.models.OpenWeatherResponse;
import com.example.maro.weatherapp.models.SimpleWeatherModel;
import com.example.maro.weatherapp.room.AppDataBase;
import com.example.maro.weatherapp.room.WeatherExecutor;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RetrofitClient.WeatherService apiService;
    private TextView locationCount;
    private ProgressDialog dialog;
    private int mCount;
    private SimpleWeatherModel simpleWeatherModel;
    private AppDataBase appDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationCount = findViewById(R.id.counter);
        mCount = 0;
        appDataBase = AppDataBase.getInstance(getApplicationContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        apiService = RetrofitClient.getClient();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                getWeatherData(arg0.latitude, arg0.longitude);
            }
        });
    }


    private void getWeatherData(double lat, double lon) {
        dialog = ProgressDialog.show(MapsActivity.this, "", "loading...");
        Call<OpenWeatherResponse> call = apiService.getCurrentWeatherData(String.valueOf(lat), String.valueOf(lon), Helper.APPID);
        call.enqueue(new Callback<OpenWeatherResponse>() {
            @Override
            public void onResponse(Call<OpenWeatherResponse> call, Response<OpenWeatherResponse> response) {
                dialog.dismiss();

                if (response.isSuccessful() && response.code() == Helper.CODE_SUCCESS) {
                    if (response.body() != null) {

                        //convert kelvin to c
                        Long tempVal = Math.round(Math.floor(response.body().main.temp - 273.15));

                        simpleWeatherModel = new SimpleWeatherModel(response.body().id, response.body().name,
                                response.body().weather.get(0).description, response.body().weather.get(0).icon,
                                response.body().weather.get(0).main, String.valueOf(response.body().main.humidity),
                                String.valueOf(response.body().coord.lon), String.valueOf(response.body().coord.lon),
                                String.valueOf(tempVal), response.body().sys.country);
                    }

                    //update the counter and insert to the room
                    mCount = mCount + 1;
                    locationCount.setText(String.format(getResources().getString(R.string.counter_messages), mCount));
                    WeatherExecutor.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (simpleWeatherModel != null) {
                                appDataBase.weatherDao().insert(simpleWeatherModel);
                            }
                        }
                    });

                } else {
                    Toast.makeText(MapsActivity.this, response.message() + "", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OpenWeatherResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });

    }
}
