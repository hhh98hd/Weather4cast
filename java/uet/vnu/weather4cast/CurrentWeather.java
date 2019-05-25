package uet.vnu.weather4cast;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;

public class CurrentWeather extends JsonParser
{
    private Context context;
    private TextView tempTextView, humidTextView, pressTextView, visiTextView, windSpdTextView,
                     weatherTextView;
    private HashMap<Integer, String> weatherMap, iconMap;
    private ImageView imageView;

    /* constructor */
    public CurrentWeather(String _jsonStr, Context _context, HashMap _weatherMap, HashMap _iconMap) {
        super(_jsonStr);
        this.context = _context;
        this.weatherMap = _weatherMap;
        this.iconMap = _iconMap;

        /* Initialize TextView */
        tempTextView = (TextView) ((Activity) context).findViewById(R.id.text_temperature);
        humidTextView = (TextView) ((Activity) context).findViewById(R.id.text_humidity);
        pressTextView = (TextView) ((Activity) context).findViewById(R.id.text_pressure);
        visiTextView = (TextView) ((Activity) context).findViewById(R.id.text_visibility);
        windSpdTextView = (TextView) ((Activity) context).findViewById(R.id.text_wind_speed);
        weatherTextView = (TextView) ((Activity) context).findViewById(R.id.text_weather);
        imageView = (ImageView) ((Activity) context).findViewById(R.id.image_weather_icon);
    }

    private boolean isInNight()
    {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(((18 <= hour) && (hour <= 23)) || ((0 <= hour) && (hour <= 5)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void displayWeatherGraphic()
    {
        int id = this.getCurrentWeatherId();
        String drawableResId = iconMap.get(id);

        if(id == 800 || id == 801 || id == 500)
        {
            /* night mode */
            if(isInNight())
            {
                drawableResId += "_n";
            }
            /* day mode */
            else
            {
                drawableResId += "_d";
            }
            imageView.setImageResource(context.getResources().getIdentifier(drawableResId, "drawable", context.getPackageName()));
        }
        else
        {
            imageView.setImageResource(context.getResources().getIdentifier(drawableResId, "drawable", context.getPackageName()));
        }
    }

    public void displayWeatherInfo()
    {
        tempTextView.setText(Integer.toString(this.getCurrentTemperature()) + "°C");
        humidTextView.setText(Integer.toString(this.getCurrentHumidity()));
        pressTextView.setText(Integer.toString(this.getCurrentPressure()));
        visiTextView.setText(Integer.toString(this.getCurrentVisibility() / 1000));
        windSpdTextView.setText(Double.toString(this.getCurrentWindSpd()));
        String weather = weatherMap.get(this.getCurrentWeatherId());
        weatherTextView.setText(weather);
    }
}
