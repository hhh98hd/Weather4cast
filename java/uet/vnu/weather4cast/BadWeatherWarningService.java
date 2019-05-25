package uet.vnu.weather4cast;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

class GetWeatherTask extends AsyncTask<Void, Integer, String>
{
    private String payload;
    private String url;
    private InputStream inputStream;

    /* Constructor */
    public GetWeatherTask(String _url)
    {
        this.url = _url;
    }

    /* Convert input stream to string */
    private String inputStreamToString(InputStream is)
    {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is), 4096);
        try
        {
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
                bufferedReader.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    @Override
    protected String doInBackground(Void... voids)
    {
        /* Get response from URL */
        try
        {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                inputStream = connection.getInputStream();
                payload = inputStreamToString(inputStream);
            }
            connection.disconnect();
            inputStream.close();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return payload;
    }
}

public class BadWeatherWarningService extends Service
{
    private String jsonStr, forecastWeatherUrl;
    private JsonParser parser;
    private GetWeatherTask getWeatherTask;
    private Calendar calendar;

    public BadWeatherWarningService()
    {

    }

    private boolean hasNetworkConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplication().getSystemService(CONNECTIVITY_SERVICE);
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

    private String inputStreamToString(InputStream is)
    {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is), 4096);
        try
        {
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
                bufferedReader.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void makeNotification(int resId, String warningText)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(resId)
                .setContentTitle("Cảnh báo thời tiết xấu")
                .setContentText(warningText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String warningText;
        if(hasNetworkConnection())
        {
            forecastWeatherUrl = this.getString(R.string.api_5_day_weather) + "1581130&" + this.getString(R.string.api_key);
            getWeatherTask = new GetWeatherTask(forecastWeatherUrl);
            getWeatherTask.execute();
            try
            {
                jsonStr = getWeatherTask.get();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            parser = new JsonParser(jsonStr);
            int weatherId = parser.getForecastWeatherId();
            /* thunderstorm */
            if(200 <= weatherId && weatherId <= 232)
            {
                warningText = "Có thể có giông trong một vài giờ tới";
                makeNotification(R.mipmap.img_weather_t_storm, warningText);
            }
            /* light rain */
            else if(weatherId == 500)
            {
                warningText = "Có thể có mưa nhỏ trong một vài giờ tới";
                makeNotification(R.mipmap.img_weather_light_rain_d, warningText);
            }
            /* moderate rain */
            else if(weatherId == 501)
            {
                warningText = "Có thể có mưa trong một vài giờ tới";
                makeNotification(R.mipmap.img_weather_mod_rain, warningText);
            }
            /* heavy rain */
            else if(weatherId == 313 || weatherId == 314 || (502 <= weatherId && weatherId <= 531))
            {
                warningText = "Có thể có mưa to trong một vài giờ tới ";
                makeNotification(R.mipmap.img_weather_heavy_rain, warningText);
            }
            /* snow */
            else if(weatherId <= 600 && weatherId <= 622)
            {
                warningText = "Tuyết có thể sẽ rơi trong một vài giờ tới";
                makeNotification(R.mipmap.img_weather_snow, warningText);
            }
            /* smoke and dust */
            else if(711 <= weatherId && weatherId <= 731 || weatherId == 761)
            {
                warningText = "Trong không khí có nhiều khói bụi";
                makeNotification(R.mipmap.img_weather_fog, warningText);
            }
            /* fog */
            else if(weatherId == 741)
            {
                warningText = "Sương mù dày đặc";
                makeNotification(R.mipmap.img_weather_fog, warningText);
            }
        }
        return START_STICKY;
    }
}
