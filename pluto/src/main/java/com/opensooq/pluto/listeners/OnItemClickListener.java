package com.opensooq.pluto.listeners;
/**
 * Created by Omar Altamimi on 28,April,2019
 */

public interface OnItemClickListener<T> {
    void onItemClicked(T item, int position);
}
