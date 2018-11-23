package me.thanongsine.myshutter.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.thanongsine.myshutter.PhotoListAdapter;
import me.thanongsine.myshutter.R;
import me.thanongsine.myshutter.models.Photo;

public class FragmentPhotoList extends Fragment {
    RecyclerView recyclerView;
    List<Photo> photoList;
    DatabaseReference databaseRef;

    public FragmentPhotoList() {
    }

    public static Fragment newInstance() {
        return new FragmentPhotoList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseRef = FirebaseDatabase.getInstance().getReference();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        databaseRef.child("photolist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                photoList = new ArrayList<>();
                List<String> photoKeyList = new ArrayList<>();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String photoKey = snapshot.getKey();
                    photoKeyList.add(photoKey);

                    Photo photo = snapshot.getValue(Photo.class);
                    photoList.add(photo);
                }

                PhotoListAdapter adapter = new PhotoListAdapter(getContext(), photoList, photoKeyList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
