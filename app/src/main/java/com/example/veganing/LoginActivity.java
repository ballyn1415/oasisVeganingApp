package com.example.veganing;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증 처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtPwd; // 로그인 입력 필드


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        ImageButton loginButton = findViewById( R.id.kakaoLogin );

        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if (oAuthToken != null) {

                }
                if (throwable != null) {

                }
                updateKakaoLoginUi();
                return null;
            }
        };

        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable( LoginActivity.this )) {
                    UserApiClient.getInstance().loginWithKakaoTalk( LoginActivity.this, callback );
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount( LoginActivity.this, callback );
                }
            }
        } );
        updateKakaoLoginUi();


        try {
            Log.d( "getKeyHash", "" + getKeyHash( LoginActivity.this ) );
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException( e );
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference( "Veganing" );

        mEtEmail = findViewById( R.id.id_textView );
        mEtPwd = findViewById( R.id.pwd_textView );


        Button button = findViewById( R.id.button );
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 요청
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword( strEmail, strPwd ).addOnCompleteListener( LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //로그인 성공 -> 이동할 페이지 수정하기
                            Intent intent = new Intent( LoginActivity.this, MyPageActivity.class );
                            startActivity( intent );
                            finish(); // 현재 액티비티 파괴
                        } else {
                            Toast.makeText( LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT ).show();


                        }
                    }
                } );
            }
        } );


        button = findViewById( R.id.button3 );
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( LoginActivity.this, RegisterActivity.class );
                startActivity( intent );
            }
        } );


//        Button developer_info_btn = (Button) findViewById( R.id.button3 );
//        developer_info_btn.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
//                startActivity( intent );
//            }
//        } );


//        Button Button3 = (Button) findViewById( R.id.button3 );
//        Button3.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class );
//                startActivity( intent );
//            }
//        }  );
    }

    private void updateKakaoLoginUi() {
        UserApiClient.getInstance().me( new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                // 로그인이 되어있으면
                if (user != null) {
                    // 유저의 아이디
                    Log.d( TAG, "invoke: id" + user.getId() );
                    // 유저의 어카운트정보에 이메일
                    Log.d( TAG, "invoke: nickname" + user.getKakaoAccount().getEmail() );
                    // 유저의 어카운트 정보의 프로파일에 닉네임
                    Log.d( TAG, "invoke: email" + user.getKakaoAccount().getProfile().getNickname() );
                    // 유저의 어카운트 파일의 성별
                    Log.d( TAG, "invoke: gerder" + user.getKakaoAccount().getGender() );
                    // 유저의 어카운트 정보에 나이
                    Log.d( TAG, "invoke: age" + user.getKakaoAccount().getAgeRange() );

//                    nickName.setText( user.getKakaoAccount().getProfile().getNickname() );

//                    Glide.with( profileImage ).load( user.getKakaoAccount().
//                            getProfile().getProfileImageUrl() ).circleCrop().into( profileImage );
//                    loginButton.setVisibility( View.GONE );
//                    logoutButton.setVisibility( View.VISIBLE );
                } else {
                    // 로그인이 되어 있지 않다면 위와 반대로
//                    nickName.setText( null );
//                    profileImage.setImageBitmap( null );
//                    loginButton.setVisibility( View.VISIBLE );
//                    logoutButton.setVisibility( View.GONE );
                }
                return null;
            }
        } );
    }

    public String getKeyHash(final Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo PackageInfo = pm.getPackageInfo( context.getPackageName(), PackageManager.GET_SIGNATURES );
            if (PackageInfo.packageName == null)
                return null;

            for (Signature signature : PackageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance( "SHA" );
                    md.update( signature.toByteArray() );
                    return Base64.encodeToString( md.digest(), Base64.NO_WRAP );
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}