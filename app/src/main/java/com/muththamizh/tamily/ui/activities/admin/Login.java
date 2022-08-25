package com.muththamizh.tamily.ui.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.muththamizh.tamily.R;
import com.muththamizh.tamily.utils.PrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Login extends AppCompatActivity {

    Unbinder unbinder;
    @BindView(R.id.email) EditText emailAddress;
    @BindView(R.id.password) EditText password;
    private FirebaseAuth mFirebaseAuth;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);
        prefManager = new PrefManager(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailAddress.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @OnClick(R.id.userLogin)
    public void userLogin(){
        String email = emailAddress.getText().toString();
        String pass = password.getText().toString();

        if (email.isEmpty() || email.length() == 0 || email.equalsIgnoreCase("")){
            emailAddress.setError(getString(R.string.invalid_email));
        }else if (pass.isEmpty() || pass.length() == 0 || pass.equalsIgnoreCase("")){
            password.setError(getString(R.string.invalid_password));
        }else {
            emailAddress.setError(null);
            password.setError(null);
            mFirebaseAuth.signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                prefManager.saveBoolean(prefManager.IS_ADMIN_LOGGED_IN,true);
                                Toast.makeText(Login.this, "Login success...!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this,Dashboard.class));
                            }else {
                                Toast.makeText(Login.this, "Login failed...!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}