package com.opensooq.pluto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Pluto pluto = findViewById(R.id.slider_view);
        SliderAdapter adapter = new SliderAdapter(getAvengers(), (item, position) -> {
        });
        pluto.create(adapter, 4000);
        LinearLayout button = findViewById(R.id.indicator_example);
        button.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,
                CustomIndicatorActivity.class)));
    }

    private List<Movie> getAvengers() {
        List<Movie> items = new ArrayList<>();
        items.add(new  Movie("7.1", R.drawable.ic_captain_marvel));
        items.add(new Movie("9.2", R.drawable.ic_end_game));
        items.add(new Movie("7.5", R.drawable.ic_dr_strange));
        items.add(new Movie("7.9", R.drawable.ic_iron_man));
        return items;
    }
}
