package uet.vnu.weather4cast;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/* This class is used to get JSON weather data from OpenWeather API */
public class GetWeatherDataTask extends AsyncTask<Void, Integer, String>
{
    private Context context;
    private String payload;
    private String url;
    private InputStream inputStream;

    /* Constructor */
    public GetWeatherDataTask(Context _context, String _url)
    {
        this.url = _url;
        this.context = _context;
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
    protected void onPreExecute()
    {
        super.onPreExecute();
        SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout)((Activity)context).findViewById(R.id.swipe_layout);
        swipeLayout.setRefreshing(true);
    }


    @Override
    protected String doInBackground(Void... voids)
    {
        publishProgress(0);
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

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        /* If published value == 0 => No Internet connection */
        super.onProgressUpdate(values);
        /* Button button = (Button)((Activity)context).findViewById(R.id.button); */
        if(values[0] == 0)
        {
            SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout)((Activity)context).findViewById(R.id.swipe_layout);
            swipeLayout.setRefreshing(true);
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout)((Activity)context).findViewById(R.id.swipe_layout);
        swipeLayout.setRefreshing(false);
        super.onPostExecute(result);
    }
}
