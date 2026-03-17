public class User {

    private long accountNo;
    private int pinNo;
    private double bal;

    public User(long accountNo, int pinNo, double bal) {
        this.accountNo = accountNo;
        this.pinNo = pinNo;
        this.bal = bal;
    }

    public long getAccountNo() {
        return accountNo;
    }

    public int getPin() {
        return pinNo;
    }

    public void setPin(int newPin) {
        this.pinNo = newPin;
    }

    public double getBalance() {
        return bal;
    }

    public void deposit(double amount) {
        bal += amount;
    }

    public boolean withdraw(double amount) {

        if (bal >= amount) {
            bal -= amount;
            return true;
        }

        return false;
    }
}