package com.example.instaclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instaclone.R;
import com.example.instaclone.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    ImageButton btnCancel, btnDone;
    CircleImageView imgProfile;
    TextView txtChangeProfile;
    EditText edtName, edtUsername, edtBio;

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
        edtBio = findViewById(R.id.edtBio);

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
                            Picasso.get().load(currentUser.getImageUrl())
                                    .resize(1000, 1000)
                                    .centerCrop()
                                    .into(imgProfile);
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
                if (checkForAnyChanges()) {
                    updateData(profileImageUri,
                            edtName.getText().toString(),
                            edtUsername.getText().toString(),
                            edtBio.getText().toString());
                }
            }
        });
    }

    private Boolean checkForAnyChanges() {
        String newName = edtName.getText().toString();
        String newUsername = edtUsername.getText().toString();
        String newBio = edtBio.getText().toString();
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

    private void updateData(Uri imgUri, String newName, String newUsername, String newBio) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (photoIsChanged) {
            //get image name & extension
            StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts")
                    .child(System.currentTimeMillis() + "." + getFileExtension(imgUri));

            //get image url
            StorageTask uploadTask = filePath.putFile(imgUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    //upload post
                    Uri downloadUri = (Uri) task.getResult();
                    String imgUrl = downloadUri.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference().child("Users")
                            .child(userId);

                    User user = getUpdateUser();
                    user.setImageUrl(imgUrl);
                    reference.setValue(user);

                    finish();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("Users")
                    .child(userId);

            User user = getUpdateUser();
            reference.setValue(user);

            finish();
            progressDialog.dismiss();
        }
    }

    private User getUpdateUser() {
        String newName = edtName.getText().toString();
        String newUsername = edtUsername.getText().toString();
        String newBio = edtBio.getText().toString();

        if (!newName.isEmpty()) {
            currentUser.setName(newName);
        }
        if (!newUsername.isEmpty()) {
            currentUser.setUsername(newUsername);
        }
        currentUser.setBio("" + newBio);

        return currentUser;
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));
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