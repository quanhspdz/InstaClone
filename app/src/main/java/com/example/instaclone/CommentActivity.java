package com.example.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instaclone.R;
import com.example.instaclone.adapters.CommentAdapter;
import com.example.instaclone.models.Comment;
import com.example.instaclone.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private EditText edtComment;
    private CircleImageView imgProfile;
    private TextView txtPost;
    private RecyclerView recyclerViewComments;
    private List<Comment> listComments;
    private CommentAdapter commentAdapter;
    private ImageView btnBack;

    private String authorId, postId;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //receive authorId and postId from HomeFragment
        Intent intent = getIntent();
        authorId = intent.getStringExtra("authorId");
        postId = intent.getStringExtra("postId");

        edtComment = findViewById(R.id.edt_comment);
        imgProfile = findViewById(R.id.img_profile);
        txtPost = findViewById(R.id.btn_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //setup back button
        btnBack = findViewById(R.id.layout_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //setup recyclerView to show comments
        recyclerViewComments = findViewById(R.id.recyclerView_comments);
        recyclerViewComments.setHasFixedSize(true);
        listComments = new ArrayList<>();
        commentAdapter = new CommentAdapter(getApplicationContext(), listComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewComments.setAdapter(commentAdapter);
        showAllComments();

        //change color of the Post button to blur when comment is empty
        blurPostButton();

        //get current user info
        getCurrentUserInfo();

        //when user hit Post button
        txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtComment.getText().toString().isEmpty()) {

                } else {
                    postThisComment(edtComment.getText().toString());
                }
            }
        });
    }

    private void showAllComments() {
        //firstly, let get all comments of this post from Firebase
        FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listComments.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            listComments.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                        recyclerViewComments.smoothScrollToPosition(listComments.size() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void postThisComment(String comment) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("comment", comment);
        data.put("publisher", firebaseUser.getUid());
        FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(postId).push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            edtComment.setText("");
                        } else {
                            Toast.makeText(CommentActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void getCurrentUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;

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
    }

    public void blurPostButton() {
        edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtComment.getText().toString().isEmpty()) {
                    txtPost.setTextColor(getResources().getColor(R.color.blur_blue));
                } else {
                    txtPost.setTextColor(getResources().getColor(R.color.blue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}