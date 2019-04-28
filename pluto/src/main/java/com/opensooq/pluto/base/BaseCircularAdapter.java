package com.opensooq.pluto.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.opensooq.pluto.listeners.OnItemClickListener;

import java.util.List;

/**
 * Created by Omar Altamimi on 28,April,2019
 */

public abstract class BaseCircularAdapter<T, VH extends BaseCircularViewHolder<T>> extends RecyclerView.Adapter<VH> {
    private List<T> items;
    public static final int MULTIPLY = 400;
    OnItemClickListener<T> mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public BaseCircularAdapter(List<T> items, OnItemClickListener<T> onItemClickListener) {
        this.items = items;
        mOnItemClickListener = onItemClickListener;
    }

    public BaseCircularAdapter(List<T> items) {
        this.items = items;
    }

    protected T getItem(int realPosition) {
        return items.get(realPosition % getRealCount());
    }

    public List<T> getList() {
        return items;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size() == 1 ? 1 : Integer.MAX_VALUE;
    }

    public int getRealCount() {
        return items == null ? 0 : items.size();
    }


    public void removeAllItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void removeItemAt(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public abstract VH getViewHolder(ViewGroup parent, int viewType);

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VH viewHolder = getViewHolder(parent, viewType);
        viewHolder.setRealAdapterCount(getRealCount());
        viewHolder.setOnItemClickListener(mOnItemClickListener);
        return viewHolder;
    }

    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position) {
        position = position % getRealCount();
        holder.mPosition = position;
        holder.setRealAdapterCount(getRealCount());
        holder.setTag(items.get(position));
        holder.set(items.get(position), position);
    }
}
