package com.opensooq.plutodemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.opensooq.pluto.PlutoView
import com.opensooq.pluto.listeners.OnItemClickListener

class CustomIndicatorActivity : AppCompatActivity() {


    private fun getGifs(): MutableList<Gif> {
        val items = mutableListOf<Gif>()
        items.add(Gif("https://i.giphy.com/media/Q8auEgoR7x0CcgH4uQ/giphy.gif"))
        items.add(Gif("https://i.giphy.com/media/8nmb8m82jbLfa/giphy.gif"))
        items.add(Gif("https://i.giphy.com/media/61XRgiopTwXYda9sTX/giphy.gif"))
        items.add(Gif("https://i.giphy.com/media/AiEr9b7sX5VKIoIvQL/giphy.gif"))

        return items
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_indicator)

        val pluto = findViewById<PlutoView>(R.id.slider_view)
        val adapter = InfinityGauntletAdapter(getGifs(), object : OnItemClickListener<Gif> {
            override fun onItemClicked(item: Gif?, position: Int) {
                Log.d(TAG, "on Item clicked $position ${item?.url}")
            }

        })
        pluto.create(adapter, 4000, lifecycle)
        pluto.setCustomIndicator(findViewById(R.id.infinity_gauntlet_indicator))

    }
}
