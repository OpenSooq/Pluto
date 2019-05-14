package com.opensooq.pluto.listeners

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper

/**
 * Created by omaraltamimi on 11,May,2019
 */
fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}