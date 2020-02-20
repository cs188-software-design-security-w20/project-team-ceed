package com.ceed.tripster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

@SuppressWarnings("FieldCanBeLocal")

public class GalleryActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 71;

    private String _tripId;

    private StorageReference _tripImageStorageRef;
    private DatabaseReference _tripImagesDatabaseRef;
    private FirebaseAuth _firebaseAuth;

    private GalleryAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        _tripId = getIntent().getStringExtra("TRIP_ID");

        _tripImageStorageRef = FirebaseStorage.getInstance().getReference()
                .child("images").child(_tripId);
        _tripImagesDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child("Trips").child(_tripId).child("images");
        _firebaseAuth = FirebaseAuth.getInstance();

        // setup RecyclerView
        RecyclerView galleryRecyclerView = findViewById(R.id.galleryRecyclerView);
        galleryRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 2)
        );

        FirebaseRecyclerOptions<GalleryPhoto> options =
                new FirebaseRecyclerOptions.Builder<GalleryPhoto>()
                        .setQuery(_tripImagesDatabaseRef, GalleryPhoto.class)
                        .build();

        _adapter = new GalleryAdapter(options);

        galleryRecyclerView.setAdapter(_adapter);
        _adapter.startListening();

        // setup choose image button
        FloatingActionButton fab = findViewById(R.id.addFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    } // end onCreate()

    private void chooseImage() {
        Intent pickImage = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, PICK_IMAGE_REQUEST);
    } // end chooseImage()

    private void uploadImage(Uri filePath) {
        if (filePath == null) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        String uploadedFilename = UUID.randomUUID().toString();

        StorageReference storageFileRef = _tripImageStorageRef.child(uploadedFilename);
        Log.d("GALLERYACTIVITY", "uploadImage: " + uploadedFilename);
        storageFileRef.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageFileRef.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                String key = _tripImagesDatabaseRef.push().getKey();
                                String userId = _firebaseAuth.getCurrentUser().getUid();

                                if (key == null) {
                                    return;
                                }

                                Long uploadTsLong = System.currentTimeMillis()/1000;
                                String uploadTs = uploadTsLong.toString();

                                GalleryPhoto newPhoto = new GalleryPhoto(downloadUrl, userId, uploadTs);
                                _tripImagesDatabaseRef.child(key).setValue(newPhoto);
                            }
                        });

                        progressDialog.dismiss();
                        Toast.makeText(GalleryActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(GalleryActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    } // end uploadImage()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null ) {
            Uri filePath = data.getData();
            uploadImage(filePath);
        }
    } // end onActivityResult()

    private void fetchPhotos() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts");
    }
}
