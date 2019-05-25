package uet.vnu.weather4cast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    private CurrentWeather currentWeatherData;
    private String currentWeatherUrl, jsonStr;
    private GetWeatherDataTask weatherData;
    private Bundle extras;
    private HashMap weatherIdMap, weatherIconMap;
    private SwipeRefreshLayout swipeLayout;
    private Intent intent;
    private PendingIntent pendingIntent;
    private Calendar calendar;
    private AlarmManager alarmManager;

    /*
     *  Check Internet connection. Return True if the device is connected
     *  and False if the device is disconnected from the Internet
     */

    private boolean hasNetworkConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)MainActivity.this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void initService(long millis)
    {
        /* run the service every millis x 0.001s */
        calendar = Calendar.getInstance();
        intent = new Intent(MainActivity.this, BadWeatherWarningService.class);
        pendingIntent = PendingIntent.getService(MainActivity.this, 0, intent, 0);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), millis, pendingIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* hide the Action Bar */
        getSupportActionBar().hide();

        initService(7000);
        swipeLayout = findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);
        extras = getIntent().getExtras();
        weatherIdMap = (HashMap)extras.getSerializable("WEATHER_MAP");
        weatherIconMap = (HashMap) extras.getSerializable("ICON_MAP");
        currentWeatherUrl = getString(R.string.api_current_weather) + "1581130&" + getString(R.string.api_key);

        if(hasNetworkConnection())
        {
            swipeLayout.setRefreshing(true);
            weatherData = new GetWeatherDataTask(MainActivity.this, currentWeatherUrl);
            weatherData.execute();
            try
            {
                jsonStr = weatherData.get();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            currentWeatherData = new CurrentWeather(jsonStr,
                                            MainActivity.this,
                                                    weatherIdMap,
                                                    weatherIconMap);
            currentWeatherData.displayWeatherInfo();
            currentWeatherData.displayWeatherGraphic();
        }
        else
        {
            Toast.makeText(MainActivity.this, "No Internet connection!", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void onRefresh()
    {
        if(hasNetworkConnection())
        {
            weatherData = new GetWeatherDataTask(MainActivity.this, currentWeatherUrl);
            weatherData.execute();
            try
            {
                jsonStr = weatherData.get();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            currentWeatherData = new CurrentWeather(jsonStr,
                    MainActivity.this,
                    weatherIdMap,
                    weatherIconMap);
            currentWeatherData.displayWeatherInfo();
            currentWeatherData.displayWeatherGraphic();
        }
        else
        {
            Toast.makeText(MainActivity.this, "No Internet connection!", Toast.LENGTH_SHORT ).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    swipeLayout.setRefreshing(false);
                }
            }, 1900);
        }
    }
}
