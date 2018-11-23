package me.thanongsine.myshutter.fragments;

import android.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import me.thanongsine.myshutter.R;
import me.thanongsine.myshutter.models.Photo;

public class FragmentPhotoUpload extends Fragment {
    ImageView uploadImageView;
    EditText titleEditText;
    Button saveBtn;
    private Uri uri;
    Boolean aBoolean;
    DatabaseReference databaseRef;
    ProgressBar progressBar;
    AlertDialog categoryFormDialog;

    public FragmentPhotoUpload() {
    }

    public static Fragment newInstance() {
        return new FragmentPhotoUpload();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_upload, container, false);
        uploadImageView = view.findViewById(R.id.upload_img_view);
        titleEditText = view.findViewById(R.id.title_edit_text);
        saveBtn = view.findViewById(R.id.save_btn);
        progressBar = view.findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.GONE);

        uploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromDevice();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                uploadImageToFirebase(titleEditText.getText().toString());
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
            aBoolean = false;

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver()
                        .openInputStream(uri));
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 400, 300, true);
                uploadImageView.setImageBitmap(bitmap1);
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
                    addNewPhotoData(photoUrl, title);
                }
            }
        });
    }

    private void addNewPhotoData(String url, String title) {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        Photo photo = new Photo(url, title);
        String photoID = databaseRef.child("photolist").push().getKey();

        databaseRef.child("photolist").child(photoID).setValue(photo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Upload photo completed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
