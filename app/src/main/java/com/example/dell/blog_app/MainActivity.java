package com.example.dell.blog_app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {


    private RecyclerView mblog_list;
    private DatabaseReference mdatabase;
    private DatabaseReference mdatabaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };
        mdatabase= FirebaseDatabase.getInstance().getReference().child("blog");
        mdatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabaseUsers.keepSynced(true);

        mblog_list=findViewById(R.id.blog_list);
        mblog_list.setHasFixedSize(true);
        mblog_list.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        //checkUserExist();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<blog, BlogViewHolder>(
                blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mdatabase
                ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, blog model, int position) {
                final String post_key=getRef(position).getKey();


                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImages());
                viewHolder.setEmail(model.getEmail());
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleItemIntent=new Intent(MainActivity.this,ItemSingleActivity.class);
                        singleItemIntent.putExtra("item_id",post_key);
                        startActivity(singleItemIntent);
                    }
                });

            }
        };
        mblog_list.setAdapter(firebaseRecyclerAdapter);

    }



    /*private void checkUserExist() {


        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            final String currentUserID = user.getUid();

            // Use currentUserID

            mdatabaseUsers.addValueEventListener(new ValueEventListener() {

                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(currentUserID)) {
                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }*/
          public static class BlogViewHolder extends RecyclerView.ViewHolder {
            View mview;

            public BlogViewHolder(View itemView) {
                super(itemView);
                 mview=itemView;

            }

            public void setTitle(String title) {
                TextView post_title = mview.findViewById(R.id.post_title);
                post_title.setText(title);
            }


              public void setDesc(String desc){

                  TextView post_description = (TextView) mview.findViewById(R.id.post_desc);
                  post_description.setText(desc);
              }
            public  void setImage(Context ctx,String image){
                ImageView postImage=mview.findViewById(R.id.post_image);
                Picasso.with(ctx).load(image).into(postImage);
            }
            public void setEmail(String email){
                TextView post_description = (TextView) mview.findViewById(R.id.post_username);
                post_description.setText(email);
            }

        }



        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_menu, menu);
            return true;
        }
        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            if (item.getItemId() == R.id.action_add) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));


            }
            if(item.getItemId()==R.id.action_logout){
                logout();
            }
            return super.onOptionsItemSelected(item);
        }
        private void logout(){
            mAuth.signOut();
        }



}
