package com.example.maro.weatherapp.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "simpleweather")
public class SimpleWeatherModel {

    @PrimaryKey
    private int id;
    private String name;
    private String description;
    private String icon ;
    private String state;
    private String temp;
    private String humidity;
    private String lon;
    private String lat;
    private String country;


    public SimpleWeatherModel(int id ,  String name , String description , String icon , String state ,
                              String humidity,  String lon, String lat , String temp , String country) {
        this.id = id;
        this.name =name;
        this.description=description;
        this.icon = icon;
        this.state = state;
        this.humidity = humidity;
        this.lon = lon;
        this.lat = lat;
        this.temp = temp;
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
