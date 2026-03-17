package authentication;
import admin.Admin;
import main.Main;
import user.User;

public class Authenticate {
    public static boolean admin(String gmail,String password){
        for(Admin ad : Main.admins){
            if(ad.gmail.equals(gmail) && ad.password.equals(password)){
                return true;
            }
        }
        return false;
    }
    public static boolean user(String gmail,String password){
        for(User user : Main.users){
            if(user.gmail.equals(gmail) && user.password.equals(password)){
                return true;
            }
        }
        return false;
    }
}
