package com.example.syyam.saifapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private FirebaseAuth mAuth;
    private Button mRegisterButton;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private TextView button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        mNameField=(EditText) findViewById(R.id.nameField);
        mEmailField=(EditText) findViewById(R.id.emailField);
        mPasswordField=(EditText) findViewById(R.id.passwordField);
        mRegisterButton=(Button) findViewById(R.id.registerBtn);
        button=(TextView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup=new Intent(RegisterActivity.this,LoginActivity.class);
                signup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signup);
            }
        });

        mProgress=new ProgressDialog(this);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }


        });
    }

    private void startRegister() {
        final String name=mNameField.getText().toString().trim();
        String email=mEmailField.getText().toString().trim();
        String password=mPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {
            mProgress.setMessage("Registering User");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        String user_id=mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db =mDatabase.child(user_id);

                        current_user_db.child("name").setValue(name);
                        current_user_db.child("image").setValue("default");
                        mProgress.dismiss();
                        Intent ma=new Intent(RegisterActivity.this,MainActivity.class);
                        ma.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(ma);
                    }
                    else
                    {
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error in registering", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}
