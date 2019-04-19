package com.awsafalam.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;

public class MainActivity extends AppCompatActivity {

    TextView temperature;
    TextView date;
    TextView location;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;
    private List<Listitem> forecastlist;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        temperature = (TextView) findViewById(R.id.temperature);
        date = (TextView) findViewById(R.id.Date);
        location = (TextView) findViewById(R.id.location);

        recyclerView = (RecyclerView) findViewById(R.id.Recycler_view);
        recyclerView.setHasFixedSize(true);
        // For horizontal scroll view
       // manager = new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        forecastlist = new ArrayList<>();

        File file = getCacheDir();
        File f1 = new File(file , "CacheData");
        File f2 = new File(file , "CacheForecastData");

        if(f1.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(f1));
                String obj = (String) objectInputStream.readObject();
                objectInputStream.close();

                System.out.println(obj);
                System.out.println("JSON Object Read -------------------------------                         -----------------------------------------------                                   ----------------------------");

                JSONObject response = new JSONObject(obj);

                String temp = response.getJSONObject("main").getString("temp");
                temp += "\u2103";

                String humidity = response.getJSONObject("main").getString("humidity");
                String City = response.getString("name");
                String currentDateTimeString = new SimpleDateFormat("EEE, MMM d hh:mm aaa").format(new Date());
                location.setText(City);
                date.setText(currentDateTimeString);
                temperature.setText(temp);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(f2.exists()) {
            // Loading from JSON Array
            try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(f2));
            String   arr = (String) objectInputStream.readObject();

            System.out.println(arr);
            JSONObject response = new JSONObject(arr);
            JSONArray items = response.getJSONArray("list");
            for (int i = 0; i < items.length(); i++) {
                JSONObject a = items.getJSONObject(i);


                String date = a.getString("dt");
                long dt = Long.parseLong(date);

                JSONArray weather = a.getJSONArray("weather");
                JSONObject weatherinfo = weather.getJSONObject(0);

                String description = weatherinfo.getString("description");
                String icon = weatherinfo.getString("icon");
                String temp2 = a.getJSONObject("main").getString("temp");
                String humidity2 = a.getJSONObject("main").getString("humidity");
                String currentDateTimeString2 = new SimpleDateFormat("EEE, hh:mm aaa").format(new Date(dt * 1000));

                temp2 = temp2 + "\u2103";
                humidity2 = description + " " + currentDateTimeString2 + "\n Humidity: " + humidity2 + "%";

                Listitem item = new Listitem(temp2, humidity2, icon);
                forecastlist.add(item);

                recyclerView.setAdapter(adapter);

            }

        }catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        Thread t = new Thread(){
            @Override
            public void run(){
                try {
                    while (true){
                        Thread.sleep(3000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String currentDateTimeString = new SimpleDateFormat("EEE, MMM d hh:mm aaa").format(new Date());
                                date.setText(currentDateTimeString);

                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        };
        t.start();


        adapter = new MyAdapter(forecastlist , this);
        recyclerView.setAdapter(adapter);


        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest  jor = Todaysweather();

        requestQueue.add(jor);

        JsonObjectRequest forecast = new JsonObjectRequest(Request.Method.GET , "http://api.openweathermap.org/data/2.5/forecast?q=Dhaka,Bangladesh&units=metric&type=accurate&appid=a0ec8b174e43526eec358f51b741cc4e",
                null , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    File file = getCacheDir();
                    File f1 = new File(file , "CacheForecastData");
                    if(!f1.exists()){
                        try {
                            f1.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(f1));
                        objectOutputStream.writeObject(response.toString());
                        objectOutputStream.flush();
                        objectOutputStream.close();
                        System.out.println(response.toString()+"Write  Forecast-------------------------------                                   -----------------------------------------------                                   ----------------------------");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    JSONArray items = response.getJSONArray("list");
                    for(int i=0 ; i<items.length() ; i++){
                        JSONObject a = items.getJSONObject(i);

                        String date = a.getString("dt");
                        long dt = Long.parseLong(date);
                        JSONArray weather = a.getJSONArray("weather");
                        JSONObject weatherinfo = weather.getJSONObject(0);

                        String description = weatherinfo.getString("description");
                        String icon = weatherinfo.getString("icon");
                        String temp = a.getJSONObject("main").getString("temp");
                        String humidity = a.getJSONObject("main").getString("humidity");
                        String currentDateTimeString = new SimpleDateFormat("EEE, hh:mm aaa").format(new Date(dt*1000));

                        temp = temp + "\u2103";
                        humidity = description+" "+ currentDateTimeString +"\n Humidity: "+ humidity +"%";

                        Listitem item = new Listitem(temp , humidity , icon);
                        forecastlist.add(item);

                        recyclerView.setAdapter(adapter);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                location.setText(error.toString());
                error.printStackTrace();
            }
        });




        requestQueue.add(forecast);



    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
        // Handle back button pressed here
        // Create action diaogue

    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    private JsonObjectRequest Todaysweather(){
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET , "http://api.openweathermap.org/data/2.5/weather?q=Dhaka,Bangladesh&units=metric&type=accurate&appid=a0ec8b174e43526eec358f51b741cc4e",
                null , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    File file = getCacheDir();
                    File f1 = new File(file , "CacheData");
                    if(!f1.exists()){
                        try {
                            f1.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(f1));
                        objectOutputStream.writeObject(response.toString());
                        objectOutputStream.flush();
                        objectOutputStream.close();
                        System.out.println("Write  -------------------------------                                   -----------------------------------------------                                   ----------------------------");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.clear();
                    editor.apply();
                    String temp = response.getJSONObject("main").getString("temp");
                    editor.putInt("temp", Integer.parseInt(temp));
                    editor.apply();
//                    Toast.makeText(getApplicationContext(), "Preferance saved", Toast.LENGTH_LONG).show();

                    temp += "\u2103";

                    String humidity = response.getJSONObject("main").getString("humidity");
                    String City = response.getString("name");
                    String currentDateTimeString = new SimpleDateFormat("EEE, MMM d hh:mm aaa").format(new Date());
                    location.setText(City);
                    date.setText(currentDateTimeString);
                    temperature.setText(temp);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  location.setText(error.toString());
                error.printStackTrace();
            }
        });
        return jor;
    }


}
