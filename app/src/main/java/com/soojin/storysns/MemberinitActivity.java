package com.soojin.storysns;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soojin.storysns.adapter.GalleryAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class MemberinitActivity extends AppCompatActivity {
    private static final String TAG = "MemberinitActivity";
    private ImageView profile_iv;
    private String profilePath;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        findViewById(R.id.memberCheck_btn).setOnClickListener(onClickListener);
        profile_iv = findViewById(R.id.profile_iv);
        profile_iv.setOnClickListener(onClickListener);

        findViewById(R.id.picture).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private final ActivityResultLauncher<Intent> resultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    profilePath = data.getStringExtra("profilePath");
//                    Log.e("??????", "profilePath" + profilePath); //??????????????? ?????? ???????????? path
//                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
//                    profile_iv.setImageBitmap(bmp);
                    Glide.with(this)
                            .load(profilePath)
                            .centerCrop()
                            .override(500)
                            .into(profile_iv);
                }
            });

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.memberCheck_btn:
                    profileUpdate();
                    break;
                case R.id.profile_iv:
                    CardView cardView = findViewById(R.id.pictureORgalley);
                    if (cardView.getVisibility() == View.VISIBLE) {
                        cardView.setVisibility(View.GONE); //???????????? ????????? ????????????
                    } else { //???????????? ???????????? ?????????
                        cardView.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.picture: //????????????
                    Intent intent = new Intent(MemberinitActivity.this, CameraActivity.class);
                    resultLauncher.launch(intent);
                    break;
                case R.id.gallery: //????????? ??????
                    //?????? ??????

                    if (ContextCompat.checkSelfPermission(MemberinitActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                     //?????? ?????? ??????..

                    }
                    else {
                        //Toast.makeText(MemberinitActivity.this,"????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
            }
        }
    };


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.i("DEBUG", "?????? ?????????");
                    Intent resultIntent = new Intent(MemberinitActivity.this, GalleryActivity.class);
                    resultLauncher.launch(resultIntent);
                }
                else {

                    new AlertDialog.Builder(MemberinitActivity.this)
                            .setTitle("?????? ??????")
                            .setMessage("????????? ??????????????????, ??? ?????? ????????? ????????? ?????????.")
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package",
                                            BuildConfig.APPLICATION_ID, null);
                                    intent.setData(uri);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                })
                                        .create().show();
                }
            });
//@Override
//public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                       int[] grantResults) {
//    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    switch (requestCode) {
//        case 1:
//            // If request is cancelled, the result arrays are empty.
//            if (grantResults.length > 0 &&
//                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
////                Intent resultIntent = new Intent(MemberinitActivity.this, GalleryActivity.class);
////                //resultLauncher.launch(resultIntent);
////                startActivity(resultIntent);
//
//            } else {
//               Toast.makeText(MemberinitActivity.this,"?????? ??????????????????",Toast.LENGTH_SHORT).show();
//            }
//            return;
//    }
//
//}


        private void profileUpdate() {
            String name = ((EditText) findViewById(R.id.name_et)).getText().toString();
            String phoneNumber = ((EditText) findViewById(R.id.phoneNumber_et)).getText().toString();
            String birthDay = ((EditText) findViewById(R.id.birthday_et)).getText().toString();
            String address = ((EditText) findViewById(R.id.address_et)).getText().toString();


            if (name.length() > 0 && phoneNumber.length() > 9 && birthDay.length() > 5 && address.length() > 0) {

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                //??????????????? ?????? ????????? ??? ??? ???????????? (???????????? ??????)
                user = FirebaseAuth.getInstance().getCurrentUser();
                final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

                if (profilePath == null) {
                    MemberInfo memberInfo = new MemberInfo(name, phoneNumber, birthDay, address);
                    uploader(memberInfo);


                } else {
                    try {
                        InputStream stream = new FileInputStream(new File(profilePath));

                        UploadTask uploadTask = mountainImagesRef.putStream(stream);

                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();

                                }

                                // Continue with the task to get the download URL
                                return mountainImagesRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    Log.e("??????", "??????: " + downloadUri);

                                    MemberInfo memberInfo = new MemberInfo(name, phoneNumber, birthDay, address, downloadUri.toString());
                                    uploader(memberInfo);


                                } else {
                                    Log.e("??????", "???????????????????????? ????????? ??????");
                                }
                            }
                        });
                    } catch (FileNotFoundException e) {
                        Log.e("??????", "??????" + e.toString());
                    }
                }

            } else {
                Toast.makeText(MemberinitActivity.this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
        }
        private void uploader(MemberInfo memberInfo) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //memberInfo = new MemberInfo(name, phoneNumber, birthDay, address, downloadUri.toString());
            db.collection("users").document(user.getUid()).set(memberInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MemberinitActivity.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MemberinitActivity.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

