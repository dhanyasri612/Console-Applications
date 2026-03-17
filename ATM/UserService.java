import java.io.*;

public class UserService {

    public static final double MIN_BALANCE = 500;

    public static void saveUser(long accNo, int pin, double bal) {

        try {

            User user = new User(accNo, pin, bal);

            Atm.accounts.add(user);

            BufferedWriter bw = new BufferedWriter(new FileWriter("accounts.txt", true));

            bw.write(accNo + "," + pin + "," + bal);
            bw.newLine();

            bw.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void deposit(long accNo, double amount) {

        if (amount <= 0) {
            System.out.println("Invalid amount");
            return;
        }

        for (User acc : Atm.accounts) {

            if (acc.getAccountNo() == accNo) {

                acc.deposit(amount);

                System.out.println("Deposit successful");
                System.out.println("Balance: " + acc.getBalance());

                saveAccounts();
                return;
            }
        }
    }

    public static void withdraw(long accNo, double amount) {

        for (User acc : Atm.accounts) {

            if (acc.getAccountNo() == accNo) {

                if (amount <= 0) {
                    System.out.println("Invalid amount");
                    return;
                }

                if (acc.getBalance() - amount < MIN_BALANCE) {
                    System.out.println("Minimum balance of " + MIN_BALANCE + " must be maintained");
                    return;
                }

                if (acc.withdraw(amount)) {

                    System.out.println("Withdraw successful");
                    System.out.println("Balance: " + acc.getBalance());

                    saveAccounts();

                } else {

                    System.out.println("Insufficient balance");
                }

                return;
            }
        }
    }

    public static void changePin(long accNo, int newPin) {

        for (User acc : Atm.accounts) {

            if (acc.getAccountNo() == accNo) {

                acc.setPin(newPin);

                saveAccounts();

                System.out.println("PIN updated");
                return;
            }
        }
    }

    public static void balance(long accNo) {

        for (User acc : Atm.accounts) {

            if (acc.getAccountNo() == accNo) {

                System.out.println("Balance: " + acc.getBalance());
                return;
            }
        }
    }

    public static void loadAccounts() {

        try {

            BufferedReader br = new BufferedReader(new FileReader("accounts.txt"));

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                long accNo = Long.parseLong(parts[0]);
                int pin = Integer.parseInt(parts[1]);
                double bal = Double.parseDouble(parts[2]);

                Atm.accounts.add(new User(accNo, pin, bal));
            }

            br.close();

        } catch (Exception e) {
            System.out.println("File not found, starting fresh");
        }
    }

    public static void saveAccounts() {

        try {

            BufferedWriter bw = new BufferedWriter(new FileWriter("accounts.txt"));

            for (User acc : Atm.accounts) {

                bw.write(acc.getAccountNo() + "," + acc.getPin() + "," + acc.getBalance());
                bw.newLine();
            }

            bw.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean login(long accNo, int pin) {

        for (User acc : Atm.accounts) {

            if (acc.getAccountNo() == accNo && acc.getPin() == pin) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAccountExist(long accNo) {

        for (User acc : Atm.accounts) {

            if (acc.getAccountNo() == accNo)
                return true;
        }

        return false;
    }

    public static long generateAccountNumber() {

        if (Atm.accounts.size() == 0)
            return 1001;

        User lastUser = Atm.accounts.get(Atm.accounts.size() - 1);

        return lastUser.getAccountNo() + 1;
    }
}