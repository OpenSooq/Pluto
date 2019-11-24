package com.opensooq.pluto

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.DisplayMetrics


class PlutoLayoutManager : LinearLayoutManager {
    var speed: Int = DEFAULT_SPEED

    constructor(context: Context, speed: Int = DEFAULT_SPEED) : super(context) {
        this.speed = speed
    }

    constructor(context: Context, orientation: Int, reverseLayout: Boolean, speed: Int = DEFAULT_SPEED) : super(context, orientation, reverseLayout) {
        this.speed = speed
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int, speed: Int = DEFAULT_SPEED) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.speed = speed
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (ignored: IndexOutOfBoundsException) {
        }
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                val res = 2F*super.calculateSpeedPerPixel(displayMetrics)
                return speed/10F*res
            }
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    override fun supportsPredictiveItemAnimations(): Boolean {
        return true
    }

    companion object {
        const val DEFAULT_SPEED = 5
    }
}