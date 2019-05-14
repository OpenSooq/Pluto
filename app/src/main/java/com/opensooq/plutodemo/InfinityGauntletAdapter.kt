package com.opensooq.plutodemo

import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.base.PlutoViewHolder
import com.opensooq.pluto.listeners.OnItemClickListener

class InfinityGauntletAdapter(items: MutableList<Gif>,
                              onItemClickListener: OnItemClickListener<Gif>) : PlutoAdapter<Gif, InfinityGauntletAdapter.ViewHolder>(items, onItemClickListener) {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, R.layout.item_infinit_gauntlet)
    }

    class ViewHolder(parent: ViewGroup, itemLayoutId: Int) : PlutoViewHolder<Gif>(parent, itemLayoutId) {
        internal var ivPoster: ImageView = getView(R.id.iv_gif)

        override fun set(item: Gif, pos: Int) {
            Glide.with(context).asGif().load(item.url).into(ivPoster)

        }
    }
}