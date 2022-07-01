package com.example.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView txtWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtWelcome = findViewById(R.id.txtWelcome);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
            Task<DataSnapshot> dataSnapshot = mRootRef.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get();

            dataSnapshot.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    Toast.makeText(MainActivity.this,  task.getResult().toString(), Toast.LENGTH_SHORT).show();
                    txtWelcome.setText(task.getResult().getValue().toString());

                    HashMap<String, Object> map = (HashMap<String, Object>) task.getResult().getValue();
                    String name = map.get("name").toString();
                    txtWelcome.setText("Welcome " + name);
                }
            });

        }

    }
}