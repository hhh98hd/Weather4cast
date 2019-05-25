package uet.vnu.weather4cast;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser
{
    private String jsonStr;
    private JSONObject json;

    /* constructor */
    public JsonParser(String _jsonStr)
    {
        this.jsonStr = _jsonStr;
        try
        {
            json = new JSONObject(jsonStr);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public int getCurrentWeatherId()
    {
        JSONObject weather;
        JSONArray weatherArr;
        int weatherId = 0;

        try
        {
            weatherArr = json.getJSONArray("weather");
            weather = weatherArr.getJSONObject(0);
            weatherId = weather.getInt("id");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return weatherId;
    }

    public int getCurrentTemperature()
    {
        double tempK = 0;
        int tempC = 0;
        JSONObject main;

        try
        {
            main = json.getJSONObject("main");
            tempK = main.getDouble("temp");
            tempC = (int)(tempK - 273.15);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return tempC;
    }

    public int getCurrentHumidity()
    {
        int humid = 0;
        JSONObject main;

        try
        {
            main = json.getJSONObject("main");
            humid = main.getInt("humidity");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return humid;
    }

    public double getCurrentWindSpd()
    {
        double spd = 0;
        JSONObject wind;

        try
        {
            wind = json.getJSONObject("wind");
            spd = wind.getDouble("speed");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return spd;
    }

    public int getCurrentPressure()
    {
        int pressure = 0;
        JSONObject main;

        try
        {
            main = json.getJSONObject("main");
            pressure = main.getInt("pressure");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return pressure;
    }

    public int getCurrentVisibility()
    {
        int visibility = 0;

        try
        {
            visibility = json.getInt("visibility");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return  visibility;
    }

    public int getForecastWeatherId()
    {
        int weatherId = 0;

        try
        {
            weatherId = json.getJSONArray("list")
                            .getJSONObject(0)
                            .getJSONArray("weather")
                            .getJSONObject(0)
                            .getInt("id");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return weatherId;
    }

    public int getForecastMinTemp()
    {
        int temp = 0;
        return temp;
    }

    public int getForecastMaxTemp()
    {
        int temp = 0;
        return temp;
    }


}
