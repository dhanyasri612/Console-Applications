package admin;

import books.Books;
import borrow.BorrowRecord;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import main.Main;
import user.*;

public class AdminService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void menu() {
        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        Books.loadBooks();
        while (!exit) {
            System.out.println("-----------Admin----------");
            System.out.println("1.Add Book");
            System.out.println("2.Modify Book");
            System.out.println("3.Delete Book");
            System.out.println("4.Add Admin");
            System.out.println("5.Add Borrower");
            System.out.println("6.View Book (normal)");
            System.out.println("7.Search Book");
            System.out.println("8.View Books sorted by Name");
            System.out.println("9.View Books sorted by Available Quantity");
            System.out.println("10.Manage Borrower Fine Limit");
            System.out.println("11.Report: Books with low quantity");
            System.out.println("12.Report: Books never borrowed");
            System.out.println("13.Report: Heavily borrowed books");
            System.out.println("14.Report: Students with outstanding books by date");
            System.out.println("15.Report: Current status by ISBN");
            System.out.println("16.Log out");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1: {
                    Books.AddBooks();
                    break;
                }
                case 2: {
                    Books.ModifyBooks();
                    break;
                }
                case 3: {
                    Books.Delete();
                    break;
                }
                case 4: {
                    Admin.AddAdmin();
                    break;
                }
                case 5: {
                    UserService.AddBorrower();
                    break;
                }
                case 6: {
                    Books.ViewBooks();
                    break;
                }
                case 7: {
                    Books.SearchBooks();
                    break;
                }
                case 8: {
                    Books.ViewBooksSortedByName();
                    break;
                }
                case 9: {
                    Books.ViewBooksSortedByAvailableQuantity();
                    break;
                }
                case 10: {
                    manageFineLimit(sc);
                    break;
                }
                case 11: {
                    reportLowQuantity(sc);
                    break;
                }
                case 12: {
                    reportNeverBorrowed();
                    break;
                }
                case 13: {
                    reportHeavilyBorrowed(sc);
                    break;
                }
                case 14: {
                    reportOutstandingByDate(sc);
                    break;
                }
                case 15: {
                    reportStatusByIsbn(sc);
                    break;
                }
                case 16: {
                    exit = true;
                    break;
                }
                default: {
                    System.out.println("Enter the correct choice number");
                }
            }
        }
    }

    private static void manageFineLimit(Scanner sc) {
        System.out.println("Enter borrower email:");
        String email = sc.nextLine();
        User target = null;
        for (User user : Main.users) {
            if (user.gmail.equalsIgnoreCase(email)) {
                target = user;
                break;
            }
        }
        if (target == null) {
            System.out.println("Borrower not found.");
            return;
        }
        System.out.println("Current fine limit: " + target.fineLimit);
        System.out.println("Enter new fine limit:");
        double newLimit = Double.parseDouble(sc.nextLine());
        target.fineLimit = newLimit;
        UserService.saveUsers();
        System.out.println("Fine limit updated.");
    }

    private static void reportLowQuantity(Scanner sc) {
        System.out.println("Enter low-quantity threshold:");
        int threshold = Integer.parseInt(sc.nextLine());
        boolean found = false;
        for (Books book : Main.books.values()) {
            if (book.getQuantity() < threshold) {
                found = true;
                System.out.println(book.getIsbn() + " | " + book.getBookName() + " | Qty: " + book.getQuantity());
            }
        }
        if (!found) {
            System.out.println("No books below threshold.");
        }
    }

    private static void reportNeverBorrowed() {
        HashSet<String> borrowed = new HashSet<>();
        for (BorrowRecord record : Main.checkouts) {
            borrowed.add(record.isbn);
        }
        boolean found = false;
        for (Books book : Main.books.values()) {
            if (!borrowed.contains(book.getIsbn())) {
                found = true;
                System.out.println(book.getIsbn() + " | " + book.getBookName());
            }
        }
        if (!found) {
            System.out.println("All books have been borrowed at least once.");
        }
    }

    private static void reportHeavilyBorrowed(Scanner sc) {
        System.out.println("Enter minimum borrow count:");
        int minCount = Integer.parseInt(sc.nextLine());
        HashMap<String, Integer> borrowCount = new HashMap<>();
        for (BorrowRecord record : Main.checkouts) {
            borrowCount.put(record.isbn, borrowCount.getOrDefault(record.isbn, 0) + 1);
        }
        boolean found = false;
        for (Map.Entry<String, Integer> entry : borrowCount.entrySet()) {
            if (entry.getValue() >= minCount) {
                found = true;
                Books b = Books.findByISBN(entry.getKey());
                String name = b == null ? "Unknown" : b.getBookName();
                System.out.println(entry.getKey() + " | " + name + " | Borrows: " + entry.getValue());
            }
        }
        if (!found) {
            System.out.println("No heavily borrowed books for this threshold.");
        }
    }

    private static void reportOutstandingByDate(Scanner sc) {
        System.out.println("Enter date (DD/MM/YYYY):");
        LocalDate date = LocalDate.parse(sc.nextLine(), DATE_FORMAT);
        boolean found = false;
        for (BorrowRecord record : Main.checkouts) {
            if (record.status.equals("BORROWED") && !record.dueDate.isAfter(date)) {
                found = true;
                System.out.println("User: " + record.userEmail + " | ISBN: " + record.isbn + " | Due: "
                        + record.dueDate.format(DATE_FORMAT));
            }
        }
        if (!found) {
            System.out.println("No outstanding books as on given date.");
        }
    }

    private static void reportStatusByIsbn(Scanner sc) {
        System.out.println("Enter ISBN:");
        String isbn = sc.nextLine();
        BorrowRecord latestActive = null;
        for (BorrowRecord record : Main.checkouts) {
            if (record.isbn.equalsIgnoreCase(isbn) && record.status.equals("BORROWED")) {
                latestActive = record;
                break;
            }
        }

        Books book = Books.findByISBN(isbn);
        if (book == null) {
            System.out.println("Book not found in inventory.");
            return;
        }

        System.out.println("Book: " + book.getBookName() + " | ISBN: " + book.getIsbn() + " | Qty in rack: "
                + book.getQuantity());
        if (latestActive == null) {
            System.out.println("Currently in rack or not actively borrowed.");
            return;
        }
        System.out.println("Borrowed by: " + latestActive.userEmail + " | Expected return: "
                + latestActive.dueDate.format(DATE_FORMAT));
    }
}
