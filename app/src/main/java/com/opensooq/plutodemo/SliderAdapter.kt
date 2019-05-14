package com.opensooq.plutodemo

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.base.PlutoViewHolder
import com.opensooq.pluto.listeners.OnItemClickListener

class SliderAdapter(items: MutableList<Movie>, onItemClickListener: OnItemClickListener<Movie>) : PlutoAdapter<Movie, SliderAdapter.ViewHolder>(items, onItemClickListener) {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, R.layout.item_movie_promotion)
    }

    class ViewHolder(parent: ViewGroup, itemLayoutId: Int) : PlutoViewHolder<Movie>(parent, itemLayoutId) {
        private var ivPoster: ImageView = getView(R.id.iv_poster)
        private var tvRating: TextView = getView(R.id.tv_rating)

        override fun set(item: Movie, position: Int) {
            Glide.with(context).load(item.posterId).into(ivPoster)
            tvRating.text = item.imdbRating
        }
    }
}
