package com.soojin.storysns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.Write;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //화면 회전 안되게 함.

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();


        if(user==null){ //만약에 현재 유저가 null 이면 로그인이 안됐다면,
            startSignUpActivity(); //회원가입으로 ㄱㄱ
        }
        else { // if (user != null) null 이 아니면
            //회원가입 or 로그인 정보가 있는지 체크
//            Intent intent=new Intent(MainActivity.this,MemberinitActivity.class);
//            startActivity(intent);

            DocumentReference docRef = db.collection("users").document("user.getUid");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            } else {
                                Log.d(TAG, "No such document");
                                Intent intent=new Intent(MainActivity.this,MemberinitActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            Intent intent = new Intent(MainActivity.this, MemberinitActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent); //회원정보 입력하라는 액티비티
                        }
                    }
                }
                });
                    }
        findViewById(R.id.logout_btn).setOnClickListener(onClickListener);
        findViewById(R.id.add_fab).setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logout_btn:
                    FirebaseAuth.getInstance().signOut(); //로그아웃
                    startSignUpActivity();
                    break;

                case R.id.add_fab:
                    Intent intent=new Intent(MainActivity.this, WritePostActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void startSignUpActivity(){

        Intent intent=new Intent(MainActivity.this,SignUPActivity.class);
        startActivity(intent);
    }

}