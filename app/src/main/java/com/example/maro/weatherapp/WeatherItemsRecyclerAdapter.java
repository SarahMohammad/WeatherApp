package com.example.maro.weatherapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maro.weatherapp.models.SimpleWeatherModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class WeatherItemsRecyclerAdapter extends RecyclerView.Adapter<WeatherItemsRecyclerAdapter.ViewHolder> {


    private Context mContext;
    private List<SimpleWeatherModel> weathers;
    private static String ICON_URL = "http://openweathermap.org/img/w/";

    public WeatherItemsRecyclerAdapter(Context context, List<SimpleWeatherModel> weathers) {
        this.mContext = context;
        this.weathers = weathers;

    }


    @NonNull
    @Override
    public WeatherItemsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView;
        ViewHolder viewHolder;
        itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_weather, parent, false);
        viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherItemsRecyclerAdapter.ViewHolder holder, final int position) {
        holder.locationTv.setText(weathers.get(position).getCountry() + "," + weathers.get(position).getName());
        holder.weatherMainTv.setText(weathers.get(position).getState());
        holder.weatherDescriptionTv.setText(weathers.get(position).getDescription());
        holder.mainTempTv.setText(String.valueOf(weathers.get(position).getTemp() + "\u2103"));
        String iconName = weathers.get(position).getIcon();
        String iconUrl = ICON_URL + iconName + ".png";
        Picasso.with(mContext).load(iconUrl).into(holder.weatherIconIv);
    }

    @Override
    public int getItemCount() {
        return weathers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationTv, weatherMainTv, weatherDescriptionTv, mainTempTv;
        ImageView weatherIconIv;

        ViewHolder(View itemView) {
            super(itemView);

            locationTv = itemView.findViewById(R.id.location_tv);
            weatherMainTv = itemView.findViewById(R.id.weather_main_tv);
            weatherDescriptionTv = itemView.findViewById(R.id.weather_description_tv);
            mainTempTv = itemView.findViewById(R.id.weather_degree_tv);
            weatherIconIv = itemView.findViewById(R.id.weather_icon_iv);
        }
    }
}
