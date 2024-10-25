package com.raj.moneytrackerwithdatabase;

public class InsertData {
    String desc,type,time,amt;
//    Double amt;

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }


    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAmt() {
        return amt;
    }
    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public InsertData(String desc, String amt, String type, String time ) {
        this.desc = desc;
        this.amt = amt;
        this.type = type;
        this.time = time;
    }

    public InsertData(){

    }
}
