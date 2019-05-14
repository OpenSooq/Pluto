package com.opensooq.pluto.base

import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.opensooq.pluto.listeners.OnItemClickListener

/**
 * Created by Omar Altamimi on 28,April,2019
 */

abstract class PlutoViewHolder<T>(parent: ViewGroup, @LayoutRes itemLayoutId: Int) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)) {
    val context: Context = itemView.context
    private var mOnItemClickListener: OnItemClickListener<T>? = null
    internal var mPosition = RecyclerView.NO_POSITION
    private var realAdapterCount: Int = 0

    /**
     * Views indexed with their IDs
     */
    private val views: SparseArray<View> = SparseArray()

    var tag: T?
        get() {
            val tag = itemView.tag
            return if (tag != null) {
                tag as T?
            } else null
        }
        set(a) {
            itemView.tag = a
        }

    internal fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>?) {
        mOnItemClickListener = onItemClickListener
    }

    internal fun setRealAdapterCount(realAdapterCount: Int) {
        this.realAdapterCount = realAdapterCount
    }

    init {

        itemView.setOnClickListener {
            mPosition = adapterPosition
            if (mOnItemClickListener == null || mPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            mPosition %= realAdapterCount
            mOnItemClickListener?.onItemClicked(tag, mPosition)
        }
    }

    abstract operator fun set(item: T, position: Int)

    fun <T : View> getView(@IdRes viewId: Int): T {
        var view: View? = views.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T
    }
}
