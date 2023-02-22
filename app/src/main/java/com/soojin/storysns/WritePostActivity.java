package com.soojin.storysns;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class WritePostActivity extends AppCompatActivity {
    private static final String TAG="WritePostActivity";
    private FirebaseUser user;
    //이미지 경로
    private ArrayList<String> pathList=new ArrayList<>();
    private LinearLayout contentView_LL;

    //이미지 올리기위해 필요
    private int pathCount;
    private int successCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        contentView_LL=findViewById(R.id.contentView_Linear);

        findViewById(R.id.add_btn).setOnClickListener(onClickListener);
        //게시물 올리는 버튼
        findViewById(R.id.image_btn).setOnClickListener(onClickListener);
        findViewById(R.id.video_btn).setOnClickListener(onClickListener);

    }

    //이미지 경로 가져오기
    private final ActivityResultLauncher<Intent> Launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath);//경로가 생성 될 때마다 추가 (올리기)

                    ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    //1. 이미지 뷰 생성
                    ImageView imageView=new ImageView(WritePostActivity.this);
                    //2. 이미지 뷰 셋팅
                    imageView.setLayoutParams(layoutParams);
                    //Glide 사용해서 이미지 뷰 넣어주기
                    Glide.with(this).load(profilePath).override(1000).into(imageView);
                    contentView_LL.addView(imageView); //콘텐츠 레이아웃 안에 이미지뷰를 넣는다.

                    EditText editText=new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    contentView_LL.addView(editText);
                }
            });

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_btn:
                    contentUpload();
                    break;
                case R.id.image_btn:
                    startActivity(GalleryActivity.class,"image");
                    break;
                case R.id.video_btn:
                    startActivity(GalleryActivity.class,"video");
                    break;

            }
        }
    };
    //add_btn (올리기) 누르면 파이어베이스한테 보내지는 코드
    private void contentUpload() {
        final String title_et = ((EditText) findViewById(R.id.title_et)).getText().toString();
       // final String content_et = ((EditText) findViewById(R.id.content_et)).getText().toString();

        if (title_et.length() >0) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            ArrayList<String>contentList=new ArrayList<>();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            //스토리지 진행 -> url 받아와서 내용이랑 같이 쏘기
            for(int i=0; i<contentView_LL.getChildCount(); i++) { //자식들 만큼 반복시킴
                View view = contentView_LL.getChildAt(i); //자식들을 하나하나 접근할거임.
                if (view instanceof EditText) { //view 가 ET 라면
                    String text = ((EditText) view).getText().toString(); // ET 안에 있는 내용을 하나하나 넣을거임 text에
                    if (text.length() > 0) {
                        contentList.add(text);
                    } else {
                        //이미지 뷰일때 라면?
                        contentList.add(pathList.get(pathCount));

                        final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/" + pathCount + ".jpg"); //0,1,2,3,4..순으로

                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            //메타 데이터 커스텀
                            StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setCustomMetadata("index", "" + (contentList.size()-1))
                                    .build();

                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);

                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.e("log", "uri:" + uri);
                                            contentList.set(index,uri.toString()); //index 값과 uri 값이 잘 들어감.
                                            successCount++;
                                            if(pathList.size()==successCount){
                                                //사진이 여러장 일 때를 위해
                                                // 성공 완료
                                                WriteInfo writeInfo = new WriteInfo(title_et, contentList,user.getUid(),new Date());
                                                storeUpload(writeInfo);
                                                Log.e("test","올리기 test");
                                            }
                                        }
                                    });
                                }
                            });

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        pathCount++;
                    }
                }
            }

        } else {
            Toast.makeText(WritePostActivity.this, "업로드할 내용을 입력헤주세요.", Toast.LENGTH_SHORT).show();

        }
    }
        private void storeUpload (WriteInfo writeInfo){
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

    private void startActivity(Class c, String media){
        Intent intent=new Intent(this,c);
        intent.putExtra("media",media);
        Launcher.launch(intent);
    }
}

