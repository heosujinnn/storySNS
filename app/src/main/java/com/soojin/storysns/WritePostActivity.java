package com.soojin.storysns;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class WritePostActivity extends AppCompatActivity {
    private static final String TAG="WritePostActivity";
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.add_btn).setOnClickListener(onClickListener);
        //게시물 올리는 버튼
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_btn:
                    contentUpload();
                    break;
            }
        }
    };
    //add_btn (올리기) 누르면 파이어베이스한테 보내지는 코드
    private void contentUpload() {
        final String title_et = ((EditText) findViewById(R.id.title_et)).getText().toString();
        final String content_et = ((EditText) findViewById(R.id.content_et)).getText().toString();

        if (title_et.length() > 0 && content_et.length() > 0) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            WriteInfo writeInfo = new WriteInfo(title_et, content_et,user.getUid());
            uploader(writeInfo);
        } else {
            Toast.makeText(WritePostActivity.this, "업로드할 내용을 입력헤주세요.", Toast.LENGTH_SHORT).show();

        }
    }
        private void uploader (WriteInfo writeInfo){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("posts").add(writeInfo)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        //document : 자동으로 id가 생성돼서 차곡차곡 잘 들어간다.
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(WritePostActivity.this, "업로드에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WritePostActivity.this, "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
