package com.example.instaclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.R;
import com.example.instaclone.models.Comment;
import com.example.instaclone.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Comment> listComments;

    public CommentAdapter(Context context, List<Comment> listComments) {
        this.context = context;
        this.listComments = listComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment =  listComments.get(position);
        holder.txtComment.setText(comment.getComment());

        //now get publisher's info from publisherId -> set profile image
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        assert user != null;
                        holder.txtUsername.setText(user.getUsername());
                        if (user.getImageUrl().equals("default")) {
                            holder.imgProfile.setImageResource(R.drawable.instagram);
                        } else {
                            Picasso.get().load(user.getImageUrl()).into(holder.imgProfile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return listComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imgProfile;
        public TextView txtUsername;
        public TextView txtComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.img_userProfile);
            txtComment = itemView.findViewById(R.id.txt_comment);
            txtUsername = itemView.findViewById(R.id.txt_username);
        }
    }
}
