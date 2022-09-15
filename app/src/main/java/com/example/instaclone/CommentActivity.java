package com.example.instaclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.example.instaclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private EditText edtComment;
    private CircleImageView imgProfile;
    private TextView txtPost;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        edtComment = findViewById(R.id.edt_comment);
        imgProfile = findViewById(R.id.img_profile);
        txtPost = findViewById(R.id.btn_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //change color of the Post button to blur when comment is empty
        blurPostButton();
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