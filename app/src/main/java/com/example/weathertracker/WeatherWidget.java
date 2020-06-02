package com.example.weathertracker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class WeatherWidget extends AppWidgetProvider {

    private static final String CITYNAME = "city";
    private static final String LAST_UPDATED_TIME = "lastupdatedtime";
    private static final String API_URL_BASE = "https://weatherscrap.herokuapp.com/?k=MVwyYghfeMXlJOAP2LuYmPYuV&c=";
    private static final String ERROR_STRING = "Unknown Error Occurred...";
    private static final String ACTIVITY_STARTER = "MAIN_ACTIVITY";
    private static final String ACTION_MY_BUTTON = "REFRESHED_CLICKED";

    private StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
       StrictMode.setThreadPolicy(policy);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
            Intent intent = new Intent(context, getClass());
            intent.setAction(ACTION_MY_BUTTON);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.refreshbtn, pendingIntent);
            Intent intent1 = new Intent(context, getClass());
            intent1.setAction(ACTIVITY_STARTER);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, 0);
            views.setOnClickPendingIntent(R.id.layout, pendingIntent1);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        StrictMode.setThreadPolicy(policy);
        if (Objects.requireNonNull(intent.getAction()).equals(ACTION_MY_BUTTON)) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

            ArrayList<Weather> weathers = prepare_make_model_data(context);
            SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.DATABASE,Context.MODE_PRIVATE);
            String city = sharedPreferences.getString(MainActivity.CITYNAME,"Delhi");
            views.setTextViewText(R.id.city_name,city);

            if (weathers != null) {
                views.setTextViewText(R.id.date1, weathers.get(0).date);
                views.setTextViewText(R.id.high1,weathers.get(0).highest);
                views.setTextViewText(R.id.low1, weathers.get(0).lowest);

                views.setTextViewText(R.id.date2, weathers.get(1).date);
                views.setTextViewText(R.id.high2, weathers.get(1).highest);
                views.setTextViewText(R.id.low2,  weathers.get(1).lowest);

                views.setTextViewText(R.id.date3,  weathers.get(2).date);
                views.setTextViewText(R.id.high3, weathers.get(2).highest);
                views.setTextViewText(R.id.low3, weathers.get(2).lowest);


                views.setTextViewText(R.id.date4,  weathers.get(3).date);
                views.setTextViewText(R.id.high4, weathers.get(3).highest);
                views.setTextViewText(R.id.low4,  weathers.get(3).lowest);

                views.setTextViewText(R.id.date5, weathers.get(4).date);
                views.setTextViewText(R.id.high5,weathers.get(4).highest);
                views.setTextViewText(R.id.low5, weathers.get(4).lowest);
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, WeatherWidget.class);
            appWidgetManager.updateAppWidget(componentName, views);
            Toast.makeText(context, "Data Refreshed...", Toast.LENGTH_SHORT).show();
        } else if (ACTIVITY_STARTER.equals(intent.getAction())) {
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        } else
            super.onReceive(context, intent);
    }


    private ArrayList<Weather> prepare_make_model_data(Context context) {
        ArrayList<Weather> weathers = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.DATABASE,Context.MODE_PRIVATE);
        String city = sharedPreferences.getString(MainActivity.CITYNAME,"Delhi");

        city = convert(city);

        URL url = null;
        BufferedReader bufferedReader = null;
        HttpURLConnection connection = null;
        Weather weather = null;
        StringBuilder fulltext = new StringBuilder();
        try {
            //FETCHING TEXT FROM URL
            url = new URL(API_URL_BASE+city);
            connection = (HttpURLConnection) url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            int temp = bufferedReader.read();
            fulltext.append((char) temp);
            while (temp != -1) {
                temp = bufferedReader.read();
                fulltext.append((char) temp);
            }
            //----------------------
            JSONObject main = new JSONObject(fulltext.toString());
            for (Iterator<String> it = main.keys(); it.hasNext(); ) {
                String s = it.next();
                Weather w = new Weather();
                w.date = s.toString();
                w.highest = main.getJSONObject(s).get("highest").toString();
                w.lowest = main.getJSONObject(s).get("lowest").toString();
                weathers.add(w);
            }
            bufferedReader.close();
        } catch (Exception ex) {
        }
        return weathers;
    }

    private String convert(String city) {
        String c = "";
        for (String x : city.split(" ")) {
            c += x.toLowerCase() + "_";
        }
        return c.substring(0, city.length());
    }
}