package com.opensooq.pluto.listeners;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
/**
 * Created by Omar Altamimi on 28,April,2019
 */

class ListUtils {
    public static int getSnapPosition(RecyclerView recyclerView, SnapHelper helper) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        View snapView = helper.findSnapView(layoutManager);
        if (layoutManager == null || snapView == null)
            return RecyclerView.NO_POSITION;
        return layoutManager.getPosition(snapView);
    }
}
