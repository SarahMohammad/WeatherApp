package com.example.maro.weatherapp.models;

import com.google.gson.annotations.SerializedName;

class Wind {
    @SerializedName("speed")
    public float speed;
    @SerializedName("deg")
    public float deg;
}
