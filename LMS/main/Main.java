package main;

import admin.Admin;
import admin.AdminService;
import authentication.*;
import books.Books;
import borrow.BorrowRecord;
import java.util.*;
import user.User;
import user.UserService;

public class Main {

    public static ArrayList<Admin> admins = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static HashMap<String, Books> books = new HashMap<>();
    public static HashMap<String, ArrayList<String>> carts = new HashMap<>();
    public static ArrayList<BorrowRecord> checkouts = new ArrayList<>();
    public static HashMap<String, ArrayList<String>> fineHistory = new HashMap<>();

    private static void loadAllData() {
        admins.clear();
        users.clear();
        books.clear();
        checkouts.clear();
        fineHistory.clear();

        Admin.loadAdmins();
        UserService.loadUsers();
        Books.loadBooks();
        UserService.loadCheckouts();
        UserService.loadFineHistory();
    }

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        loadAllData();
        boolean exit = false;
        int input;
        while (!exit) {
            System.out.println("----------LMS---------");
            System.out.println("1. Admin");
            System.out.println("2. User");
            System.out.println("3. Exit");
            input = sc.nextInt();
            sc.nextLine();
            switch (input) {
                case 1: {
                    System.out.print("Enter the mail id:");
                    String gmail = sc.nextLine();
                    System.out.print("\nEnter the password:");
                    String pass = sc.nextLine();
                    if (Authenticate.admin(gmail, pass)) {
                        System.out.println("\nSuccessfull login...\n");
                        AdminService.menu();
                    } else {
                        System.out.println("\nInvalid credentials");
                    }
                    break;
                }
                case 2: {
                    System.out.print("\nEnter the mail id:");
                    String gmail = sc.nextLine();
                    System.out.println("\nEnter the password:");
                    String pass = sc.nextLine();
                    if (Authenticate.user(gmail, pass)) {
                        System.out.println("\nSuccessfull login..");
                        UserService.menu(gmail);
                    } else {
                        System.out.println("\nInvalid credentials");
                    }
                    break;
                }
                case 3: {
                    exit = true;
                    break;
                }
                default: {
                    System.out.println("Invalid option. Try again.");
                }
            }
        }
    }
}
