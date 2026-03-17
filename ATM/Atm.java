import java.util.*;

public class Atm {

    static ArrayList<User> accounts = new ArrayList<>();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserService.loadAccounts();

        boolean exit = false;

        while (!exit) {

            System.out.println("\n------ ATM SYSTEM ------");
            System.out.println("1. Admin");
            System.out.println("2. User Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    Admin.menu(sc);
                    break;

                case 2:
                    userLogin(sc);
                    break;

                case 3:
                    exit = true;
                    System.out.println("Thank you for using ATM");
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    static void userLogin(Scanner sc) {

        System.out.print("Enter Account Number: ");
        long accNo = sc.nextLong();

        System.out.print("Enter PIN: ");
        int pin = sc.nextInt();

        if (!UserService.login(accNo, pin)) {
            System.out.println("Invalid Credentials");
            return;
        }

        boolean logout = false;

        while (!logout) {

            System.out.println("\n--- USER MENU ---");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Change PIN");
            System.out.println("4. Check Balance");
            System.out.println("5. Logout");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.print("Enter amount: ");
                    double dep = sc.nextDouble();
                    UserService.deposit(accNo, dep);
                    break;

                case 2:
                    System.out.print("Enter amount: ");
                    double with = sc.nextDouble();
                    UserService.withdraw(accNo, with);
                    break;

                case 3:
                    System.out.print("Enter new PIN: ");
                    int newPin = sc.nextInt();
                    UserService.changePin(accNo, newPin);
                    break;

                case 4:
                    UserService.balance(accNo);
                    break;

                case 5:
                    logout = true;
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}