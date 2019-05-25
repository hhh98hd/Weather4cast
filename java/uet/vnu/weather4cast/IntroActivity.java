package uet.vnu.weather4cast;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class IntroActivity extends AppCompatActivity
{
    private ImageView uetLogo;
    private TextView textView;
    private Intent intent;
    private XmlParser weatherIdParser, iconParser;
    private HashMap<Integer, String> weatherIdMap, weatherIconMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getSupportActionBar().hide();

        intent = new Intent(this, MainActivity.class);

        weatherIdParser = new XmlParser(IntroActivity.this, XmlParser.GET_WEATHER_MAP);
        iconParser = new XmlParser(IntroActivity.this, XmlParser.GET_ICON_MAP);

        weatherIdParser.execute();
        iconParser.execute();
        try
        {
            weatherIdMap = weatherIdParser.get();
            weatherIconMap = iconParser.get();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        textView = findViewById(R.id.text_intro);
        textView.setAlpha(0f);

        uetLogo = findViewById(R.id.image_uet);
        uetLogo.setAlpha(0f);
        uetLogo.animate().setListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                uetLogo.setAlpha(0f);
                textView.setText("Hoàng Huy Hoàng" + "\n" + "16020131 - UET - VNU" + "\n");
                textView.animate().setListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        Bundle extras = new Bundle();
                        extras.putSerializable("WEATHER_MAP", weatherIdMap);
                        extras.putSerializable("ICON_MAP", weatherIconMap);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                }).alphaBy(1f).setDuration(3800);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        }).alphaBy(1f).setDuration(3800);
    }
}
