package user;
import java.io.*;
import java.util.*;
import main.Main;
public class UserService {
    public static void AddBorrower(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the User Email Id: ");
        String email = sc.nextLine();
        System.out.println("Enter the password: ");
        String password = sc.nextLine();
        User user = new User(email,password);
        Main.users.add(user);
        try {
            FileWriter fw = new FileWriter("Users.txt",true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(email+","+password);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void loadUsers(){
        try {
            FileReader fr = new FileReader("Users.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line=br.readLine())!=null){
                String[] parts = line.split(",");
                String email = parts[0];
                String password = parts[1];
                User user = new User(email,password);
                Main.users.add(user);
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

