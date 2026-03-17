package admin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import main.Main;

public class Admin {

    public String gmail;
    public String password;

    public Admin(String gmail, String password) {
        this.gmail = gmail;
        this.password = password;
    }

    public static void loadAdmins() {
        try {
            FileReader fr = new FileReader("Admin.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String admin = parts[0];
                String password = parts[1];
                Admin libAdmin = new Admin(admin, password);
                Main.admins.add(libAdmin);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

    public static void AddAdmin() {
        Random r = new Random();
        String admin = "admin" + (Main.admins.size() + 1) + "@gmail.com";
        String password = ""+100000 + r.nextInt(9000000);
        Admin libAdmin = new Admin(admin, password);
        Main.admins.add(libAdmin);
        try {
            FileWriter fw = new FileWriter("Admin.txt",true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(admin+","+password);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
