package com.example.instaclone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.arch.core.executor.DefaultTaskExecutor;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.R;
import com.example.instaclone.activities.EditProfileActivity;
import com.example.instaclone.adapters.GridPostAdapter;
import com.example.instaclone.models.Post;
import com.example.instaclone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private TextView txtUsername, txtFullName, txtNumOfFollowers, txtNumOfPosts, txtNumOfFollowing;
    private TextView txtPosts, txtFollowers, txtFollowing;
    private CircleImageView imgProfile;
    private AppCompatButton btnEditProfile;
    private ImageButton btnMyPosts, btnSavedPosts;
    private RecyclerView recyclerViewMyPost, recyclerViewSavedPosts;
    private FrameLayout frameMyPosts, frameSavedPosts;

    private GridPostAdapter myPostsAdapter, savedPostedAdapter;
    private List<Post> listMyPosts, listSavePosts;
    private GridLayoutManager gridLayoutManager;

    private HashMap<String, Post> mapSavedPostsId;
    private List<String> listSavedPostId;
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
        frameMyPosts = view.findViewById(R.id.frame_myPosts);
        frameSavedPosts = view.findViewById(R.id.frame_savedPosts);

        userId = FirebaseAuth.getInstance().getUid();

        listMyPosts = new ArrayList<>();
        listSavePosts = new ArrayList<>();
        listSavedPostId = new ArrayList<>();
        mapSavedPostsId = new HashMap<>();

        setupSwitchButton();

        setupRecyclerViewMyPosts();
        setUserProfileData();
        setEditProfileButtonListener();

        return view;
    }

    private void setEditProfileButtonListener() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupSwitchButton() {
        //firstly, it automatically show user's posts
        btnMyPosts.setTag("isChosen");
        btnMyPosts.setImageResource(R.drawable.ic_grid_selected);
        btnSavedPosts.setTag("isNotChosen");
        btnSavedPosts.setImageResource(R.drawable.ic_save_blur);

        //when user tap 1 of 2 switch buttons highlight it and blur the other
        frameMyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnMyPostsIsChosen();
            }
        });
        frameSavedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnSavedPostIsChosen();
            }
        });

        //surprise! this seem a dumb duplicated but it will set all view in frameLayout onClickListener
        btnMyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnMyPostsIsChosen();
            }
        });
        btnSavedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnSavedPostIsChosen();
            }
        });
    }

    private void onBtnMyPostsIsChosen() {
        btnMyPosts.setTag("isChosen");
        btnMyPosts.setImageResource(R.drawable.ic_grid_selected);
        btnSavedPosts.setTag("isNotChosen");
        btnSavedPosts.setImageResource(R.drawable.ic_save_blur);

        recyclerViewMyPost.setVisibility(View.VISIBLE);
        recyclerViewSavedPosts.setVisibility(View.GONE);
    }

    private void onBtnSavedPostIsChosen() {
        btnSavedPosts.setTag("isChosen");
        btnSavedPosts.setImageResource(R.drawable.ic_save);
        btnMyPosts.setTag("isNotChosen");
        btnMyPosts.setImageResource(R.drawable.ic_my_posts);

        recyclerViewSavedPosts.setVisibility(View.VISIBLE);
        recyclerViewMyPost.setVisibility(View.GONE);

        setupRecyclerViewSavedPosts();
    }

    private void setupRecyclerViewSavedPosts() {
        savedPostedAdapter = new GridPostAdapter(getContext(), listSavePosts);
        recyclerViewSavedPosts.setHasFixedSize(true);
        recyclerViewSavedPosts.setAdapter(savedPostedAdapter);
        gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerViewSavedPosts.setLayoutManager(gridLayoutManager);

        //get list of saved posts id from Firebase
        mapSavedPostsId.clear();
        listSavedPostId.clear();
        FirebaseDatabase.getInstance().getReference()
                .child("SavedPosts")
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            mapSavedPostsId.put(dataSnapshot.getKey(), null);
                            listSavedPostId.add(dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //now from list of postId get post data from Firebase
        listSavePosts.clear();
        FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (mapSavedPostsId.containsKey(dataSnapshot.getKey())) {
                                Post post = dataSnapshot.getValue(Post.class);
                                listSavePosts.add(post);
                            }
                        }
                        savedPostedAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setupRecyclerViewMyPosts() {
        myPostsAdapter = new GridPostAdapter(getContext(), listMyPosts);
        recyclerViewMyPost.setHasFixedSize(true);
        recyclerViewMyPost.setAdapter(myPostsAdapter);
        gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        recyclerViewMyPost.setLayoutManager(gridLayoutManager);
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
                                listMyPosts.add(post);
                                numberOfPosts++;
                            }
                        }
                        txtNumOfPosts.setText(numberOfPosts + "");
                        reverseListOfPosts();
                        myPostsAdapter.notifyDataSetChanged();

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

    private void reverseListOfPosts() {
        List<Post> tempList = new ArrayList<>(listMyPosts);
        listMyPosts.clear();
        for(int i = tempList.size() - 1; i >= 0; i--) {
            listMyPosts.add(tempList.get(i));
        }
    }
}