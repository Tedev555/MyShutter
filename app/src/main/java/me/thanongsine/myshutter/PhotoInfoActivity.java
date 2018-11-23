package me.thanongsine.myshutter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import me.thanongsine.myshutter.fragments.FragmentPhotoInfo;

public class PhotoInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_info);

        String photoKey =  getIntent().getStringExtra("PHOTO_KEY");
        Log.e("PhotoKeyLog", photoKey);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, FragmentPhotoInfo.newInstance(photoKey))
                .commit();
    }
}
