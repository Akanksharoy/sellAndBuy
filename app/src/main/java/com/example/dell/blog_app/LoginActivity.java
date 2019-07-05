package com.example.dell.blog_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mLoginEmailField;
    private EditText mLoginPasswordField;
    private Button mLoginBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    private DatabaseReference mDatabaseUsers;
    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN=1;
    GoogleApiClient mGoogleApiClient;
    private static final  String TAG = "LoginActivity";
    private  TextView forgetpwd;
    //private DatabaseReference mDatatbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        //mDatatbase= FirebaseDatabase.getInstance().getReference().child("users");

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUsers.keepSynced(true);

        mProgress = new ProgressDialog(this);
        mGoogleBtn=(SignInButton)findViewById(R.id.googleBtn);

        mLoginEmailField = (EditText) findViewById(R.id.loginEmailField);
        mLoginPasswordField = (EditText) findViewById(R.id.loginPasswordField);

        mLoginBtn = (Button)findViewById(R.id.looginBtn);
        forgetpwd=(TextView) findViewById(R.id.textViewforgot);

        findViewById(R.id.textviewsignup).setOnClickListener(this);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();

            }
        });

        forgetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,PasswordActivity.class));
            }
        });
        //--------Google sign in
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    Toast.makeText(LoginActivity.this,"somathing went wrong",Toast.LENGTH_SHORT).show();


                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }
    private void signIn(){
        Intent signInIntent= Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mProgress.setMessage("starting sign in");
            mProgress.show();
            if(result.isSuccess()){
                GoogleSignInAccount account=result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                //Googlesign in failed
                mProgress.dismiss();
                Toast.makeText(LoginActivity.this,"Authentication went wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
       // Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Sign in success, update UI with the signed-in user's information
                       // Log.d(TAG, "signInWithCredential:success" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            mProgress.dismiss();

                            Log.d(TAG,"sign in with credential successful");
                            FirebaseUser user=mAuth.getCurrentUser();
                            checkUserExist();
                            // If sign in succesful, display a message to the user.

                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void checkLogin() {
        String email = mLoginEmailField.getText().toString().trim();
        String password = mLoginPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgress.setMessage("Checking login...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        mProgress.dismiss();
                        checkUserExist();


                    } else {
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_LONG).show();
                    }

                }

            });

        }
    }

    private void checkEmail(){
        FirebaseUser firebaseUser= mAuth.getInstance().getCurrentUser();
        Boolean emailflag= firebaseUser.isEmailVerified();

        if (emailflag){

            Toast.makeText(this,""+emailflag,Toast.LENGTH_SHORT).show();

            startActivity(new Intent(LoginActivity.this,SetupActivity.class));
            finish();
        }
        else
        {
            Toast.makeText(this,"Verify your email",Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }

    }

    private void checkUserExist() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String currentUserID = user.getUid();

            // Use currentUserID


            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(currentUserID)) {
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);


                    } else {
                        checkEmail();
                        /*Intent setupIntent = new Intent(LoginActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);*/

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.textviewsignup:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
        }

    }
}
