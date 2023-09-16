package com.example.veganing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class MyPageActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_my_page );

        mFirebaseAuth = FirebaseAuth.getInstance();

        ImageButton imageButton11 = findViewById( R.id.imageButton11 );
        imageButton11.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 하기
                mFirebaseAuth.signOut();

                Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                startActivity( intent );
                finish();
            }
        } );
    }
}