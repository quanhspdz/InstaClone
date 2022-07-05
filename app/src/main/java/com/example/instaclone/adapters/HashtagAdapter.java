package com.example.instaclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.R;

import java.util.List;

public class HashtagAdapter extends RecyclerView.Adapter<HashtagAdapter.ViewHolder>{

    private Context mContext;
    private List<String> mTags;
    private List<String> mTagsCount;

    public HashtagAdapter(Context mContext, List<String> mTags, List<String> mTagsCount) {
        this.mContext = mContext;
        this.mTags = mTags;
        this.mTagsCount = mTagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.each_tag_item, parent, false);
        return new HashtagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtHashtag.setText(mTags.get(position));
        holder.txtNumberOfPosts.setText(mTagsCount.get(position));
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtHashtag, txtNumberOfPosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtHashtag = itemView.findViewById(R.id.txtHashtag);
            txtNumberOfPosts = itemView.findViewById(R.id.txtNumberOfPosts);
        }
    }

    public void filter(List<String> listSearchTags, List<String> listSearchTagsCount) {
        this.mTags = listSearchTags;
        this.mTagsCount = listSearchTagsCount;

        notifyDataSetChanged();
    }
}
