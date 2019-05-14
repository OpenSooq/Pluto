package com.opensooq.pluto.listeners

/**
 * Created by Omar Altamimi on 28,April,2019
 */

interface OnItemClickListener<T> {
    fun onItemClicked(item: T?, position: Int)
}
