package com.example.syyam.saifapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PostActivity extends AppCompatActivity {

    private Uri mImageUri=null;
    private Uri resultUri=null;
    private ImageButton selectImage;
    private EditText name;
    private EditText phone;
    private EditText email;
    private Button submit;
    private static final int GALLERY_REQUEST=1;
    private StorageReference mstorage;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        mProgress=new ProgressDialog(this);
        mstorage= FirebaseStorage.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Profiles");
        selectImage =(ImageButton) findViewById(R.id.imageSelect);
        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        name =(EditText) findViewById(R.id.name);
        phone =(EditText) findViewById(R.id.phone);
        email =(EditText) findViewById(R.id.email);
        submit =(Button) findViewById(R.id.submit);



        selectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent,GALLERY_REQUEST);
                }
            });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

    }


    private void startPosting(){
        //startActivity(new Intent(PostActivity.this,RealAlarm.class));
        mProgress.setMessage("Uploading Data");

        mProgress.show();
        final String NAME=name.getText().toString().trim();
        final String PHONE=phone.getText().toString().trim();
        final String EMAIL=email.getText().toString().trim();
        if(!TextUtils.isEmpty(NAME)&&!TextUtils.isEmpty(PHONE)&&!TextUtils.isEmpty(EMAIL)&& resultUri!=null)
        {

            StorageReference filepath=mstorage.child("Images").child(resultUri.getLastPathSegment());
            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    final DatabaseReference newPost=mDatabase.push();

                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("Name").setValue(NAME);
                            newPost.child("Phone").setValue(PHONE);
                            newPost.child("Email").setValue(EMAIL);
                            newPost.child("Image").setValue(downloadUrl.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        startActivity(new Intent(PostActivity.this,Main2Activity.class));
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(PostActivity.this,"error",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            Toast.makeText(PostActivity.this,"Error Uploading data",Toast.LENGTH_LONG).show();
                        }
                    });

                    mProgress.dismiss();
                    //startActivity(new Intent(PostActivity.this,Main2Activity.class));
                }
            });
        }
        else
        {
            mProgress.dismiss();
            Toast.makeText(PostActivity.this, "Invalid data", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            mImageUri =data.getData();
            selectImage.setImageURI(mImageUri);
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(16,9)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                selectImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
