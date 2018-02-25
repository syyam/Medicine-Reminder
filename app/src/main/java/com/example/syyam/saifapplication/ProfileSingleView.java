package com.example.syyam.saifapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileSingleView extends AppCompatActivity {

    String mpost_key=null;

    private TextView mPostName;
    private TextView mPostPhone;
    private TextView mPostEmail;
    private ImageView mPostImage;
    private Button mRemovePost;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single_view);

        mPostName=(TextView) findViewById(R.id.post_name);
        mPostPhone=(TextView) findViewById(R.id.post_phone);
        mPostEmail=(TextView) findViewById(R.id.post_email);
        mPostImage=(ImageView) findViewById(R.id.post_image);
        mRemovePost=(Button) findViewById(R.id.RemovePost);

        mAuth=FirebaseAuth.getInstance();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Profiles");
        mpost_key=getIntent().getExtras().getString("Profile_id");

        mRemovePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child(mpost_key).removeValue();
                Intent L=new Intent(ProfileSingleView.this,MainActivity.class);
                startActivity(L);
            }
        });

        mDatabase.child(mpost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_Name= (String) dataSnapshot.child("Name").getValue();
                String post_Phone= (String) dataSnapshot.child("Phone").getValue();
                String post_Email= (String) dataSnapshot.child("Email").getValue();
                String post_Image= (String) dataSnapshot.child("Image").getValue();
                String post_Uid= (String) dataSnapshot.child("uid").getValue();

                mPostName.setText(post_Name);
                mPostPhone.setText(post_Phone);
                mPostEmail.setText(post_Email);
                Picasso.with(ProfileSingleView.this).load(post_Image).into(mPostImage);

                if (mAuth.getCurrentUser().getUid().equals(post_Uid))
                {
                    mRemovePost.setVisibility(View.VISIBLE);

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
