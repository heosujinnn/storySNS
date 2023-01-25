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

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG="signUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.login_btn).setOnClickListener(onClickListener);
        findViewById(R.id.gotoPasswordReset_tv).setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.login_btn:
                    Log.e("클릭","클릭");
                    login();
                    break;
                case R.id.gotoPasswordReset_tv:
                    Intent intent=new Intent(LoginActivity.this,PasswordResetActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
    private void login() {
        String email = ((EditText) findViewById(R.id.email_et)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_et)).getText().toString();
        if (email.length() > 0 && password.length() > 0 ) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "로그인 성공하였습니다", Toast.LENGTH_SHORT).show();

                                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            } else{
                Toast.makeText(LoginActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }



