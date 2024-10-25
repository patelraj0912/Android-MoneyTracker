package com.raj.moneytrackerwithdatabase;

public class InsertUser {
    String username,email,password;
String balance,contact;
    public InsertUser(String balance, String contact) {
//        this.username = username;
        this.balance = balance;
//        this.email = email;
        this.contact = contact;
//        this.password = password;
    }

    public InsertUser() {
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
}
