package com.example.weathertracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String DATABASE = "CITY_SAVED";
    public static final String CITYNAME = "CITY";

    private Spinner spinner;
    private TextView instructions;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> cities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        instructions = findViewById(R.id.instructions);

        instructions.setText("1. Long press on your home screen, you will see some options at the bottom.\n\n" +
                "2. Select WIDGETS from options.\n\n" +
                "3. Search for Weather Tracker and long press to place it on your Home screen.\n\n" +
                "4. Refresh the Widget by pressing refresh button.\n\n" +
                "5. Done\n\n\n" +
                "Developers and Designers :\n\n" +
                "\t\tAnshuman Pandey\n" +
                "\t\tHardik Goel");
        fetchAllCities();

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cities);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences sharedPreferences = getSharedPreferences(DATABASE,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CITYNAME,cities.get(position));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(DATABASE,MODE_PRIVATE);
        String city = sharedPreferences.getString(CITYNAME,"delhi");
        spinner.setSelection(cities.indexOf(city));
    }

    private void fetchAllCities() {
        cities = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("cities.txt")));
            String line;
            while((line = reader.readLine())!=null){
                cities.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
