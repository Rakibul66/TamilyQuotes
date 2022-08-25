package com.muththamizh.tamily.ui.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.muththamizh.tamily.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadImageQuote extends AppCompatActivity {

    private ImageView imageUpload;
    private LinearLayout uploadHint;
    private CardView upload;

    Dialog popup;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference document_reference;
    private String userID;

    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image_quote);

        imageUpload = findViewById(R.id.image_upload);
        uploadHint = findViewById(R.id.upload_hint);
        upload = findViewById(R.id.btn_post);


        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        db = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("image_quote");

        popup = new Dialog(this);
        popup.setContentView(R.layout.popur_upload);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        uploadHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(imageUpload);
            uploadHint.setVisibility(View.INVISIBLE);
        }
    }

    private void uploadFile() {
        if (mImageUri != null) {
            final String currentTimeMillis = String.valueOf(System.currentTimeMillis());
            final StorageReference fileReference = mStorageRef.child(currentTimeMillis);

            popup.show();
            popup.setCanceledOnTouchOutside(false);

//            Bitmap bmp = null;
//            try {
//                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
//            byte[] data = baos.toByteArray();

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                            mStorageRef.child(currentTimeMillis).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String img_url = String.valueOf(uri);

                                    Map<String, Object> userMap = new HashMap<>();

                                    userMap.put("quote", img_url);
                                    userMap.put("quoteId", currentTimeMillis);
                                    userMap.put("timestamp", FieldValue.serverTimestamp());

                                    document_reference = db.collection("image_quote").document(currentTimeMillis);

                                    document_reference.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            popup.dismiss();
                                            Toast.makeText(UploadImageQuote.this, "Upload successful", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(UploadImageQuote.this,AddImageQuote.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UploadImageQuote.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadImageQuote.this, " e.getMessage()", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(UploadImageQuote.this,AddImageQuote.class);
        startActivity(intent);
        finish();
    }
}