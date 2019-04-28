package com.opensooq.pluto;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.opensooq.pluto.base.PlutoAdapter;
import com.opensooq.pluto.base.PlutoViewHolder;
import com.opensooq.pluto.listeners.OnItemClickListener;

import java.util.List;

public class SliderAdapter extends PlutoAdapter<Movie, SliderAdapter.ViewHolder> {

    public SliderAdapter(List<Movie> items, OnItemClickListener<Movie> onItemClickListener) {
        super(items, onItemClickListener);
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_movie_promotion);
    }

    public static class ViewHolder extends PlutoViewHolder<Movie> {
        ImageView ivPoster;
        TextView tvRating;

        public ViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivPoster = getView(R.id.iv_poster);
            tvRating = getView(R.id.tv_rating);
        }

        @Override
        public void set(Movie item, int pos) {
            Glide.with(mContext).load(item.getPosterId()).into(ivPoster);
            tvRating.setText(item.getImdbRating());
        }
    }
}
