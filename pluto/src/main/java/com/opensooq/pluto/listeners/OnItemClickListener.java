package com.opensooq.pluto.listeners;

public interface OnItemClickListener<T> {
    void onItemClicked(T item, int position);
}
