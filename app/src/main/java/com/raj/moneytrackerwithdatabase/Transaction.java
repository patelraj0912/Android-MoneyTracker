public class Transaction {
    public String amt;
    public String desc;
    public String time;
    public String type;

    // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    public Transaction() {
    }

    public Transaction(String amt, String desc, String time, String type) {
        this.amt = amt;
        this.desc = desc;
        this.time = time;
        this.type = type;
    }

    // Getters and setters
    public String getAmt() { return amt; }
    public void setAmt(String amt) { this.amt = amt; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
