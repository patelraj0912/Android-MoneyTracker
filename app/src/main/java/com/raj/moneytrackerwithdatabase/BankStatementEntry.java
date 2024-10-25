package com.raj.moneytrackerwithdatabase;

public class BankStatementEntry {
    int id;
    String description;
    String dateTime;
    String type;
    double amount;

    public BankStatementEntry(int id, String description, String dateTime, String type, double amount) {
        this.id = id;
        this.description = description;
        this.dateTime = dateTime;
        this.type = type;
        this.amount = amount;
    }
}
