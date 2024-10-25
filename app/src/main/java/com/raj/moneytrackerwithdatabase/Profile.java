package com.raj.moneytrackerwithdatabase;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Profile extends AppCompatActivity {
    private LinearLayout logOut,personalInfo,changePwd,notificationReminder,deleteAccount,aboutUs,pdfStatement;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
    private final String user=auth.getCurrentUser().getEmail().replace(".com", "");
    private TextView textUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        textUsername=findViewById(R.id.textUsername);
        Intent intent=getIntent();
        String balance=intent.getStringExtra("balance");


//        SET USERNAME
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabase.child(user).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        final String username = String.valueOf(dataSnapshot.child("username").getValue());
                        textUsername.setText(username);
                    }
                }
            }
        });



//        PERSONAL DETAILS
        personalInfo = findViewById(R.id.personalInfo);
        personalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference;
                reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(user).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                DataSnapshot dataSnapshot = task.getResult();
                                final String email = user+".com";
                                final String contact = String.valueOf(dataSnapshot.child("number").getValue());
                                final String user=String.valueOf(dataSnapshot.child("username").getValue());

                                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                                LayoutInflater inflater = getLayoutInflater();

                                View personaldetails = inflater.inflate(R.layout.personaldetails, null);
                                builder.setView(personaldetails);

                                TextView tvusername = personaldetails.findViewById(R.id.txtusername);
                                tvusername.setText(user);
                                TextView tvbalance = personaldetails.findViewById(R.id.txtbalance);
                                tvbalance.setText(balance);
                                TextView tvemail = personaldetails.findViewById(R.id.txtemail);
                                tvemail.setText(email);
                                TextView tvcontact = personaldetails.findViewById(R.id.txtcontact);
                                tvcontact.setText(contact);

                                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }
                    }
                });
            }
        });



//        PASSWORD RESET
        changePwd=findViewById(R.id.changePwd);
        changePwd.setOnClickListener(new View.OnClickListener() {
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
                            makeText(Profile.this, "Please enter your registered Email Address"+resetmail.getText().toString().trim(), LENGTH_SHORT).show();
                        }
                        else {
                            auth.sendPasswordResetEmail(resetmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    makeText(Profile.this, "Link Sent to your email", LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    makeText(Profile.this, "Resent Link not sent"+e.getMessage(), LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                pwdResetDailog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                pwdResetDailog.create().show();
            }
        });


//        PDF STATEMENT
        pdfStatement = findViewById(R.id.pdfStatement);
        pdfStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                View pdf_statementView = inflater.inflate(R.layout.pdf_statement, null);

                TextView textViewFromDate = pdf_statementView.findViewById(R.id.textViewFromDate);
                TextView textViewToDate = pdf_statementView.findViewById(R.id.textViewToDate);

                // Set click listeners to show DatePickerDialog and set date to TextViews
                textViewFromDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog((TextView) v);
                    }
                });

                textViewToDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog((TextView) v);
                    }
                });

                // Build the alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setView(pdf_statementView);

                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!textViewFromDate.getText().toString().isEmpty() || !textViewToDate.getText().toString().isEmpty()) {
                            makeText(Profile.this, "From :" + textViewFromDate.getText().toString() + "\nTo :" + textViewToDate.getText().toString(), LENGTH_SHORT).show();
                        }else {
                            makeText(Profile.this, "Select Date", LENGTH_SHORT).show();
                        }
                        }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });


//        ADD NOTIFICATION REMINDER
        notificationReminder=findViewById(R.id.notificationReminder);
        notificationReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeText(Profile.this, "Feature coming soon", LENGTH_SHORT).show();
            }
        });


//        ABOUT US
        aboutUs=findViewById(R.id.aboutUs);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://rajpatelportfolio.netlify.app/"));
                startActivity(intent);
            }
        });


//        LOG OUT
        logOut=findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,sign_up.class));
                auth.signOut();
                finishAffinity();
            }
        });



//        DELETE ACCOUNT
        deleteAccount=findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("Delete Account");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDatabase.child("Users").child(user).removeValue();
                                makeText(Profile.this, "User Deleted", LENGTH_SHORT).show();
                                startActivity(new Intent(Profile.this,sign_up.class));
                                finishAffinity();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeText(Profile.this, "Task Fail", LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
            }
        });
    }




    // Function to show DatePickerDialog and set selected date to TextView
    private void showDatePickerDialog(final TextView textView) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Define min and max dates
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.YEAR, year - 1); // One year before today
        Calendar maxDate = Calendar.getInstance(); // Today's date

        DatePickerDialog datePickerDialog = new DatePickerDialog(Profile.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        textView.setText(selectedDate);
                    }
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }


//    private boolean validateDates(String fromDate, String toDate) {
//        // Use a library like Joda-Time (https://www.joda.org/joda-time/) for robust date parsing and manipulation
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE; // Or your preferred date format
//
//        try {
//            DateTime fromDateTime = formatter.parseDateTime(fromDate);
//            DateTime toDateTime = formatter.parseDateTime(toDate);
//
//            // Calculate the difference in months
//            Months months = Months.monthsBetween(fromDateTime, toDateTime);
//            int monthDiff = months.getMonths();
//
//            return monthDiff <= 2; // Ensure maximum two-month gap
//
//        } catch (IllegalArgumentException e) {
//            // Handle invalid date format
//            return false;
//        }
//    }


        // Function to check if a date is within a specific range
