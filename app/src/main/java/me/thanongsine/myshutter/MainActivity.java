package me.thanongsine.myshutter;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import me.thanongsine.myshutter.fragments.FragmentAbout;
import me.thanongsine.myshutter.fragments.FragmentPhotoList;
import me.thanongsine.myshutter.fragments.FragmentPhotoUpload;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectFragment(FragmentPhotoList.newInstance());

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.photo_list_action:
                        selectFragment(FragmentPhotoList.newInstance());
                        break;
                    case R.id.photo_upload_action:
                        selectFragment(FragmentPhotoUpload.newInstance());
                        break;
                    case R.id.about_action:
                        selectFragment(FragmentAbout.newInstance());
                        break;
                }

                return true;
            }
        });
    }

    private void selectFragment(Fragment selectedFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();
    }

}
