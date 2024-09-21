package com.example.dialekto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment fragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        chipNavigationBar = findViewById(R.id.bottom_nav);
        chipNavigationBar.setItemSelected(R.id.bottom_nav, true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();



        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;

                switch(i){
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_voice:
                        fragment = new VoiceFragment();
                        break;
                    case R.id.nav_camera:
                        fragment = new CameraFragment();
                        break;
                    case R.id.nav_fave:
                        fragment = new FavoriteFragment();
                        break;
                    default:
                        // Log or handle the unexpected case
                        Log.e("MainActivity", "Unknown item ID selected: " + i);
                        return; // Exit the method if fragment is null
                }
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                } else {
                    Log.e("MainActivity", "Fragment is null for item ID: " + i);
                }
            }



        });
    }
}