//        private boolean isWithinDateRange(String dateTime, String fromDate, String toDate) {
//            // Implement logic to check if dateTime falls within the range of fromDate and toDate
//            // You can use libraries or a date parsing approach
//            return true; // Replace with your date range validation logic












//        private void createPdf(List<BankStatementEntry> entries) {
//
//            // PDF document creation
//            PdfDocument pdfDocument = new PdfDocument();
//            PdfDocument.Page page = pdfDocument.startPage();
//
//            // Canvas for drawing on the page
//            Canvas canvas = page.getCanvas();
//
//            // Paint object for formatting text and shapes
//            Paint paint = new Paint();
//
//            // Set font, text size, and color
//            paint.setTextSize(20);
//            paint.setColor(Color.BLACK);
//
//            // Header section
//            canvas.drawText("Bank Statement", canvas.getWidth() / 2, 50, paint);
//
//            // Table header row
//            float tableLeft = 20;
//            float tableTop = 100;
//            float columnWidth1 = 50; // Adjust column widths as needed
//            float columnWidth2 = 200;
//            float columnWidth3 = 150;
//            float columnWidth4 = 100;
//            float columnWidth5 = 100;
//            paint.setColor(Color.GRAY);
//            canvas.drawRect(tableLeft, tableTop, tableLeft + columnWidth1 + columnWidth2 + columnWidth3 + columnWidth4 + columnWidth5, tableTop + 20, paint);
//            canvas.drawText("No.", tableLeft + columnWidth1 / 2, tableTop + 15, paint);
//            canvas.drawText("Description", tableLeft + columnWidth1 + columnWidth2 / 2, tableTop + 15, paint);
//            canvas.drawText("Date & Time", tableLeft + columnWidth1 + columnWidth2 + columnWidth3 / 2, tableTop + 15, paint);
//            canvas.drawText("Debit Amount", tableLeft + columnWidth1 + columnWidth2 + columnWidth3 + columnWidth4 / 2, tableTop + 15, paint);
//            canvas.drawText("Credit Amount", tableLeft + columnWidth1 + columnWidth2 + columnWidth3 + columnWidth4 + columnWidth5 / 2, tableTop + 15, paint);
//
//            // Table data rows
//            tableTop += 25; // Adjust for spacing between header and data
//            for (int i = 0; i < entries.size(); i++) {
//                BankStatementEntry entry = entries.get(i);
//
//                canvas.drawText(String.valueOf(entry.id), tableLeft, tableTop, paint);
//                canvas.drawText(entry.description, tableLeft + columnWidth1, tableTop, paint);
//                canvas.drawText(entry.dateTime, tableLeft + columnWidth1 + columnWidth2, tableTop, paint);
//
//                // Format and draw debit/credit amounts appropriately
//                String debitAmountString = String.format("%.2f", entry.debitAmount);
//                String creditAmountString = String.format("%.2f", entry.creditAmount);
//                if (entry.debitAmount > 0) {
//                    canvas.drawText(debitAmountString, tableLeft + columnWidth1 + columnWidth2 + columnWidth3 + columnWidth4 / 2 - paint.measureText(debitAmountString) / 2, tableTop, paint);
//                } else {
//                    canvas.drawText(creditAmountString, tableLeft + columnWidth1 + columnWidth2 + columnWidth3 + columnWidth4 / 2 - paint.measureText(creditAmountString) / 2, tableTop, paint);
//                }
//
//                tableTop += 20; // Adjust for spacing between rows
//            }
//
//            // Finalize and save the PDF (replace with your desired location and logic)
//            pdfDocument.finishPage(page);
//            File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "bank_statement.pdf");
//            try {
//                pdfDocument.writeTo(new FileOutputStream(pdfFile));
//                Toast.makeText(getApplicationContext(), "PDF generated successfully!", Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                Log.e("PDF", "Error creating PDF: ", e);
//            }
//
//            pdfDocument.close();
//        }
//    public void generatePDF(String fromDate, String toDate) {
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference("Users").child();
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<BankStatementEntry> entries = new ArrayList<>();
//                if (snapshot.exists()) {
//                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
//                        int id = childSnapshot.child("id").getValue(Integer.class);
//                        String description = childSnapshot.child("description").getValue(String.class);
//                        String dateTime = childSnapshot.child("dateTime").getValue(String.class);
//                        double debitAmount = childSnapshot.child("debitAmount").getValue(Double.class);
//                        double creditAmount = childSnapshot.child("creditAmount").getValue(Double.class);
//
//                        // Filter data based on user-provided dates
//                        entries.add(new BankStatementEntry(id, description, dateTime, type, amount));
//
//                    }
//                }
//
//                // Create and populate PDF using the fetched entries
//                createPdf(entries); // Call PDF creation function
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", "Error fetching data: ", error.toException());
//            }
//        });
//    }

        }