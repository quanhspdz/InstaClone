package com.example.instaclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.activities.CommentActivity;
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

        if (post.getImageUrl().equals("default")) {
            holder.imgPost.setImageResource(R.drawable.instagram);
        } else {
            Picasso.get().load(post.getImageUrl())
                    .resize(1000, 1000)
                    .centerCrop()
                    .into(holder.imgPost);
        }

        if (post.getDescriptions().equals("")) {
            holder.txtDescription.setVisibility(View.GONE);
        } else {
            holder.txtDescription.setVisibility(View.VISIBLE);
            holder.txtDescription.setText(post.getDescriptions());
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;

                if (user.getImageUrl().equals("default")) {
                    holder.imgProfile.setImageResource(R.drawable.instagram);
                } else {
                    Picasso.get().load(user.getImageUrl())
                            .resize(1000, 1000)
                            .centerCrop()
                            .into(holder.imgProfile);
                }
                holder.txtProfileName.setText(user.getUsername());
                holder.txtAuthor.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //defined "Like button"'s logic
        holder.btnLike.setTag("like");
        isLiked(holder.btnLike, post.getPostId());
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnLike.getTag().equals("like")) {   //if user has not liked this post yet
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        //setup textView to show number of likes
        FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(post.getPostId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int numberOfLikes = (int) snapshot.getChildrenCount();
                        if (numberOfLikes > 1) {
                            holder.txtNumOfLikes.setVisibility(View.VISIBLE);
                            holder.txtNumOfLikes.setText(numberOfLikes + " likes");
                        } else if (numberOfLikes == 1) {
                            holder.txtNumOfLikes.setVisibility(View.VISIBLE);
                            holder.txtNumOfLikes.setText(numberOfLikes + " like");
                        } else {
                            holder.txtNumOfLikes.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //setup Comment button
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getPublisher());
                context.startActivity(intent);
            }
        });

        //show number of comments
        FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(post.getPostId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int numberOfComments = (int) snapshot.getChildrenCount();
                        if (numberOfComments > 1) {
                            holder.txtNumOfComments.setVisibility(View.VISIBLE);
                            holder.txtNumOfComments.setText("View all " + numberOfComments + " comments");
                        } else if (numberOfComments == 1) {
                            holder.txtNumOfComments.setVisibility(View.VISIBLE);
                            holder.txtNumOfComments.setText("View all " + numberOfComments + " comment");
                        } else {
                            holder.txtNumOfComments.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //click to number of comment textView also lead to Comment Activity
        holder.txtNumOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("authorId", post.getPublisher());
                context.startActivity(intent);
            }
        });

        //setup Save Button
        holder.btnSave.setTag("save");
        isSaved(holder.btnSave, post.getPostId());
        setSaveButtonListener(holder.btnSave, post.getPostId());

    }

    private void isSaved(ImageView btnSave, String postId) {
        //this method check if the post is already saved
        FirebaseDatabase.getInstance().getReference()
                .child("SavedPosts").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(postId).exists()) {
                            //this mean this post has been already saved by user
                            btnSave.setTag("saved");
                            btnSave.setImageResource(R.drawable.ic_saved);
                        } else {
                            btnSave.setTag("save");
                            btnSave.setImageResource(R.drawable.ic_save);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setSaveButtonListener(ImageView btnSave, String postId) {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSave.getTag().equals("saved")) {
                    //this mean user want to unsaved this post
                    FirebaseDatabase.getInstance().getReference()
                            .child("SavedPosts")
                            .child(firebaseUser.getUid())
                            .child(postId).removeValue();
                } else {
                    //this mean user want to save this post
                    FirebaseDatabase.getInstance().getReference()
                            .child("SavedPosts")
                            .child(firebaseUser.getUid())
                            .child(postId).setValue(true);
                }
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
            btnLike = itemView.findViewById(R.id.btn_like);
            btnComment = itemView.findViewById(R.id.btn_comment);
            btnSave = itemView.findViewById(R.id.btn_save_post);
            btnMore = itemView.findViewById(R.id.btn_more);
            txtNumOfComments = itemView.findViewById(R.id.number_of_comments);
            txtNumOfLikes = itemView.findViewById(R.id.number_of_likes);
        }
    }

    public void isLiked(ImageView btnLike, String postId) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(firebaseUser.getUid()).exists()) {
                            btnLike.setImageResource(R.drawable.ic_liked);
                            btnLike.setTag("liked");
                        } else {
                            btnLike.setImageResource(R.drawable.ic_like);
                            btnLike.setTag("like");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
