package com.soojin.storysns;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUPActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG="signUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.signUp_btn).setOnClickListener(onClickListener);
        findViewById(R.id.login_btn).setOnClickListener(onClickListener);
    }
        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
        }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        //로그인-> 뒤로가기 했을 때 앱 종료 시키기

    }

View.OnClickListener onClickListener=new View.OnClickListener(){
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signUp_btn:
                Log.e("클릭","클릭");
                signUp();
                break;
            case R.id.login_btn:
                Log.e("클릭","클릭");
                Intent intent = new Intent(SignUPActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
};
    private void signUp() {
        String email = ((EditText) findViewById(R.id.email_et)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_et)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.password_check_et)).getText().toString();
        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {

            if (password.equals(passwordCheck)) {


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(SignUPActivity.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                    //성공 ui
                                } else {
                                    if (task.getException() != null) {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "fail", task.getException());
                                        Toast.makeText(SignUPActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                        //실패->ui
                                    }
                                }
                            }
                        });
            } else {
                Toast.makeText(SignUPActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    
}