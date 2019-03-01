package com.example.maro.weatherapp.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.maro.weatherapp.models.SimpleWeatherModel;

@Database(version = 1, exportSchema = false, entities = {SimpleWeatherModel.class})

public abstract class AppDataBase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "weatherdb";
    private static AppDataBase sInstance;

    //singleton of appDataBase
    public static AppDataBase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDataBase.class, AppDataBase.DATABASE_NAME).build();
            }

        }
        return sInstance;

    }

    public abstract WeatherDao weatherDao();
}