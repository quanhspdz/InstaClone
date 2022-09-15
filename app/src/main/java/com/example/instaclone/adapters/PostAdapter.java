package com.example.instaclone.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.R;
import com.example.instaclone.models.Post;
import com.example.instaclone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> listPosts;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> listPosts) {
        this.context = context;
        this.listPosts = listPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Post post = listPosts.get(position);
        Picasso.get().load(post.getImageUrl()).into(holder.imgPost);
        holder.txtDescription.setText(post.getDescriptions());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;

                if (user.getImageUrl().equals("default")) {
                    holder.imgProfile.setImageResource(R.drawable.instagram);
                } else {
                    Picasso.get().load(user.getImageUrl()).into(holder.imgProfile);
                }
                holder.txtProfileName.setText(user.getUsername());
                holder.txtAuthor.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgProfile;
        public ImageView imgPost;
        public ImageView btnLike;
        public ImageView btnComment;
        public ImageView btnSave;
        public ImageView btnMore;

        public TextView txtProfileName;
        public TextView txtNumOfLikes;
        public TextView txtNumOfComments;
        public TextView txtAuthor;
        SocialTextView txtDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.img_profile);
            imgPost = itemView.findViewById(R.id.img_post);
            txtProfileName = itemView.findViewById(R.id.txt_author_profile);
            txtDescription = itemView.findViewById(R.id.txt_des);
            txtAuthor = itemView.findViewById(R.id.txt_author);
        }
    }
}