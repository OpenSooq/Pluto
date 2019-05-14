package com.opensooq.pluto.base

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import com.opensooq.pluto.listeners.OnItemClickListener

/**
 * Created by Omar Altamimi on 28,April,2019
 */

abstract class PlutoAdapter<T, VH : PlutoViewHolder<T>>(var items: MutableList<T>,
                                                        private var mOnItemClickListener: OnItemClickListener<T>?) : RecyclerView.Adapter<VH>() {


    val realCount: Int
        get() = items.size

    open fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>) {
        mOnItemClickListener = onItemClickListener
    }

    constructor(items: MutableList<T>) : this(items, null)

    /**
     * Get a item form adapter items
     * @param index index of item form adapter items
     * @return item from adapter list by specified index
     */
    protected fun getItem(index: Int): T {
        return items[index % realCount]
    }

    override fun getItemCount(): Int {
        return when {
            items.size == 0 -> 0
            items.size == 1 -> 1
            else -> Integer.MAX_VALUE
        }
    }

    /**
     * Remove all items from adapter
     */
    open fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    /**
     * Add new item to already existing adapter items
     * @param index index where to insert the item
     * @param item new adapter item
     */
    open fun add(index: Int = getlastItem(), item: T) {
        items.add(index, item)
        notifyItemInserted(index)
    }

    /**
     * Add new item to already existing adapter items
     * @param item new adapter item
     */
    open fun add(item: T) {
        items.add(item)
        notifyItemInserted(getlastItem())
    }

    private fun getlastItem(): Int {
        if (items.isEmpty())
            return 0
        return items.size - 1
    }

    /**
     * Replace only one item on a specific index.
     * @param index index of item form adapter items
     * @param item new adapter item
     */
    open operator fun set(index: Int, item: T) {
        items[index] = item
        notifyItemInserted(index)
    }

    /**
     * Remove a item from adapter items
     * @param index index of item form adapter items
     */
    open fun remove(index: Int) {
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    abstract fun getViewHolder(parent: ViewGroup, viewType: Int): VH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewHolder = getViewHolder(parent, viewType)
        viewHolder.setRealAdapterCount(realCount)
        viewHolder.setOnItemClickListener(mOnItemClickListener)
        return viewHolder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (realCount == 0)
            return
        var holderpos = position
        holderpos %= realCount
        holder.mPosition = holderpos
        holder.setRealAdapterCount(realCount)
        holder.tag = items[holderpos]
        holder.set(items[holderpos], holderpos)
    }

    companion object {
        const val MULTIPLY = 400
    }
}
