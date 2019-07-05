package com.example.dell.blog_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    public static final int gallaryRequest=1;
     private  ImageButton selectImage;
     private EditText msetTitle;
     private EditText msetdesc;
     private Button msubmitBtn;
    private Uri imageUri;
    private StorageReference mstorage;
    private ProgressDialog mprogress;
    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        mAuth= FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        mstorage= FirebaseStorage.getInstance().getReference();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("blog");
        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());
        selectImage=findViewById(R.id.imageSelect);
        msetTitle=findViewById(R.id.titleField);
        msetdesc=findViewById(R.id.descField);
        msubmitBtn=findViewById(R.id.submitBtn);
        mprogress = new ProgressDialog(this);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallaryIntent =new Intent(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent,gallaryRequest);
            }
        });
        msubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }
    public void startPosting(){
        mprogress.setMessage("posting to item...");

        final String title_val=msetTitle.getText().toString().trim();
        final String desc_val=msetdesc.getText().toString().trim();
        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && imageUri!=null ){
            mprogress.show();
            StorageReference filepath=mstorage.child("BLOG_IMAGES").child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                 final   Uri downloadurl=taskSnapshot.getDownloadUrl();
                  final  DatabaseReference newpost=mdatabase.push();

                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newpost.child("title").setValue(title_val);
                            newpost.child("desc").setValue(desc_val);
                            newpost.child("images").setValue(downloadurl.toString());
                            newpost.child("uid").setValue(mCurrentUser.getUid());
                            newpost.child("email").setValue(dataSnapshot.child("email").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(PostActivity.this,MainActivity.class));

                                    }
                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mprogress.dismiss();


                }
            });


        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==gallaryRequest && resultCode==RESULT_OK){
            imageUri=data.getData();
            selectImage.setImageURI(imageUri);


        }
    }
}
