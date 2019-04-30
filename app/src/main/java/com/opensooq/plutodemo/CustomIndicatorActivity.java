package com.opensooq.plutodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.opensooq.pluto.Pluto;

import java.util.ArrayList;
import java.util.List;

public class CustomIndicatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_indicator);

        Pluto pluto = findViewById(R.id.slider_view);
        InfinityGauntletAdapter adapter = new InfinityGauntletAdapter(getGifs(), (item, position) -> {

        });
        pluto.create(adapter, 4000);
        pluto.setCustomIndicator(findViewById(R.id.infinity_gauntlet_indicator));

    }


    private List<Gif> getGifs() {
        List<Gif> items = new ArrayList<>();
        items.add(new Gif("https://i.giphy.com/media/Q8auEgoR7x0CcgH4uQ/giphy.gif"));
        items.add(new Gif("https://i.giphy.com/media/8nmb8m82jbLfa/giphy.gif"));
        items.add(new Gif("https://i.giphy.com/media/61XRgiopTwXYda9sTX/giphy.gif"));
        items.add(new Gif("https://i.giphy.com/media/AiEr9b7sX5VKIoIvQL/giphy.gif"));

        return items;
    }
}
