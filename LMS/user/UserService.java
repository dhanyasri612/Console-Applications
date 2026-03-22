package user;

import books.Books;
import borrow.BorrowRecord;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import main.Main;

public class UserService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int MAX_BORROW_LIMIT = 3;
    private static final double MIN_DEPOSIT_FOR_BORROW = 500.0;
    private static final double LOST_CARD_FINE = 10.0;

    public static void AddBorrower() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the User Email Id: ");
        String email = sc.nextLine();
        System.out.println("Enter the password: ");
        String password = sc.nextLine();
        System.out.println("Enter the initial amount to deposit (press Enter for default 1500):");
        String depositInput = sc.nextLine().trim();
        double deposit = depositInput.isEmpty() ? 1500.0 : Double.parseDouble(depositInput);
        System.out.println("Enter fine limit for borrower (press Enter for default 1000):");
        String fineInput = sc.nextLine().trim();
        double fineLimit = fineInput.isEmpty() ? 1000.0 : Double.parseDouble(fineInput);

        User user = new User(email, password, deposit, fineLimit);
        Main.users.add(user);
        saveUsers();
    }

    public static void loadUsers() {
        try {
            FileReader fr = new FileReader("Users.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String email = parts[0];
                String password = parts[1];
                double deposit = Double.parseDouble(parts[2]);
                double fineLimit = parts.length > 3 ? Double.parseDouble(parts[3]) : 1000.0;
                User user = new User(email, password, deposit, fineLimit);
                Main.users.add(user);
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void saveUsers() {
        try {
            FileWriter fw = new FileWriter("Users.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            for (User user : Main.users) {
                bw.write(user.gmail + "," + user.password + "," + user.deposit + "," + user.fineLimit);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void loadCheckouts() {
        try {
            FileReader fr = new FileReader("CheckOut.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                String userEmail = parts[0];
                String isbn = parts[1];
                LocalDate borrowDate = LocalDate.parse(parts[2], DATE_FORMAT);
                LocalDate dueDate = LocalDate.parse(parts[3], DATE_FORMAT);
                LocalDate returnDate = parts[4].equals("-") ? null : LocalDate.parse(parts[4], DATE_FORMAT);
                String status = parts[5];
                int extensionCount = Integer.parseInt(parts[6]);
                Main.checkouts.add(
                        new BorrowRecord(userEmail, isbn, borrowDate, dueDate, returnDate, status, extensionCount));
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("CheckOut.txt not found, creating on first checkout.");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void saveCheckouts() {
        try {
            FileWriter fw = new FileWriter("CheckOut.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            for (BorrowRecord record : Main.checkouts) {
                String returnDate = record.returnDate == null ? "-" : record.returnDate.format(DATE_FORMAT);
                bw.write(record.userEmail + "," + record.isbn + "," + record.borrowDate.format(DATE_FORMAT) + ","
                        + record.dueDate.format(DATE_FORMAT) + "," + returnDate + "," + record.status + ","
                        + record.extensionCount);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void loadFineHistory() {
        try {
            FileReader fr = new FileReader("FineHistory.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                String userEmail = parts[0];
                String entry = parts.length > 1 ? parts[1] : "";
                Main.fineHistory.computeIfAbsent(userEmail, key -> new ArrayList<>()).add(entry);
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("FineHistory.txt not found, creating on first fine entry.");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void saveFineHistory() {
        try {
            FileWriter fw = new FileWriter("FineHistory.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            for (Map.Entry<String, ArrayList<String>> entry : Main.fineHistory.entrySet()) {
                for (String detail : entry.getValue()) {
                    bw.write(entry.getKey() + "," + detail);
                    bw.newLine();
                }
            }
            bw.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static User getUserByEmail(String email) {
        for (User user : Main.users) {
            if (user.gmail.equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    private static ArrayList<String> getCart(String userEmail) {
        return Main.carts.computeIfAbsent(userEmail, key -> new ArrayList<>());
    }

    private static int activeBorrowCount(String userEmail) {
        int count = 0;
        for (BorrowRecord record : Main.checkouts) {
            if (record.userEmail.equalsIgnoreCase(userEmail) && record.status.equals("BORROWED")) {
                count++;
            }
        }
        return count;
    }

    private static boolean hasBorrowedBook(String userEmail, String isbn) {
        for (BorrowRecord record : Main.checkouts) {
            if (record.userEmail.equalsIgnoreCase(userEmail) && record.isbn.equalsIgnoreCase(isbn)
                    && record.status.equals("BORROWED")) {
                return true;
            }
        }
        return false;
    }

    private static BorrowRecord activeRecord(String userEmail, String isbn) {
        for (BorrowRecord record : Main.checkouts) {
            if (record.userEmail.equalsIgnoreCase(userEmail) && record.isbn.equalsIgnoreCase(isbn)
                    && record.status.equals("BORROWED")) {
                return record;
            }
        }
        return null;
    }

    private static void addFineEntry(String userEmail, String reason, double amount, String mode) {
        String entry = LocalDate.now().format(DATE_FORMAT) + " | " + reason + " | Rs." + amount + " | " + mode;
        Main.fineHistory.computeIfAbsent(userEmail, key -> new ArrayList<>()).add(entry);
        saveFineHistory();
    }

    private static void collectFine(User user, double fine, String reason, Scanner sc) {
        if (fine <= 0) {
            return;
        }
        if (fine > user.fineLimit) {
            System.out.println("Fine exceeded user fine limit. Please settle by cash.");
            addFineEntry(user.gmail, reason, fine, "CASH");
            return;
        }

        System.out.println("Fine amount: Rs." + fine);
        System.out.println("Choose payment mode: 1.Cash 2.Deduct from deposit");
        int mode = Integer.parseInt(sc.nextLine());
        if (mode == 2) {
            if (user.deposit >= fine) {
                user.deposit -= fine;
                saveUsers();
                addFineEntry(user.gmail, reason, fine, "DEPOSIT");
            } else {
                System.out.println("Insufficient deposit. Marked as cash payment.");
                addFineEntry(user.gmail, reason, fine, "CASH");
            }
        } else {
            addFineEntry(user.gmail, reason, fine, "CASH");
        }
    }

    public static void menu(String userEmail) {
        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        Books.loadBooks();
        while (!exit) {
            System.out.println("----------User---------");
            System.out.println("1.View all Books");
            System.out.println("2.Search Books");
            System.out.println("3.Add Book to cart");
            System.out.println("4.Remove Book from cart");
            System.out.println("5.Checkout(Borrow Books)");
            System.out.println("6.Return Book");
            System.out.println("7.Extend Book");
            System.out.println("8.Exchange Book");
            System.out.println("9.Report Lost Book");
            System.out.println("10.Report Lost Card");
            System.out.println("11.View Fine History");
            System.out.println("12.View Borrow History");
            System.out.println("13.Logout");

            int input = Integer.parseInt(sc.nextLine());
            switch (input) {
                case 1: {
                    Books.ViewBooks();
                    break;
                }
                case 2: {
                    Books.SearchBooks();
                    break;
                }
                case 3: {
                    addBookToCart(userEmail, sc);
                    break;
                }
                case 4: {
                    removeBookFromCart(userEmail, sc);
                    break;
                }
                case 5: {
                    checkoutBooks(userEmail);
                    break;
                }
                case 6: {
                    returnBook(userEmail, sc);
                    break;
                }
                case 7: {
                    extendBook(userEmail, sc);
                    break;
                }
                case 8: {
                    exchangeBook(userEmail, sc);
                    break;
                }
                case 9: {
                    reportLostBook(userEmail, sc);
                    break;
                }
                case 10: {
                    reportLostCard(userEmail, sc);
                    break;
                }
                case 11: {
                    viewFineHistory(userEmail);
                    break;
                }
                case 12: {
                    viewBorrowHistory(userEmail);
                    break;
                }
                case 13: {
                    exit = true;
                    break;
                }
                default: {
                    System.out.println("Invalid option.");
                }
            }
        }
    }

    private static void addBookToCart(String userEmail, Scanner sc) {
        User user = getUserByEmail(userEmail);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        ArrayList<String> cart = getCart(userEmail);
        int alreadyBorrowed = activeBorrowCount(userEmail);
        if (alreadyBorrowed + cart.size() >= MAX_BORROW_LIMIT) {
            System.out.println("You can borrow maximum 3 books at a time.");
            return;
        }

        System.out.println("Enter Book Name or ISBN to add to cart:");
        String key = sc.nextLine();
        Books book = Books.findByNameOrISBN(key);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        String isbn = book.getIsbn();
        if (book.getQuantity() <= 0) {
            System.out.println("Book currently unavailable.");
            return;
        }
        if (cart.contains(isbn) || hasBorrowedBook(userEmail, isbn)) {
            System.out.println("Cannot borrow same book twice.");
            return;
        }
        cart.add(isbn);
        System.out.println("Book added to cart.");
    }

    private static void removeBookFromCart(String userEmail, Scanner sc) {
        ArrayList<String> cart = getCart(userEmail);
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        System.out.println("Books in cart:");
        for (String isbn : cart) {
            Books book = Books.findByISBN(isbn);
            System.out.println(isbn + " - " + (book != null ? book.getBookName() : "Unknown"));
        }
        System.out.println("Enter ISBN to remove:");
        String isbn = sc.nextLine();
        if (cart.remove(isbn)) {
            System.out.println("Removed from cart.");
        } else {
            System.out.println("ISBN not present in cart.");
        }
    }

    private static void checkoutBooks(String userEmail) {
        User user = getUserByEmail(userEmail);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        ArrayList<String> cart = getCart(userEmail);
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. Add books first.");
            return;
        }
        if (user.deposit < MIN_DEPOSIT_FOR_BORROW) {
            System.out.println("Minimum security deposit of Rs.500 is required to borrow books.");
            return;
        }

        for (String isbn : cart) {
            Books book = Books.findByISBN(isbn);
            if (book == null || book.getQuantity() <= 0) {
                System.out.println("Book unavailable during checkout: " + isbn);
                return;
            }
            if (hasBorrowedBook(userEmail, isbn)) {
                System.out.println("Cannot borrow same book twice: " + isbn);
                return;
            }
        }

        LocalDate borrowDate = LocalDate.now();
        for (String isbn : cart) {
            Books book = Books.findByISBN(isbn);
            book.setQuantity(book.getQuantity() - 1);
            BorrowRecord record = new BorrowRecord(userEmail, isbn, borrowDate, borrowDate.plusDays(15), null,
                    "BORROWED", 0);
            Main.checkouts.add(record);
        }
        cart.clear();
        Books.saveBooks();
        saveCheckouts();
        System.out.println("Checkout successful.");
    }

    private static double calculateDelayFine(BorrowRecord record, Books book, LocalDate returnDate) {
        long delayDays = ChronoUnit.DAYS.between(record.dueDate, returnDate);
        if (delayDays <= 0) {
            return 0.0;
        }
        double baseFine = delayDays * 2.0;
        long exponentialBlock = delayDays / 10;
        double fine = baseFine * Math.pow(2, exponentialBlock);
        double cap = book.getPrice() * 0.8;
        return Math.min(fine, cap);
    }

    private static void returnBook(String userEmail, Scanner sc) {
        User user = getUserByEmail(userEmail);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.println("Enter ISBN to return:");
        String isbn = sc.nextLine();
        BorrowRecord record = activeRecord(userEmail, isbn);
        if (record == null) {
            System.out.println("No active borrow found for this ISBN.");
            return;
        }
        Books book = Books.findByISBN(isbn);
        if (book == null) {
            System.out.println("Book not found in inventory.");
            return;
        }
        System.out.println("Enter return date (DD/MM/YYYY):");
        LocalDate returnDate = LocalDate.parse(sc.nextLine(), DATE_FORMAT);
        double fine = calculateDelayFine(record, book, returnDate);

        collectFine(user, fine, "Late return for ISBN " + isbn, sc);

        record.returnDate = returnDate;
        record.status = "RETURNED";
        book.setQuantity(book.getQuantity() + 1);
        Books.saveBooks();
        saveCheckouts();
        System.out.println("Book returned successfully.");
    }

    private static void extendBook(String userEmail, Scanner sc) {
        System.out.println("Enter ISBN to extend:");
        String isbn = sc.nextLine();
        BorrowRecord record = activeRecord(userEmail, isbn);
        if (record == null) {
            System.out.println("No active borrow found for this ISBN.");
            return;
        }
        if (record.extensionCount >= 2) {
            System.out.println("Tenure can be extended only up to two consecutive times.");
            return;
        }
        record.dueDate = record.dueDate.plusDays(7);
        record.extensionCount++;
        saveCheckouts();
        System.out.println("Book tenure extended. New due date: " + record.dueDate.format(DATE_FORMAT));
    }

    private static void exchangeBook(String userEmail, Scanner sc) {
        System.out.println("Enter currently borrowed ISBN to exchange:");
        String oldIsbn = sc.nextLine();
        BorrowRecord oldRecord = activeRecord(userEmail, oldIsbn);
        if (oldRecord == null) {
            System.out.println("No active borrow found for old ISBN.");
            return;
        }
        Books oldBook = Books.findByISBN(oldIsbn);
        if (oldBook == null) {
            System.out.println("Old book not found.");
            return;
        }
        System.out.println("Enter new book Name or ISBN:");
        String newInput = sc.nextLine();
        Books newBook = Books.findByNameOrISBN(newInput);
        if (newBook == null || newBook.getQuantity() <= 0) {
            System.out.println("New book unavailable.");
            return;
        }
        if (hasBorrowedBook(userEmail, newBook.getIsbn())) {
            System.out.println("Cannot borrow same book twice.");
            return;
        }

        oldRecord.status = "RETURNED";
        oldRecord.returnDate = LocalDate.now();
        oldBook.setQuantity(oldBook.getQuantity() + 1);

        newBook.setQuantity(newBook.getQuantity() - 1);
        LocalDate date = LocalDate.now();
        Main.checkouts
                .add(new BorrowRecord(userEmail, newBook.getIsbn(), date, date.plusDays(15), null, "BORROWED", 0));
        Books.saveBooks();
        saveCheckouts();
        System.out.println("Book exchanged successfully.");
    }

    private static void reportLostBook(String userEmail, Scanner sc) {
        User user = getUserByEmail(userEmail);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.println("Enter ISBN of lost book:");
        String isbn = sc.nextLine();
        BorrowRecord record = activeRecord(userEmail, isbn);
        if (record == null) {
            System.out.println("No active borrow found for this ISBN.");
            return;
        }
        Books book = Books.findByISBN(isbn);
        if (book == null) {
            System.out.println("Book not found in inventory.");
            return;
        }
        double fine = book.getPrice() * 0.5;
        collectFine(user, fine, "Lost book ISBN " + isbn, sc);
        record.status = "LOST";
        record.returnDate = LocalDate.now();
        saveCheckouts();
        System.out.println("Lost book recorded and fine applied.");
    }

    private static void reportLostCard(String userEmail, Scanner sc) {
        User user = getUserByEmail(userEmail);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        collectFine(user, LOST_CARD_FINE, "Lost membership card", sc);
        System.out.println("Lost card fine recorded.");
    }

    private static void viewFineHistory(String userEmail) {
        ArrayList<String> entries = Main.fineHistory.get(userEmail);
        if (entries == null || entries.isEmpty()) {
            System.out.println("No fine history found.");
            return;
        }
        System.out.println("---- Fine History ----");
        for (String entry : entries) {
            System.out.println(entry);
        }
    }

    private static void viewBorrowHistory(String userEmail) {
        boolean found = false;
        System.out.println("---- Borrow History ----");
        for (BorrowRecord record : Main.checkouts) {
            if (!record.userEmail.equalsIgnoreCase(userEmail)) {
                continue;
            }
            found = true;
            System.out.println("ISBN: " + record.isbn + " | Borrow: " + record.borrowDate.format(DATE_FORMAT)
                    + " | Due: " + record.dueDate.format(DATE_FORMAT) + " | Return: "
                    + (record.returnDate == null ? "-" : record.returnDate.format(DATE_FORMAT)) + " | Status: "
                    + record.status + " | Extensions: " + record.extensionCount);
        }
        if (!found) {
            System.out.println("No borrow history found.");
        }
    }
}
