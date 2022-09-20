package com.example.instaclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instaclone.R;
import com.example.instaclone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    ImageButton btnCancel, btnDone;
    CircleImageView imgProfile;
    TextView txtChangeProfile;
    EditText edtName, edtUsername;

    String userId;
    Uri profileImageUri;
    User currentUser;
    Boolean photoIsChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btnCancel = findViewById(R.id.btn_cancel);
        btnDone = findViewById(R.id.btn_done);
        imgProfile = findViewById(R.id.img_profile);
        txtChangeProfile = findViewById(R.id.txt_change_profile);
        edtName = findViewById(R.id.edtName);
        edtUsername = findViewById(R.id.edtUserName);

        userId = FirebaseAuth.getInstance().getUid();
        photoIsChanged = false;

        loadCurrentUserInfo();
        setListener();

    }

    private void loadCurrentUserInfo() {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        currentUser = snapshot.getValue(User.class);

                        assert currentUser != null;
                        edtName.setText(currentUser.getName());
                        edtUsername.setText(currentUser.getUsername());
                        if (currentUser.getImageUrl().equals("default")) {
                            imgProfile.setImageResource(R.drawable.instagram);
                        } else {
                            Picasso.get().load(currentUser.getImageUrl()).into(imgProfile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(EditProfileActivity.this);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForAnyChanges();
            }
        });
    }

    private Boolean checkForAnyChanges() {
        String newName = edtName.getText().toString();
        String newUsername = edtUsername.getText().toString();
        if (newName.isEmpty()) {
            Toast.makeText(this, "Your name can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (newUsername.isEmpty()) {
            Toast.makeText(this, "Your username can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (!(currentUser.getName().equals(newName))) {
                return true;
            } else if (!(currentUser.getUsername().equals(newUsername))) {
                return true;
            } else if (photoIsChanged) {
                return true;
            }
        }

        Toast.makeText(this, "Nothing change!", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            profileImageUri = result.getUri();
            imgProfile.setImageURI(profileImageUri);
            photoIsChanged = true;
        }
    }
}