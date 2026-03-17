package user;
import java.io.BufferedReader;
import java.io.FileReader;
import main.Main;
public class User {

    public String gmail;
    public String password;
    public User(String gmail,String password){
        this.gmail = gmail;
        this.password = password;
    }

    public static void loadUsers(){
        try {
            FileReader fr = new FileReader("Users.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine())!=null){
                String[] parts = line.split(",");
                String gmail = parts[0];
                String password = parts[1];
                User user = new User(gmail,password);
                Main.users.add(user);
            }
        } catch (Exception e) {
            System.out.println("Unknown error occured "+e);
        }
    }
}
