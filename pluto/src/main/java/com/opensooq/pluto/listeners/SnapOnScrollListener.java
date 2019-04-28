package com.opensooq.pluto.listeners;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
/**
 * Created by Omar Altamimi on 28,April,2019
 */

public class SnapOnScrollListener extends RecyclerView.OnScrollListener {
    private int snapPosition = RecyclerView.NO_POSITION;
    OnSnapPositionChangeListener mOnSnapPositionChangeListener;
    SnapHelper mSnapHelper;

    public SnapOnScrollListener(SnapHelper snapHelper, OnSnapPositionChangeListener onSnapPositionChangeListener) {
        mOnSnapPositionChangeListener = onSnapPositionChangeListener;
        mSnapHelper = snapHelper;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        int snapPosition = ListUtils.getSnapPosition(recyclerView, mSnapHelper);
        boolean snapPositionChanged = this.snapPosition != snapPosition&&snapPosition!= RecyclerView.NO_POSITION;
        if (snapPositionChanged) {
            mOnSnapPositionChangeListener
                    .onSnapPositionChange(snapPosition);
            this.snapPosition = snapPosition;
        }

    }
}
