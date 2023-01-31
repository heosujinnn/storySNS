package com.soojin.storysns;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MemberinitActivity extends AppCompatActivity {
    private static final String TAG = "MemberinitActivity";
    private ImageView profile_iv;
    private String profilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        findViewById(R.id.memberCheck_btn).setOnClickListener(onClickListener);
        profile_iv = findViewById(R.id.profile_iv);
        profile_iv.setOnClickListener(onClickListener);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private final ActivityResultLauncher<Intent> resultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getResultCode() == RESULT_OK) {
//                   Bundle extras = result.getData().getExtras();
//                   bitmap=(Bitmap)extras.get("some_key");
                    Intent data = result.getData();
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그", "profilePath" + profilePath); //카메라찍고 사진 저장되는 path

                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    profile_iv.setImageBitmap(bmp);

                }
//                else{
//                    Log.e("로그","실패profilePath");
//                }
            });

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.memberCheck_btn:
                    profileUpdate();
                    break;
                case R.id.profile_iv:
                    Intent intent = new Intent(MemberinitActivity.this, CameraActivity.class);
                    resultLauncher.launch(intent);
                    //startActivity(intent);

                    break;

            }
        }
    };

    private void profileUpdate() {
        String name = ((EditText) findViewById(R.id.name_et)).getText().toString();
        String phoneNumber = ((EditText) findViewById(R.id.phoneNumber_et)).getText().toString();
        String birthDay = ((EditText) findViewById(R.id.birthday_et)).getText().toString();
        String address = ((EditText) findViewById(R.id.address_et)).getText().toString();


        if (name.length() > 0 && phoneNumber.length() > 9 && birthDay.length() > 5 && address.length() > 0) {


            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            //사용자마다 사진 파일을 각 각 생성하게 (덮어쓰지 않게)
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

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
                            Log.e("로그", "성공: " + downloadUri);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            MemberInfo memberInfo = new MemberInfo(name, phoneNumber, birthDay, address,downloadUri.toString());
                                db.collection("users").document(user.getUid()).set(memberInfo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                Toast.makeText(MemberinitActivity.this, "회원정보 등록을 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                                Toast.makeText(MemberinitActivity.this, "회원정보 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                        } else {
                            Log.e("로그", "실패");
                        }
                        }
                    });
            } catch (FileNotFoundException e) {
                Log.e("로그", "에러" + e.toString());
            }


        } else {
            Toast.makeText(MemberinitActivity.this, "회원정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        }
    }


