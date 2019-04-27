package com.opensooq.sliderview;

import android.support.annotation.DrawableRes;

public class Movie {
    private String imdbRating;
    @DrawableRes
    private int posterId;

    public Movie(String imdbRating, int posterId) {
        this.imdbRating = imdbRating;
        this.posterId = posterId;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public int getPosterId() {
        return posterId;
    }
}
