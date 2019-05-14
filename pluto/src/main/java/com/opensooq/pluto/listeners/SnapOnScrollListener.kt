package com.opensooq.pluto.listeners

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper

/**
 * Created by Omar Altamimi on 28,April,2019
 */

class SnapOnScrollListener(private var mSnapHelper: SnapHelper, private var mOnSnapPositionChangeListener: OnSnapPositionChangeListener) : RecyclerView.OnScrollListener() {
    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val snapPosition = mSnapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = this.snapPosition != snapPosition && snapPosition != RecyclerView.NO_POSITION
        if (snapPositionChanged) {
            mOnSnapPositionChangeListener
                    .onSnapPositionChange(snapPosition)
            this.snapPosition = snapPosition
        }

    }
}
