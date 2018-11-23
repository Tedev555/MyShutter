package me.thanongsine.myshutter.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.thanongsine.myshutter.R;
import me.thanongsine.myshutter.models.Photo;

public class FragmentPhotoInfo extends Fragment {
    private static final String ARG_PARAM1 = "PARAM1";
    private String photoKey;
    private DatabaseReference databaseRef;
    ImageView imageView;
    EditText titleEdt;
    Button saveChangedBtn;
    Button deleteBtn;
    private Uri uri;
    private Photo photo;
    private ValueEventListener eventListioner;

    public FragmentPhotoInfo() {
    }

    public static Fragment newInstance(String param1) {
        FragmentPhotoInfo fragment = new FragmentPhotoInfo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        if (getArguments() != null) {
            photoKey = getArguments().getString(ARG_PARAM1);
            Log.e("PhotoKeyLog", photoKey);
        } else {
            Log.e("PhotoKeyLog", "Null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_info, container, false);

        imageView = view.findViewById(R.id.image_view_info);
        titleEdt = view.findViewById(R.id.title_info_edt);
        deleteBtn = view.findViewById(R.id.delete_btn);
        saveChangedBtn = view.findViewById(R.id.save_changed_btn);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromDevice();
            }
        });

        saveChangedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uri == null) {
                    updatePhotoData(photo.getUrl(), titleEdt.getText().toString());
                } else {
                    uploadImageToFirebase(titleEdt.getText().toString());
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseRef.child("photolist").child(photoKey).removeValue();
                getActivity().finish();
            }
        });

        databaseRef.child("photolist").child(photoKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    photo = dataSnapshot.getValue(Photo.class);
                    Picasso.get().load(photo.getUrl()).into(imageView);
                    titleEdt.setText(photo.getTitle());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    private void getImageFromDevice() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Please Choose App"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            uri = data.getData();

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver()
                        .openInputStream(uri));
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 400, 300, true);
                imageView.setImageBitmap(bitmap1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Please Choose Photo", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadImageToFirebase(final String title) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference photoImgRef =
                storageReference.child("PhotoList/" + title + "_photo");

        UploadTask uploadTask = photoImgRef.putFile(uri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return photoImgRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String photoUrl = task.getResult().toString();
                    updatePhotoData(photoUrl, title);
                }
            }
        });
    }

    private void updatePhotoData(String url, String title) {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        Photo updatePhoto = new Photo(url, title);

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/photolist/" + photoKey, updatePhoto);

        databaseRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(),
                        "Completed Update Data", Toast.LENGTH_SHORT)
                        .show();
            }
        });

//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/movieslist/"+ moviesKey, movies);
//        mDatabaseRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(getContext(), "Completed Updating " , Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getContext(), "Update Fail: " + e, Toast.LENGTH_SHORT).show();
//            }
//        });

    }
}
