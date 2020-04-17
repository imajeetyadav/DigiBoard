package com.ak47.digiboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ak47.digiboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentUserID;
    ImageView imageView;
    TextView textName, textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        rootRef.keepSynced(true);
        imageView = findViewById(R.id.imageView);
        textName = findViewById(R.id.textViewName);
        textEmail = findViewById(R.id.textViewEmail);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userProfile = Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("profilePic").getValue()).toString());
                textName.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                textEmail.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());

                Picasso.get().load(userProfile).placeholder(R.drawable.profle_pic).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //if the user is not logged in
        //opening the login activity
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

//    private void sendUserToLoginActivity()
//    {
//        Intent loginIntent=new Intent(this, LoginActivity.class);
//        startActivity(loginIntent);
//    }
}
