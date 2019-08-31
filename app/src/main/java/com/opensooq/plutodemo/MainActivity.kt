package com.opensooq.plutodemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout

import com.opensooq.pluto.PlutoView
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.listeners.OnItemClickListener
import com.opensooq.pluto.listeners.OnSlideChangeListener

class MainActivity : AppCompatActivity() {

    private fun getAvenger(): MutableList<Movie> {
        val items = mutableListOf<Movie>()
        items.add(Movie("7.1", R.drawable.ic_captain_marvel))
        items.add(Movie("9.2", R.drawable.ic_end_game))
        items.add(Movie("7.5", R.drawable.ic_dr_strange))
        items.add(Movie("7.9", R.drawable.ic_iron_man))
        return items
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pluto = findViewById<PlutoView>(R.id.slider_view)
        val adapter = SliderAdapter(getAvenger(), object : OnItemClickListener<Movie> {
            override fun onItemClicked(item: Movie?, position: Int) {
            }
        })

        pluto.create(adapter, lifecycle =  lifecycle)
        pluto.setOnSlideChangeListener(object : OnSlideChangeListener {
            override fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int) {

            }
        })

        val button = findViewById<LinearLayout>(R.id.indicator_example)
        button.setOnClickListener {
            startActivity(Intent(this, CustomIndicatorActivity::class.java))
        }

    }
}
