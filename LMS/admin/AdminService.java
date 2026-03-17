package admin;
import books.Books;
import java.util.*;
import user.*;

public class AdminService {
    
    public static void menu(){
        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        Books.loadBooks();
        while(!exit){
            System.out.println("-----------Admin----------");
            System.out.println("1.Add Book");
            System.out.println("2.Modify Book");
            System.out.println("3.Delete Book");
            System.out.println("4.Add Admin");
            System.out.println("5.Add Borrower");
            System.out.println("6.View Book");
            System.out.println("7.Search Book");
            System.out.println("8.Log out");
            int choice = sc.nextInt();
            switch(choice){
                case 1:{
                    Books.AddBooks();
                    break;
                }
                case 2:{
                    Books.ModifyBooks();
                    break;
                }
                case 3:{
                    Books.Delete();
                    break;
                }
                case 4:{
                    Admin.AddAdmin();
                    break;
                }
                case 5:{
                    UserService.AddBorrower();
                    break;
                }
                case 6:{
                    Books.ViewBooks();
                    break;
                }
                case 7:{
                    Books.SearchBooks();
                    break;
                }
                case 8:{
                    exit = true;
                    break;
                }
                default:{
                    System.out.println("Enter the correct choice number");
                }
            }
        }
    }
}
