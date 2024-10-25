package com.raj.moneytrackerwithdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sign_up extends AppCompatActivity {
    private TextView txtSignIn;
    private AppCompatButton btnSignUp;
    private EditText etUsername,etBalance,etEmail,etContactNumber,etPassword,etConfirmPassword;
    private ImageView togglePasswordImageView,toggleConfirmPasswordImageView;
    private boolean isPasswordVisible = false,isConfirmPasswordVisible=false;
    private FirebaseDatabase db;
    private ProgressBar progressSignUp;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressSignUp=findViewById(R.id.progressSignUp);


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        txtSignIn=findViewById(R.id.txtSignIn);
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sign_up.this, signIn.class);
                startActivity(intent);
                finish();
            }
        });

//        Password Show Hide
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

        toggleConfirmPasswordImageView=findViewById(R.id.toggleConfirmPasswordImageView);
        toggleConfirmPasswordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etConfirmPassword=findViewById(R.id.etConfirmPassword);
                if(etConfirmPassword.getText().toString() != null) {
                    if (isConfirmPasswordVisible) {
                        etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        toggleConfirmPasswordImageView.setImageResource(R.drawable.ic_show_eye_24);
                    } else {
                        etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        toggleConfirmPasswordImageView.setImageResource(R.drawable.ic_hide_eye);
                    }
                    // Toggle the boolean flag
                    isConfirmPasswordVisible = !isConfirmPasswordVisible;
                    // Move cursor to the end of the text
                    etConfirmPassword.setSelection(etConfirmPassword.getText().length());
                }
            }
        });

//        Sign Up
        btnSignUp=findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername = findViewById(R.id.etUsername);
                etBalance = findViewById(R.id.etBalance);
                etEmail = findViewById(R.id.etEmail);
                etContactNumber = findViewById(R.id.etContactNumber);
                etPassword = findViewById(R.id.etPassword);
                etConfirmPassword = findViewById(R.id.etConfirmPassword);

                if (validationsignup(etUsername.getText().toString(), etBalance.getText().toString(), etEmail.getText().toString(), etContactNumber.getText().toString(), etPassword.getText().toString(), etConfirmPassword.getText().toString()) == true)
                {
                    progressSignUp.setVisibility(View.VISIBLE);
                    signUpUser();
                }
            }
        });
    }

    private void signUpUser() {


        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(etUsername.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        Toast.makeText(sign_up.this, "User already Exist", Toast.LENGTH_SHORT).show();
                    }
                    else {
//                        InsertUser insertUser=new InsertUser(etBalance.getText().toString(), etContactNumber.getText().toString());


                        auth=FirebaseAuth.getInstance();
                        auth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
//                                    Toast.makeText(sign_up.this, "Successfully Authentication", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(sign_up.this, "Sign Up Succesfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(sign_up.this, signIn.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    progressSignUp.setVisibility(View.INVISIBLE);
                                    Toast.makeText(sign_up.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        db=FirebaseDatabase.getInstance();
                        reference=db.getReference("Users");
                        reference.child(emailtouser(etEmail.getText().toString())).child("number").setValue(etContactNumber.getText().toString());
                        reference.child(emailtouser(etEmail.getText().toString())).child("username").setValue(etUsername.getText().toString());

                        ZonedDateTime currentDateTimeUTC = ZonedDateTime.now(ZoneOffset.UTC);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");// Format the date and time in a professional manner
                        String formattedDateTime = currentDateTimeUTC.format(formatter);// Convert the current date and time to a formatted string

                        InsertData insertData = new InsertData("balance", etBalance.getText().toString(), "Credited",formattedDateTime);
                        String parent=currentDateTime()+"balance";
                        reference.child(emailtouser(etEmail.getText().toString())).child("Transaction").child(parent).setValue(insertData);

//                        Toast.makeText(sign_up.this, "Sign Up Succesfully", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(sign_up.this, signIn.class);
//                        startActivity(intent);
//                        finish();
                    }
                }
                else
                    progressSignUp.setVisibility(View.INVISIBLE);
//                    Toast.makeText(sign_up.this, "Task Failed", Toast.LENGTH_SHORT).show();
            }
        });
        }

    public String emailtouser(String email) {
        String original = email;
        return original.replace(".com", "");
    }

    public boolean validationsignup(String username,String balance,String email,String number,String pwd,String cnfpwd) {
        int temp=1;
        if(username.trim().isEmpty()) {
            etUsername.setError("Please fill Username");
            temp = 0;
        }
        if(balance.trim().isEmpty() || Double.parseDouble(balance)<0) {
            etBalance.setError("Please fill Balance");
            temp = 0;
        }
        if(email.trim().isEmpty() || !email.contains("@gmail.com")) {
            etEmail.setError("Please fill Email");
            temp = 0;
        }
        if(number.trim().isEmpty() || number.trim().length()!=10) {
            etContactNumber.setError("Please fill Contact Number");
            temp = 0;
        }
        if(pwd.trim().isEmpty() || isValidPassword(pwd)==false) {
            etPassword.setError("Please fill Password\nPassword Contain Special symbol,At least one Uppercase and Lowercase charachter,Digit");
            temp = 0;
        }
        if(cnfpwd.trim().isEmpty()) {
            etConfirmPassword.setError("Please fill ConfirmPassword");
            temp = 0;
        }
        if (!cnfpwd.equals(pwd))
        {
            etConfirmPassword.setError("Password and confirmPassword must be same");
            temp = 0;
        }

        if (temp==0)
            return false;
        else
            return true;
    }

    public static boolean isValidPassword(String password) {
        // Check length
        if (password.length() < 8) {
            return false;
        }

        // Check for special character, number, uppercase, and lowercase letters
        Pattern specialCharPattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>/]");
        Pattern digitPattern = Pattern.compile("\\d");
        Pattern uppercasePattern = Pattern.compile("[A-Z]");
        Pattern lowercasePattern = Pattern.compile("[a-z]");

        Matcher specialCharMatcher = specialCharPattern.matcher(password);
        Matcher digitMatcher = digitPattern.matcher(password);
        Matcher uppercaseMatcher = uppercasePattern.matcher(password);
        Matcher lowercaseMatcher = lowercasePattern.matcher(password);

        // Check if all conditions are met
        return specialCharMatcher.find() && digitMatcher.find() && uppercaseMatcher.find() && lowercaseMatcher.find();
    }

    private String currentDateTime(){
        LocalDateTime now = LocalDateTime.now();

        // Format the date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = formatter.format(now);
        return formattedDateTime;

    }


}