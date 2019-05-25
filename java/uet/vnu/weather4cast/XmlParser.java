package uet.vnu.weather4cast;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

public class XmlParser extends AsyncTask<Void, Integer, HashMap>
{
    public static final int GET_WEATHER_MAP = 1;
    public static final int GET_ICON_MAP = 2;

    private XmlResourceParser parser;
    private Context context;
    private HashMap weatherMap, iconMap;
    private ProgressBar progressBar;
    private TextView progressStatus;

    private int mode;

    /* constructor */
    public XmlParser(Context _context, int _mode)
    {
        this.context = _context;
        this.parser = context.getResources().getXml(R.xml.weather_code);
        this.weatherMap = new HashMap();
        this.iconMap = new HashMap();
        this.mode = _mode;
    }

    @Override
    protected HashMap<Integer, String> doInBackground(Void... voids)
    {
        int eventType = 0;
        int key = 0;
        String value = "";

        parser = context.getResources().getXml(R.xml.weather_code);
        try
        {
            eventType = parser.getEventType();
        }
        catch (XmlPullParserException e)
        {
            e.printStackTrace();
        }
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            String tagName = parser.getName();
            switch (eventType)
            {
                case XmlPullParser.START_TAG:
                {
                    if (tagName.equals("weather"))
                    {
                        key = Integer.parseInt(parser.getAttributeValue(null, "name"));
                    }
                    break;
                }

                case XmlPullParser.TEXT:
                {
                    value = parser.getText();
                    break;
                }

                case XmlPullParser.END_TAG:
                {
                    if(tagName.equalsIgnoreCase("title"))
                    {
                        weatherMap.put(key, value);
                    }
                    else if(tagName.equalsIgnoreCase("icon"))
                    {
                        iconMap.put(key, value);
                    }
                }
            }
            try
            {
                eventType = parser.next();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (XmlPullParserException e)
            {
                e.printStackTrace();
            }
        }
        parser.close();

        if(mode == XmlParser.GET_WEATHER_MAP)
        {
            return weatherMap;
        }
        /* mode == XmlParser.GET_ICON_MAP */
        else
        {
            return iconMap;
        }
    }

}
