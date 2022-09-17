package com.example.instaclone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instaclone.MainActivity;
import com.example.instaclone.R;
import com.example.instaclone.adapters.PostAdapter;
import com.example.instaclone.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPosts, recyclerViewStories;
    private PostAdapter postAdapter;
    private List<Post> listPosts;
    private ImageView imgLogo;
    public static NestedScrollView nestedScrollView;
    private BottomNavigationView bottom_nav;
    LinearLayoutManager linearLayoutManager;

    HashMap<String, Boolean> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imgLogo = view.findViewById(R.id.imgLogo);
        nestedScrollView = view.findViewById(R.id.nest_view);
        recyclerViewPosts = view.findViewById(R.id.recycler_view_posts);
        recyclerViewPosts.setHasFixedSize(true);
//        recyclerViewStories = view.findViewById(R.id.recycler_view_stories);
//        recyclerViewStories.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPosts.setLayoutManager(linearLayoutManager);

        listPosts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), listPosts);
        recyclerViewPosts.setAdapter(postAdapter);

        followingList = new HashMap<>();

        checkFollowingUsers();

        //recycler view scroll to top
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nestedScrollView.smoothScrollTo(0, 0);
            }
        });

        return view;
    }

    private void checkFollowingUsers() {
        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        followingList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            followingList.put(dataSnapshot.getKey(), true);
                        }

                        readPost();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void readPost() {
        FirebaseDatabase.getInstance().getReference().child("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listPosts.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            assert post != null;
                            //just showing the posts of whom following by current user
                            if (followingList.containsKey(post.getPublisher())
                                || TextUtils.equals(post.getPublisher(),
                                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                                listPosts.add(post);
                            }
//                            listPosts.add(post);
                        }

                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}