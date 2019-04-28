package com.opensooq.pluto.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.opensooq.pluto.listeners.OnItemClickListener;
/**
 * Created by Omar Altamimi on 28,April,2019
 */

public abstract class PlutoViewHolder<T> extends RecyclerView.ViewHolder {
    protected final Context mContext;
    private OnItemClickListener<T> mOnItemClickListener;
    public int mPosition = RecyclerView.NO_POSITION;
    private int realAdapterCount;

    /**
     * Views indexed with their IDs
     */
    private final SparseArray<View> views;

    void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    void setRealAdapterCount(int realAdapterCount) {
        this.realAdapterCount = realAdapterCount;
    }

    public PlutoViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent, false));
        this.views = new SparseArray<>();
        mContext = itemView.getContext();

        itemView.setOnClickListener(v -> {
            mPosition = getAdapterPosition();
            if (mOnItemClickListener == null || mPosition == RecyclerView.NO_POSITION) {
                return;
            }
            mPosition = mPosition % realAdapterCount;
            mOnItemClickListener.onItemClicked(getTag(), mPosition);
        });
    }


    public Context getContext() {
        return mContext;
    }


    public void setTag(T a) {
        itemView.setTag(a);
    }

    public abstract void set(T item, int position);

    public T getTag() {
        Object tag = itemView.getTag();
        if (tag != null) {
            return (T) tag;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }
}
