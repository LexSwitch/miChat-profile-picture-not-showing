package com.nalexander240.michat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView settingsDisplayProfileImage;
    private TextView settingsDisplayName,settingsDisplayStatus;
    private Button settingsChangeProfileImageButton,settingsChangeStatusButton;

    final static int galleryPick=1;
    private StorageReference storeProfileImagestorageRef;

    private DatabaseReference getUserDataReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth=FirebaseAuth.getInstance();
        String onlineUserID=mAuth.getCurrentUser().getUid();
        getUserDataReference= FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserID);
        storeProfileImagestorageRef=FirebaseStorage.getInstance().getReference().child("Profile_Images");

        settingsDisplayProfileImage=(CircleImageView) findViewById(R.id.profileImage);
        settingsDisplayName=(TextView) findViewById(R.id.settingsUsername);
        settingsDisplayStatus=(TextView) findViewById(R.id.settingsUserStatus);
        settingsChangeProfileImageButton=(Button) findViewById(R.id.settingsChangeProfileImageButton);
        settingsChangeStatusButton=(Button) findViewById(R.id.settingsChangeProfileStatusButton);

        getUserDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String name=dataSnapshot.child("Username").getValue().toString();
                String status=dataSnapshot.child("UserStatus").getValue().toString();
                String image=dataSnapshot.child("UserImage").getValue().toString();
                String thumbImage=dataSnapshot.child("userThumbImage").getValue().toString();

                settingsDisplayName.setText(name);
                settingsDisplayStatus.setText(status);
                Picasso.get().load(image).into(settingsDisplayProfileImage);
                //Glide.with(SettingsActivity.this).load(image).into(settingsDisplayProfileImage);







            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        settingsChangeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryPick);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==galleryPick && resultCode==RESULT_OK && data!=null){
            Uri ImageUri=data.getData();

            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                String userID=mAuth.getCurrentUser().getUid();
                StorageReference filePath=storeProfileImagestorageRef.child(userID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this,
                                    "Saving your profile image online...",Toast.LENGTH_LONG).show();

                            String downloadUrl=task.getResult().getStorage().getDownloadUrl().toString();


                            getUserDataReference.child("UserImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            Toast.makeText(SettingsActivity.this,
                                                    "Profile image uploaded successfully.",Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                        else {

                            Toast.makeText(SettingsActivity.this,
                                    "Error occurred while uploading profile pic,try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
