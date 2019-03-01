package com.example.maro.weatherapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.maro.weatherapp.models.LocationCoordinates;
import com.example.maro.weatherapp.models.OpenWeatherResponse;
import com.example.maro.weatherapp.models.SimpleWeatherModel;
import com.example.maro.weatherapp.room.AppDataBase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


public class MainActivity extends AppCompatActivity  implements AlarmReceiver.IAsyncExecute {


    private RetrofitClient.WeatherService apiService;
    private RecyclerView weatherItemsRecycler;
    private AppDataBase appDataBase;
    private WeatherItemsRecyclerAdapter weatherItemsRecyclerAdapter;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private ArrayList<LocationCoordinates> locationCoordinates;
    private LocationCoordinates coordinates;
    private List<Observable<?>> requests;
    private TextView hintTv;


    private void initializeViews(){
        weatherItemsRecycler = findViewById(R.id.weather_items_recyclerview);
        hintTv = findViewById(R.id.no_picked_location_tv);
        appDataBase = AppDataBase.getInstance(getApplicationContext());
        apiService = RetrofitClient.getClient();
        requests = new ArrayList<>();
        getSupportActionBar().setElevation(0);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        weatherItemsRecycler.setLayoutManager(mLinearLayoutManager);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        getLocationsFromDbIfExist();

    }

    private void getLocationsFromDbIfExist() {
        appDataBase.weatherDao().getAllLocationsWeathers()
                .observe(MainActivity.this, new Observer<List<SimpleWeatherModel>>() {
                    @Override
                    public void onChanged(@Nullable List<SimpleWeatherModel> weathers) {
                        assert weathers != null;
                        if (weathers.size() != 0) {
                                hintTv.setVisibility(View.GONE);
                                locationCoordinates = new ArrayList<>();
                                for(int i =0 ; i <weathers.size() ; i ++ ) {
                                    coordinates = new LocationCoordinates(weathers.get(i).getLat() , weathers.get(i).getLon());
                                    locationCoordinates.add(coordinates);
                                }
                                startAlarm();
                                weatherItemsRecyclerAdapter = new WeatherItemsRecyclerAdapter(MainActivity.this , weathers);
                                weatherItemsRecycler.setAdapter(weatherItemsRecyclerAdapter);
                            }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_location) {
            Intent i = new Intent(MainActivity.this , MapsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //update weather every hour
    public void startAlarm() {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 3600000;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        AlarmReceiver.setAsyncExecute(this);
    }

    @Override
    public void executeAsync() {
       for (int i = 0 ; i< locationCoordinates.size() ; i++){
           requests.add(apiService.getCurrentWeatherDataObservable(locationCoordinates.get(i).latitude , locationCoordinates.get(i).longitude , Helper.APPID));
       }
        WeatherTask task = new WeatherTask(getApplicationContext());
        task.execute(requests);
    }
}
class WeatherTask extends AsyncTask<List<Observable<?>>, Void, Void> {

    Context mContext;

    public WeatherTask (Context context){
        mContext = context;
    }


    @SuppressLint("CheckResult")
    protected Void doInBackground(List<Observable<?>>... requests) {

        // Zip all requests with the Function, which will receive the results.
        Observable.zip(
                requests[0],
                new Function<Object[], Object>() {
                    @Override
                    public Object apply(Object[] objects) throws Exception {
                        ArrayList<SimpleWeatherModel> simpleList = new ArrayList<>();
                        for(int i = 0 ; i < objects.length ; i ++){
                            SimpleWeatherModel simpleWeatherModel = new SimpleWeatherModel(((OpenWeatherResponse) objects[i]).id ,
                                    ((OpenWeatherResponse) objects[i]).name ,((OpenWeatherResponse) objects[i]).weather.get(0).description,
                                    ((OpenWeatherResponse) objects[i]).weather.get(0).icon,((OpenWeatherResponse) objects[i]).weather.get(0).main ,
                                    String.valueOf(((OpenWeatherResponse) objects[i]).main.humidity) , String.valueOf(((OpenWeatherResponse) objects[i]).coord.lon) ,
                                    String.valueOf(((OpenWeatherResponse) objects[i]).coord.lat ),
                                    String.valueOf(Math.round(Math.floor(((OpenWeatherResponse) objects[i]).main.temp - 273.15))) ,
                                    ((OpenWeatherResponse) objects[i]).sys.country);
                            simpleList.add(simpleWeatherModel);
                        }

                        AppDataBase.getInstance(mContext).weatherDao().updateWeather(simpleList);
                        return new Object();
                    }
                })
                .subscribe(
                        new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                e.printStackTrace();
                            }
                        }
                );
        return null;
    }
}