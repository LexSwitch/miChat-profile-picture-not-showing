package com.nalexander240.michat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText registerUsername,registerUserEmail,registerUserPassword;
    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference storeUserDefaultDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.registerToolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerUsername = (EditText) findViewById(R.id.registerName);
        registerUserEmail = (EditText) findViewById(R.id.registerEmail);
        registerUserPassword = (EditText) findViewById(R.id.registerPassword);
        createAccountButton=(Button) findViewById(R.id.createAccountButton);


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name=registerUsername.getText().toString();
                String email=registerUserEmail.getText().toString();
                String password=registerUserPassword.getText().toString();

                RegisterAccount(name,email,password);
            }
        });

    }

    private void RegisterAccount(final String name, String email, String password) {
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter your name",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait while we are creating your account");
            loadingBar.show();

                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                   String currentUserId= mAuth.getCurrentUser().getUid();
                                    storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

                                    storeUserDefaultDataReference.child("Username").setValue(name);
                                    storeUserDefaultDataReference.child("UserStatus").setValue("Hello,I'm on Orbit");
                                    storeUserDefaultDataReference.child("UserImage").setValue("defaultProfile");
                                    storeUserDefaultDataReference.child("userThumbImage").setValue("defaultImage")
                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(mainIntent);
                                                        Toast.makeText(RegisterActivity.this,"Account created successfully",Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                                else {
                                    Toast.makeText(RegisterActivity.this,"Error occurred.Try again...",Toast.LENGTH_LONG).show();
                                }
                                loadingBar.dismiss();
                            }
                        });

        }
   }
}
