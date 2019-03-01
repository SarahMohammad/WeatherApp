package com.example.maro.weatherapp.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.maro.weatherapp.models.SimpleWeatherModel;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface WeatherDao {
    @Query("SELECT * FROM simpleweather")
    LiveData<List<SimpleWeatherModel>> getAllLocationsWeathers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SimpleWeatherModel simpleWeather);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWeather(ArrayList<SimpleWeatherModel> simpleWeather);

}