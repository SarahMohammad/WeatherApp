package com.example.maro.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    private static IAsyncExecute asyncExecute;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(asyncExecute!=null) {
            asyncExecute.executeAsync();
        }
    }
    public interface IAsyncExecute {
         void executeAsync();
    }

    public static void setAsyncExecute(IAsyncExecute listener) {
        AlarmReceiver .asyncExecute = listener;
    }

}

