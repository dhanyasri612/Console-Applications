package user;

import java.io.BufferedReader;
import java.io.FileReader;
import main.Main;

public class User {

    public String gmail;
    public String password;
    public double deposit;
    public double fineLimit;

    public User(String gmail, String password, double deposit) {
        this(gmail, password, deposit, 1000.0);
    }

    public User(String gmail, String password, double deposit, double fineLimit) {
        this.gmail = gmail;
        this.password = password;
        this.deposit = deposit;
        this.fineLimit = fineLimit;
    }

    public static void loadUsers() {
        try {
            FileReader fr = new FileReader("Users.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String gmail = parts[0];
                String password = parts[1];
                double deposit = Double.parseDouble(parts[2]);
                double fineLimit = parts.length > 3 ? Double.parseDouble(parts[3]) : 1000.0;
                User user = new User(gmail, password, deposit, fineLimit);
                Main.users.add(user);
            }
        } catch (Exception e) {
            System.out.println("Unknown error occured " + e);
        }
    }
}
