package com.example.blogapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.blogapp.databinding.ActivitySplashBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    GoogleSignInOptions signInOptions;
    GoogleSignInClient signInClient;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_splash);

        binding= ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupsignin();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);


            return insets;

        });
    }

    private void setupsignin() {
        auth = FirebaseAuth.getInstance();
        signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this,signInOptions);
    }

    @Override
    protected void onStart() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!=null){
            //showhomescreen
            startActivity(new Intent(this,DrawerActivity.class));
            finish();
        }else{
            signin();
        }
        super.onStart();
    }

    private void signin() {
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent,100);
    }

    //when he press on any email it should get that email data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            //data ka task milega
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
                //now login is done
                //we are adding user to firebase
                auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if user gets sign in what we want to do
                        //or when he unable to sign in what we are doing
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Login Successful!!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),DrawerActivity.class));
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"Login Failed!!",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }catch (ApiException e){
                e.printStackTrace();
            }
        }
    }
}