package com.opensooq.plutodemo;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.opensooq.pluto.base.PlutoAdapter;
import com.opensooq.pluto.base.PlutoViewHolder;
import com.opensooq.pluto.listeners.OnItemClickListener;

import java.util.List;

public class InfinityGauntletAdapter extends PlutoAdapter<Gif,
        InfinityGauntletAdapter.ViewHolder> {

    public InfinityGauntletAdapter(List<Gif> items,
                                   OnItemClickListener<Gif> onItemClickListener) {
        super(items, onItemClickListener);
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_infinit_gauntlet);
    }

    public static class ViewHolder extends PlutoViewHolder<Gif> {
        ImageView ivPoster;


        public ViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            ivPoster = getView(R.id.iv_gif);

        }

        @Override
        public void set(Gif item, int pos) {
            Glide.with(mContext).asGif().load(item.getUrl()).into(ivPoster);

        }
    }
}