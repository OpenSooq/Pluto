package com.opensooq.pluto.listeners

import com.opensooq.pluto.base.PlutoAdapter

/**
 * Created by Omar Altamimi on 28,April,2019
 */

interface OnSlideChangeListener {
    fun onSlideChange(adapter: PlutoAdapter<*, *>, position: Int)
}
