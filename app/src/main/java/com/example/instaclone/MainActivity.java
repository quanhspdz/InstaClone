package com.example.instaclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.instaclone.fragments.AddFragment;
import com.example.instaclone.fragments.HomeFragment;
import com.example.instaclone.fragments.NotificationFragment;
import com.example.instaclone.fragments.ProfileFragment;
import com.example.instaclone.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    public static BottomNavigationView bottom_nav;
    private Fragment selectorFragment;
    private Fragment homeFragment, searchFragment, addFragment,
            notificationFragment, profileFragment;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_nav = findViewById(R.id.bottom_nav);

        //create a fragment for each tab in memory, and saves them as a local variable in the activity:
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        addFragment = new AddFragment();
        notificationFragment = new NotificationFragment();
        profileFragment = new ProfileFragment();

        fragmentManager = getSupportFragmentManager();
        selectorFragment = homeFragment;

//        add all 5 fragments to the manager, but hide 4 of them, so only HomeFragment will be visible:
        fragmentManager.beginTransaction().add(R.id.fragment_container, searchFragment, "search")
                .hide(searchFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, addFragment, "add")
                .hide(addFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, notificationFragment, "notification")
                .hide(notificationFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, profileFragment, "profile")
                .hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment, "home").commit();

        //when user select bottom nav: hide current fragment and show selected fragment
        bottom_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_home: {
                        //when user click Home nav button in HomeFragment, it will reload this fragment
                        if (selectorFragment == homeFragment) {
                            HomeFragment.nestedScrollView.smoothScrollTo(0, 0);
                        } else {
                            fragmentManager.beginTransaction().hide(selectorFragment)
                                    .show(homeFragment).commit();
                            selectorFragment = homeFragment;
                        }
                        break;
                    }
                    case R.id.nav_search: {
                        fragmentManager.beginTransaction().hide(selectorFragment)
                                .show(searchFragment).commit();
                        selectorFragment = searchFragment;
                        break;
                    }
                    case R.id.nav_add: {
                        fragmentManager.beginTransaction().hide(selectorFragment)
                                .show(addFragment).commit();
                        selectorFragment = addFragment;
                        Intent intent = new Intent(MainActivity.this, PostActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.nav_heart: {
                        fragmentManager.beginTransaction().hide(selectorFragment)
                                .show(notificationFragment).commit();
                        selectorFragment = notificationFragment;
                        break;
                    }
                    case R.id.nav_profile: {
                        fragmentManager.beginTransaction().hide(selectorFragment)
                                .show(profileFragment).commit();
                        selectorFragment = profileFragment;
                        break;
                    }
                }
                return true;
            }
        });
    }
}