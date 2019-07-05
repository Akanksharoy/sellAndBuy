package com.example.dell.blog_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;

public class SetupActivity extends AppCompatActivity {

    private EditText mNameField;
    private Button mSubmitBtn;
    private ImageButton mSetUpImage;
    private EditText mSetUpEmail;
    private Uri mImageUri=null;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseusers;
    private StorageReference mStorageImage;
    EditText et;
    private ProgressDialog mProgress;

    private static final int GALLARY_REQUEST =1;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth=FirebaseAuth.getInstance();
        mStorageImage= FirebaseStorage.getInstance().getReference().child("Profile Images");

        mDatabaseusers= FirebaseDatabase.getInstance().getReference().child("users");
        mProgress=new ProgressDialog(this);

        mNameField=(EditText)findViewById(R.id.setUpNameField);
        mSubmitBtn=(Button)findViewById(R.id.setUpSubmitBtn);
        mSetUpImage=(ImageButton)findViewById(R.id.setUpImageField);
        mSetUpEmail=(EditText)findViewById(R.id.setUpEmailField);
        et=(EditText)findViewById(R.id.usn);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccount();
            }
        });
        mSetUpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallayIntent=new Intent();
                gallayIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallayIntent.setType("image/*");
                startActivityForResult(gallayIntent,GALLARY_REQUEST);

            }
        });
    }
    private void startSetupAccount() {
        final String name = mNameField.getText().toString().trim();
        final String email=mSetUpEmail.getText().toString().trim();
        final String usn=et.getText().toString();
        final String user_id=mAuth.getCurrentUser().getUid();
        if(!usn.matches("1[sS][iI]1[0-8][a-zA-Z][a-zA-Z][0-3][0-9][0-9]"))
        {
            et.setError("Invalid usn");
            et.requestFocus();
            return;
        }


            if (!TextUtils.isEmpty(name) && mImageUri != null) {

                mProgress.setMessage("Finishing Setup");
                mProgress.show();

                StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());

                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String downloadUri = taskSnapshot.getDownloadUrl().toString();
                        mDatabaseusers.child(user_id).child("name").setValue(name);
                        mDatabaseusers.child(user_id).child("image").setValue(downloadUri);
                        mDatabaseusers.child(user_id).child("email").setValue(email);


                        mProgress.dismiss();

                        Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    }
                });


            }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLARY_REQUEST && resultCode==RESULT_OK){
            Uri imageUri=data.getData();
            if (imageUri != null) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mSetUpImage.setImageURI(mImageUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

