package com.raj.moneytrackerwithdatabase;

import static android.widget.Toast.*;
import static java.time.ZonedDateTime.now;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.TimeZoneNames;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.C;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {
    Button btnAddCredit,btnAddDebit;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    Toolbar toolbar;
    TextView totalCr,totalDb,txtbalance;
    double totalDebit = 0, totalCredit = 0;
    RecyclerView recyclerView;
    InsertDataAdapter adapter;
    List<InsertData> dataList;
    String username,balance;
    ProgressBar progressMainActivity;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    private static final String CHANNEL_ID="Remainder";
    private static final int NOTIFICATION_ID=100;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ActivityCompat.requestPermissions(this,new String[]{
//                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PackageManager.PERMISSION_GRANTED); // Replace REQUEST_CODE with a unique integer value

        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if(auth.getCurrentUser() != null) {
            username = auth.getCurrentUser().getEmail().replace(".com", "");
        }
        else {
            finish();
        }
        createNotificationChannel();

        //Transaction Recycle View
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapter = new InsertDataAdapter(this, dataList);

        //      Data sort in DateTime Order
//        Collections.sort(dataList, new Comparator<InsertData>() {
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            @Override
//            public int compare(InsertData o1, InsertData o2) {
//                try {
//                    Date date1 = dateFormat.parse(o1.getTime());
//                    Date date2 = dateFormat.parse(o2.getTime());
//                    return date2.compareTo(date1);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                return 0;
//            }
//        });
        recyclerView.setAdapter(adapter);

        totalCr=findViewById(R.id.TotalCredit);
        totalDb=findViewById(R.id.TotalDebit);
        txtbalance=findViewById(R.id.TxtBalane);
        progressMainActivity=findViewById(R.id.progressMainActivity);
        // Initialize Firebase Database
        reference = FirebaseDatabase.getInstance().getReference("Users");
        // Fetch data from Firebase
        reference.child(username).child("Transaction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                totalDebit=0;
                totalCredit=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    InsertData data = snapshot.getValue(InsertData.class);
                    if (data != null) {
                        dataList.add(data);
                        double amt =Double.parseDouble( data.getAmt());
                        if ("Debited".equals(data.getType())) {
                            totalDebit += amt;
                        } else if ("Credited".equals(data.getType())) {
                            totalCredit += amt;
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
                //     set Total Credit and Debit
                totalCr.setText("₹ "+StringToDoubleToString(String.valueOf(totalCredit)));
                totalDb.setText("₹ "+StringToDoubleToString(String.valueOf(totalDebit)));
                balance=String.valueOf(StringToDouble(String.valueOf(totalCredit))-StringToDouble(String.valueOf(totalDebit)));
                progressMainActivity.setVisibility(View.INVISIBLE);
                txtbalance.setText(StringToDoubleToString(balance));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


//      Appbar
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



//      Add Credit & Debit Button
        long maxDate=System.currentTimeMillis();
        Calendar minDate=Calendar.getInstance();
        minDate.add(Calendar.MONTH,-2);
        //Credit Button
        btnAddCredit=findViewById(R.id.btnAddCredit);
        btnAddCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View add_credit = inflater.inflate(R.layout.add_credit, null);

                builder.setView(add_credit);
                DatePicker date;
                date=add_credit.findViewById(R.id.date);
                date.setMaxDate(maxDate);
                date.setMinDate(minDate.getTimeInMillis());
                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText txtDescription,txtAmount;
                                TimePicker time;

                                txtDescription=add_credit.findViewById(R.id.txtDescription);
                                txtAmount=add_credit.findViewById(R.id.txtAmount);
                                time =add_credit.findViewById(R.id.time);

                                time.setIs24HourView(true);
                                String desc = txtDescription.getText().toString();
                                String amt =StringToDoubleToString(txtAmount.getText().toString());
                                String datetime= date.getYear()+"-"+date.getMonth()+"-"+date.getDayOfMonth()+" "+time.getHour()+":"+time.getMinute();

                                if (desc.trim().isEmpty() || amt.trim().isEmpty() || amt.equals("0.00")) {
                                    makeText(MainActivity.this, "Please fill both fields..!", LENGTH_SHORT).show();
                                } else {
                                    // Show dialog with information
                                    Dailog(desc, amt, datetime,"Credited");
                                    makeText(MainActivity.this, datetime, LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //Dedit Button
        btnAddDebit=findViewById(R.id.btnAddDebit);
        btnAddDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();

                View add_debit = inflater.inflate(R.layout.add_credit, null);
                TextView heading=add_debit.findViewById(R.id.heading);
                heading.setText("ADD DEBIT");
                heading.setBackgroundColor(Color.RED);
                builder.setView(add_debit);
                DatePicker date;
                date=add_debit.findViewById(R.id.date);
                date.setMaxDate(maxDate);
                date.setMinDate(minDate.getTimeInMillis());
                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText txtDescription,txtAmount;
                                TimePicker time;


                                txtDescription=add_debit.findViewById(R.id.txtDescription);
                                txtAmount=add_debit.findViewById(R.id.txtAmount);

                                time =add_debit.findViewById(R.id.time);

                                time.setIs24HourView(true);

                                String desc = txtDescription.getText().toString();
                                String amt =StringToDoubleToString(txtAmount.getText().toString());
                                String datetime= date.getYear()+"-"+date.getMonth()+"-"+date.getDayOfMonth()+" "+time.getHour()+":"+time.getMinute();


                                // Show dialog with information
                                if(desc.trim().isEmpty() || amt.trim().isEmpty() || amt.equals("0.00"))
                                {
                                    makeText(MainActivity.this, "Please Fill both fields..!", LENGTH_SHORT).show();
                                }
                                else
                                {
                                        Dailog(desc, amt, datetime, "Debited");
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });



    }







    //    FUNCTION & METHODS DEFINATION

    //    menu related method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id=item.getItemId();

        if(item_id==R.id.action_logout)
        {
            Intent intent=new Intent(MainActivity.this, sign_up.class);
            startActivity(intent);
             auth.signOut();
            finish();
            return true;
        }
        else if (item_id==R.id.action_profile)
        {
            Intent intent=new Intent(MainActivity.this, Profile.class).putExtra("Username",username).putExtra("balance",StringToDoubleToString(balance));
            startActivity(intent);
//            finish();
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }

    }

    //    data insert and toast displaying for add credit or debit
    private void Dailog(String discription, String amount,String datetime,String type){

            InsertData insertData = new InsertData(discription, amount, type, datetime);

            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference("Users");
            // Push data to Firebase
            String parent = datetime + discription;
            reference.child(username).child("Transaction").child(datetime + discription).setValue(insertData);
    }


    //Notification
    private void createNotificationChannel() {
        NotificationManager nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Drawable drawable= ResourcesCompat.getDrawable(getResources(),R.drawable.money_tracker_logo,null);
        BitmapDrawable bitmapDrawable=(BitmapDrawable)drawable;
        Bitmap largeIcon=bitmapDrawable.getBitmap();
        Notification notification;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.money_tracker_logo)
                    .setContentText("Add your today's Transaction")
                    .setChannelId(CHANNEL_ID)
                    .build();
            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "New Channel", NotificationManager.IMPORTANCE_HIGH));
        }
        else {
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.money_tracker_logo)
                    .setSubText("Add your today's Transaction")
                    .setContentText("Money Tracker")
                    .build();
        }
        nm.notify(NOTIFICATION_ID,notification);
    }

    //    String Amount convert in two decimal point value and return string
    private String StringToDoubleToString(String str) {
        if (str == null || str.trim().isEmpty()) {
            // Handle empty string by returning a default value, e.g., "0.00"
            return "0.00";
        }
        // Convert string to double
        double value = Double.parseDouble(str);
        // Format the double to 2 decimal points
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(value);
    }

    //    String Amount convert in two decimal point value and return Double
    private Double StringToDouble(String str) {
        if (str == null || str.trim().isEmpty()) {
            // Handle empty string by returning a default value, e.g., 0.00
            return 0.00;
        }
        // Convert string to double
        double value = Double.parseDouble(str);
        // Format the double to 2 decimal points
        DecimalFormat df = new DecimalFormat("#.00");
        return Double.valueOf(df.format(value));
    }

}