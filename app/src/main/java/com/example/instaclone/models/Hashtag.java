package com.example.instaclone.models;

import androidx.annotation.NonNull;

import com.hendraanggrian.appcompat.socialview.Hashtagable;

public class Hashtag implements Hashtagable {
    public Hashtag(String key, int childrenCount) {
    }

    @NonNull
    @Override
    public CharSequence getId() {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
