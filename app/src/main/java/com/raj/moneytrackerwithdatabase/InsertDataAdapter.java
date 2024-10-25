package com.raj.moneytrackerwithdatabase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class InsertDataAdapter extends RecyclerView.Adapter<InsertDataAdapter.InsertDataViewHolder> {
    private List<InsertData> dataList;
    private Context context;
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth=FirebaseAuth.getInstance();
    private final String user=auth.getCurrentUser().getEmail().replace(".com", "");
    public InsertDataAdapter(Context context, List<InsertData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public InsertDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.transaction_listview,parent,false);
//        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_credit, parent, false); // Inflate add_credit.xml
        return new InsertDataViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull InsertDataViewHolder holder, int position) {
        InsertData data = dataList.get(position);
        holder.desc.setText(data.getDesc());
        holder.amt.setText(data.getAmt());
        holder.type.setText(data.getType());
        if ("Debited".equals(data.getType()))
        {
            holder.amt.setTextColor(Color.RED);
        }
        else {
            holder.amt.setTextColor(Color.GREEN);
        }

        holder.time.setText(data.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Desc :"+data.getDesc()+"\nAmount :"+data.getAmt(), Toast.LENGTH_SHORT).show();
                if (!data.getDesc().equals("balance"))
                    showUpdateDeleteDialog(holder.itemView, position, data);
                }
            });
    }

    private void showUpdateDeleteDialog(View view, final int position, final InsertData data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Options");
        builder.setItems(new String[]{"UPDATE", "DELETE"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Handle update action
                    Intent update=new Intent(context, update_transaction.class)
                            .putExtra("Description",data.getDesc())
                            .putExtra("Amount",data.getAmt())
                            .putExtra("DateTime",data.getTime())
                            .putExtra("Type",data.getType());
                    context.startActivity(update);

                } else if (which == 1) {
                    // Handle delete action
                    String dataKey =data.getTime()+data.getDesc();
                    mDatabase.child("Users").child(user).child("Transaction").child(dataKey).removeValue(); // Replace 'dataKey' with the actual key
                    Toast.makeText(context, "Record Deleted", Toast.LENGTH_SHORT).show();
                    dataList.remove(position);
                    notifyItemRemoved(position); // Update RecyclerView
                }
            }
        });
        builder.create().show();
    }

    public static class InsertDataViewHolder extends RecyclerView.ViewHolder {
        private View dialogView = null;
        TextView desc, amt, type, time;

        public InsertDataViewHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.desc);
            amt = itemView.findViewById(R.id.amt);
            type = itemView.findViewById(R.id.type);
            time = itemView.findViewById(R.id.time);
            this.dialogView = dialogView;
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
