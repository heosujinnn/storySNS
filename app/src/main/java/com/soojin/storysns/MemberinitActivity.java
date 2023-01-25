package com.soojin.storysns;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class MemberinitActivity extends AppCompatActivity {
    private static final String TAG="MemberinitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

       findViewById(R.id.memberCheck_btn).setOnClickListener(onClickListener);
       findViewById(R.id.profile_iv).setOnClickListener(onClickListener);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private final ActivityResultLauncher<Intent> resultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData()!=null)  {
//                   Bundle extras = result.getData().getExtras();
//                   bitmap=(Bitmap)extras.get("some_key");
                    Intent data = result.getData();
                    String returnValue=data.getStringExtra("some_key");

                }

            });

    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.memberCheck_btn:
                    profileUpdate();
                    break;
                case R.id.profile_iv:
                    Intent intent= new Intent(MemberinitActivity.this, CameraActivity.class);
                    startActivity(intent);

                    break;

            }
        }
    };
    private void profileUpdate() {
        String name = ((EditText) findViewById(R.id.name_et)).getText().toString();
        String phoneNumber = ((EditText) findViewById(R.id.phoneNumber_et)).getText().toString();
        String birthDay = ((EditText) findViewById(R.id.birthday_et)).getText().toString();
        String address = ((EditText) findViewById(R.id.address_et)).getText().toString();


        if (name.length() > 0 &&phoneNumber.length()>9 &&birthDay.length()>5 && address.length()>0) {
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            MemberInfo memberInfo=new MemberInfo(name,phoneNumber,birthDay,address);
            if(user!=null) {
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
            }



        } else{
                Toast.makeText(MemberinitActivity.this, "회원정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        }


}



