package com.raj.moneytrackerwithdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class signIn extends AppCompatActivity {

    private TextView txtSignUp,txtForgotPwd;
    private EditText etUserName,etPassword;
    private ProgressBar progressSignIn;
    private AppCompatButton btnSignIn;
    private ImageView togglePasswordImageView;
    private boolean isPasswordVisible = false;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        progressSignIn=findViewById(R.id.progressSignin);
        txtSignUp=findViewById(R.id.txtSignUp);
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(signIn.this, sign_up.class);
                startActivity(intent);
                finish();
            }
        });

//     Password Show and Hide
        togglePasswordImageView=findViewById(R.id.togglePasswordImageView);
        togglePasswordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPassword=findViewById(R.id.etPassword);
                if (etPassword.getText().toString() != null) {
                    if (isPasswordVisible) {
                        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        togglePasswordImageView.setImageResource(R.drawable.ic_show_eye_24);
                    } else {
                        etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        togglePasswordImageView.setImageResource(R.drawable.ic_hide_eye);
                    }
                    // Toggle the boolean flag
                    isPasswordVisible = !isPasswordVisible;
                    // Move cursor to the end of the text
                    etPassword.setSelection(etPassword.getText().length());
                }
            }
        });


//      User Sign Up
        btnSignIn=findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUserName=findViewById(R.id.etUsername);
                etPassword=findViewById(R.id.etPassword);
                if(validation(etUserName.getText().toString(),etPassword.getText().toString())==true) {
                    progressSignIn.setVisibility(View.VISIBLE);
                    signinUser();
                }
            }
        });

//        Reset Password
        txtForgotPwd=findViewById(R.id.txtForgotPwd);
        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetmail=new EditText(v.getContext());
                AlertDialog.Builder pwdResetDailog=new AlertDialog.Builder(v.getContext());
                pwdResetDailog.setTitle("Reset Password ?");
                pwdResetDailog.setMessage("Enter Email");
                pwdResetDailog.setView(resetmail);
                String email = resetmail.getText().toString().trim();

                pwdResetDailog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (resetmail.getText().toString().trim().isEmpty()) {
                            Toast.makeText(signIn.this, "Please enter your registered Email Address"+resetmail.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            auth.sendPasswordResetEmail(resetmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(signIn.this, "Link Sent to your email", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(signIn.this, "Resent Link not sent"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                pwdResetDailog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        pwdResetDailog.
                    }
                });
                pwdResetDailog.create().show();
            }
        });
    }

    private void signinUser() {
//        reference= FirebaseDatabase.getInstance().getReference("Users");
        auth=FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(etUserName.getText().toString(),etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(signIn.this, "Signed In", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("user",emailtouser(etUserName.getText().toString())));
                    finish();

                }
                else {
                    Toast.makeText(signIn.this, "Error !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressSignIn.setVisibility(View.INVISIBLE);
                }
            }
        });
//        reference.child(etUserName.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful()){
//                    if(task.getResult().exists()){
//                        DataSnapshot dataSnapshot=task.getResult();
//                        final String pwd=String.valueOf(dataSnapshot.child("password").getValue());
//                        String balance=String.valueOf(dataSnapshot.child("balance").getValue());
//                        if(etPassword.getText().toString().equals(pwd)) {
//                            Toast.makeText(signIn.this, "Sign In Succesfully", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(signIn.this, MainActivity.class).putExtra("Username",etUserName.getText().toString()).putExtra("Balance",balance);
//                            startActivity(intent);
//                            finish();
//                        }
//                        else {
//                            Toast.makeText(signIn.this, "Wrong Cardentials...!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else {
//                        Toast.makeText(signIn.this, "User doesn't Exist", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                    Toast.makeText(signIn.this, "Task Failed", Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

    private void forgetPassword() {
//        EditText resetmail=new EditText(v.getContext());
//                AlertDialog.Builder pwdResetDailog=new AlertDialog.Builder(v.getContext());
//                pwdResetDailog.setTitle("Reset Password ?");
//                pwdResetDailog.setMessage("Enter Email");
//                pwdResetDailog.setView(resetmail);
//        String email = resetmail.getText().toString().trim();
//
//        if (TextUtils.isEmpty(email)) {
//            Toast.makeText(this, "Please enter your registered Email Address", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
////        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
////            Toast.makeText(this, "Please enter a valid Email Address", Toast.LENGTH_SHORT).show();
////            return;
////        }
//
//        auth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
//                        finish();
//                    } else {
//                        String errorMessage = task.getException().getMessage();
//                        if (errorMessage.contains("no user record")) {
//                            Toast.makeText(this, "Email not registered", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//        pwdResetDailog.create().show();

    }


    public String emailtouser(String email) {
        String original = email;
        return original.replace(".com", "");
    }
    public boolean validation(String username,String pswd) {
        int temp = 1;
        if (username.trim().isEmpty()) {
            etUserName.setError("Please fill Username");
            temp = 0;
        }
        if (pswd.trim().isEmpty()) {
            etPassword.setError("Please fill Password");
            temp = 0;
        }

        if (temp == 0)
            return false;
        else
            return true;
    }
}