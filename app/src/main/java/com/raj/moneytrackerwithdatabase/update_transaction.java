package com.raj.moneytrackerwithdatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class update_transaction extends AppCompatActivity {
String desc,amt,datetime,type;
EditText etDescription,etAmount;
RadioButton radioCr,radioDb,radioType;
RadioGroup btnRadio;
private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
FirebaseAuth auth=FirebaseAuth.getInstance();
private final String user=auth.getCurrentUser().getEmail().replace(".com", "");
AppCompatButton btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_transaction);

        desc=getIntent().getStringExtra("Description");
        amt=getIntent().getStringExtra("Amount");
        datetime=getIntent().getStringExtra("DateTime");
        type=getIntent().getStringExtra("Type");

        etDescription=findViewById(R.id.etDescription);
        etAmount=findViewById(R.id.etAmount);
        btnUpdate=findViewById(R.id.btnUpdate);
        radioCr=findViewById(R.id.radioCr);
        radioDb=findViewById(R.id.radioDb);



        etDescription.setText(desc);
        etAmount.setText(amt);

        if(type.equals("Credited"))
            radioCr.setChecked(true);
        else if (type.equals("Debited"))
            radioDb.setChecked(true);



        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etDescription.getText().toString().equals("balance")) {
                    if(radioCr.isChecked())
                        type="Credited";
                    else if (radioDb.isChecked())
                        type="Debited";
                    HashMap<String, Object> newvalue = new HashMap<>();
                    newvalue.put("amt", etAmount.getText().toString());
                    newvalue.put("desc", etDescription.getText().toString());
                    newvalue.put("type", type);

                    if(desc.equals(etDescription.getText().toString())) {
                        String dataKey = datetime + desc;
                        mDatabase.child("Users").child(user).child("Transaction").child(dataKey).updateChildren(newvalue).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(update_transaction.this, "Transaction Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(update_transaction.this, "Task Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }
                    else {
                        newvalue.put("time", datetime);
                        String dataKey =datetime+desc;
                        mDatabase.child("Users").child(user).child("Transaction").child(dataKey).removeValue(); // Replace 'dataKey' with the actual key


                        dataKey=datetime+etDescription.getText().toString();
                        mDatabase.child("Users").child(user).child("Transaction").child(dataKey).setValue(newvalue).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(update_transaction.this, "Transaction Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(update_transaction.this, "Task Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }
                }
                else {
                    Toast.makeText(update_transaction.this, "Please Change Description", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}