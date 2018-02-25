package com.example.syyam.saifapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseCuurentUser;
    private RecyclerView mProfileList;
    private Query mQueryCurrentUser;
    //private FirebaseAuthException firebase;
    private FirebaseAuth firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        // mDatabaseUsers.keepSynced(true);
        mProfileList=(RecyclerView) findViewById(R.id.profile_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mProfileList.setHasFixedSize(true);
        mProfileList.setLayoutManager(new LinearLayoutManager(this));
        mAuth= FirebaseAuth.getInstance();


        if(mAuth.getCurrentUser() == null){
            Intent login2=new Intent(MainActivity.this,LoginActivity.class);
            login2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login2);
        }

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null)
                {
                    Intent login=new Intent(MainActivity.this,LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                }
            }
        };

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Profiles");
        String currentUserId=mAuth.getCurrentUser().getUid();
        mDatabaseCuurentUser=FirebaseDatabase.getInstance().getReference().child("Profiles");

        mQueryCurrentUser=mDatabaseCuurentUser.orderByChild("uid").equalTo(currentUserId);
        mDatabase.keepSynced(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checkUserExist();
       // mAuth.addAuthStateListener(mAuthListener);
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Profile,ProfileViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Profile, ProfileViewHolder>(

                Profile.class,
                R.layout.profile_row,
                ProfileViewHolder.class,
                mQueryCurrentUser
        ) {
            @Override
            protected void populateViewHolder(ProfileViewHolder viewHolder, Profile model, int position) {

                final String post_key= getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setPhone(model.getPhone());
                viewHolder.setEmail(model.getEmail());
                viewHolder.setImage(getApplicationContext(),model.getImage());

                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {


                        mDatabase.child(post_key).removeValue();
                        return true;
                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(MainActivity.this,post_key,Toast.LENGTH_LONG).show();

                       // Intent singleBlogIntent =new Intent(MainActivity.this,ProfileSingleActivity.class);
                        //singleBlogIntent.putExtra("blog_id",post_key);
                        //startActivity(singleBlogIntent);
                        //Toast.makeText(MainActivity.this,"Long press to delete the post",Toast.LENGTH_LONG).show();
                        Intent abc =new Intent(MainActivity.this,ProfileViewHolder.class);
                        abc.putExtra("Profile_id",post_key);
                        startActivity(abc);
                    }
                });
            }
        };
        mProfileList.setAdapter(firebaseRecyclerAdapter);
    }

    /*
   private void checkUserExist() {
        final String user_id=mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id))
                {
                    Intent setup=new Intent(MainActivity.this,SetupActivity.class);
                    setup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setup);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/
    public static class ProfileViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        TextView post_name;
        
        public ProfileViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

            post_name=(TextView) mView.findViewById(R.id.post_name);
        }
        public void setName(String name)
        {
            //TextView post_name=(TextView) mView.findViewById(R.id.post_name);
            post_name.setText(name);
        }
        public void setPhone(String phone)
        {
            TextView post_phone=(TextView) mView.findViewById(R.id.post_phone);
            post_phone.setText(phone);
        }
        public void setEmail(String email)
        {
            TextView post_email=(TextView) mView.findViewById(R.id.post_email);
            post_email.setText(email);
        }
        public void setImage(final Context ctx, final String image)
        {
            final ImageView post_image=(ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(ctx).load(image).into(post_image);
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_add)
        {
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if (item.getItemId()==R.id.action_logout)
        {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Are you sure?");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                Process.killProcess(Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
