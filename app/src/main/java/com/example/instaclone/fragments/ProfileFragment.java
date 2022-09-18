package com.example.instaclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.R;
import com.example.instaclone.models.Post;
import com.example.instaclone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private TextView txtUsername, txtFullName, txtNumOfFollowers, txtNumOfPosts, txtNumOfFollowing;
    private TextView txtPosts, txtFollowers, txtFollowing;
    private CircleImageView imgProfile;
    private AppCompatButton btnEditProfile;
    private ImageButton btnMyPosts, btnSavedPosts;
    private RecyclerView recyclerViewMyPost, recyclerViewSavedPosts;

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtUsername = view.findViewById(R.id.txt_username);
        txtFullName = view.findViewById(R.id.txt_fullName);
        txtNumOfFollowers = view.findViewById(R.id.txt_number_of_followers);
        txtNumOfPosts = view.findViewById(R.id.txt_number_of_posts);
        txtNumOfFollowing = view.findViewById(R.id.txt_number_of_following);
        txtPosts = view.findViewById(R.id.txt_posts);
        txtFollowers = view.findViewById(R.id.followers);
        txtFollowing = view.findViewById(R.id.following);
        imgProfile = view.findViewById(R.id.img_profile);
        btnEditProfile = view.findViewById(R.id.btn_editProfile);
        btnMyPosts = view.findViewById(R.id.btn_myPosts);
        btnSavedPosts = view.findViewById(R.id.btn_mySavedPosts);
        recyclerViewMyPost = view.findViewById(R.id.recycler_view_my_posts);
        recyclerViewSavedPosts = view.findViewById(R.id.recycler_view_saved_posts);

        userId = FirebaseAuth.getInstance().getUid();

        setUserProfileData();


        return view;
    }

    private void setUserProfileData() {
        //firstly, get basic user info from Firebase
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        //now set the profile
                        assert user != null;
                        txtUsername.setText(user.getUsername());
                        txtFullName.setText(user.getName());
                        if (user.getImageUrl().equals("default")) {
                           imgProfile.setImageResource(R.drawable.instagram);
                        } else {
                            Picasso.get().load(user.getImageUrl()).into(imgProfile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

       //get number of followers
       FirebaseDatabase.getInstance().getReference().child("Follow")
               .child(userId).child("followers").addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       int numberOfFollowers = 0;
                       numberOfFollowers = (int) snapshot.getChildrenCount();

                       txtNumOfFollowers.setText(numberOfFollowers + "");
                       if (numberOfFollowers > 1) {
                           txtFollowers.setText("Followers");
                       } else {
                           txtFollowers.setText("Follower");
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
        //get number of following
        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(userId).child("following").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int numberOfFollowing = 0;
                        numberOfFollowing = (int) snapshot.getChildrenCount();
                        txtNumOfFollowing.setText(numberOfFollowing + "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //get number of posts
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int numberOfPosts = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            assert post != null;
                            if (post.getPublisher().equals(userId)) {
                                numberOfPosts++;
                            }
                        }
                        txtNumOfPosts.setText(numberOfPosts + "");

                        if (numberOfPosts > 1) {
                            txtPosts.setText("Posts");
                        } else {
                            txtPosts.setText("Post");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}