import java.util.Scanner;

public class Admin {

    public static void menu(Scanner sc) {

        boolean exit = false;

        while (!exit) {

            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. View Users");
            System.out.println("2. Create User");
            System.out.println("3. Exit");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:

                    for (User acc : Atm.accounts) {
                        System.out.println("Account: " + acc.getAccountNo()
                                + " | Balance: " + acc.getBalance());
                    }

                    break;

                case 2:

                    long accNo = UserService.generateAccountNumber();

                    System.out.println("Generated Account Number: " + accNo);

                    System.out.print("Enter PIN: ");
                    int pin = sc.nextInt();

                    System.out.print("Enter Initial Deposit: ");
                    double deposit = sc.nextDouble();

                    if (deposit < UserService.MIN_BALANCE) {
                        System.out.println("Minimum opening balance is " + UserService.MIN_BALANCE);
                        break;
                    }

                    UserService.saveUser(accNo, pin, deposit);

                    System.out.println("User created successfully");

                    break;

                case 3:
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}