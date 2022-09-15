package com.example.instaclone.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instaclone.MainActivity;
import com.example.instaclone.R;
import com.example.instaclone.adapters.HashtagAdapter;
import com.example.instaclone.adapters.UserAdapter;
import com.example.instaclone.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    RecyclerView recyclerViewUsers, recyclerViewHashtags;
    SocialAutoCompleteTextView edtSearch;
    UserAdapter userAdapter;
    List<User> listUsers;
    List<String> listHashtags, listHashTagsCount;
    HashtagAdapter hashtagAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerViewUsers = view.findViewById(R.id.recyclerUsers);
        recyclerViewHashtags = view.findViewById(R.id.recyclerTags);
        edtSearch = view.findViewById(R.id.edtSearch);

        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewHashtags.setHasFixedSize(true);
        recyclerViewHashtags.setLayoutManager(new LinearLayoutManager(getContext()));

        listUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), listUsers, true);
        recyclerViewUsers.setAdapter(userAdapter);

        listHashtags = new ArrayList<>();
        listHashTagsCount = new ArrayList<>();
        hashtagAdapter = new HashtagAdapter(getContext(), listHashtags, listHashTagsCount);
        recyclerViewHashtags.setAdapter(hashtagAdapter);

        //when user haven't type any key yet, all users will appear
        readListUsers();

        //when user type something (key), show the users & hashtags match with user's key
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
                searchHashtag(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //when user haven't type any key yet, all hashtags will appear
        readListHashtags();

        return view;
    }

    private void searchHashtag(String key) {
        List<String> listSearchTags = new ArrayList<>();
        List<String> listSearchTagsCount = new ArrayList<>();

        for (String tag : listHashtags) {
            if (tag.toLowerCase().contains(key.toLowerCase())) {
                listSearchTags.add(tag);
                listSearchTagsCount.add(listHashTagsCount.get(listHashtags.indexOf(tag)));
            }
        }
        hashtagAdapter.filter(listSearchTags, listSearchTagsCount);
    }

    private void readListHashtags() {
        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(edtSearch.getText().toString())) {
                    listHashtags.clear();
                    listHashTagsCount.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        listHashtags.add("# " + dataSnapshot.getKey());
                        if (dataSnapshot.getChildrenCount() > 1) {
                            listHashTagsCount.add(dataSnapshot.getChildrenCount() + " posts");
                        } else {
                            listHashTagsCount.add(dataSnapshot.getChildrenCount() + " post");
                        }
                    }
                    hashtagAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUser(String key) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(key).endAt(key + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    listUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readListUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(edtSearch.getText().toString())) {
                    listUsers.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        assert user != null;
                        listUsers.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}