package com.example.instaclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.instaclone.fragments.AddFragment;
import com.example.instaclone.fragments.HomeFragment;
import com.example.instaclone.fragments.NotificationFragment;
import com.example.instaclone.fragments.ProfileFragment;
import com.example.instaclone.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottom_nav;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_nav = findViewById(R.id.bottom_nav);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        bottom_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_home: {
                        selectorFragment = new HomeFragment();
                        break;
                    }
                    case R.id.nav_search: {
                        selectorFragment = new SearchFragment();
                        break;
                    }
                    case R.id.nav_add: {
                        selectorFragment = new AddFragment();
                        Intent intent = new Intent(MainActivity.this, PostActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.nav_heart: {
                        selectorFragment = new NotificationFragment();
                        break;
                    }
                    case R.id.nav_profile: {
                        selectorFragment = new ProfileFragment();
                        break;
                    }
                }
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }

                return true;
            }
        });
    }
}