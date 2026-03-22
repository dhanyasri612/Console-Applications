package books;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import main.Main;

public class Books {

    String b_name;
    String ISBN;
    String author;
    int quantity;
    double price;

    public Books(String b_name, String ISBN, String author, int quantity, double price) {
        this.b_name = b_name;
        this.ISBN = ISBN;
        this.author = author;
        this.quantity = quantity;
        this.price = price;
    }

    public String getBookName() {
        return b_name;
    }

    public String getIsbn() {
        return ISBN;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static void AddBooks() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the Book Name: ");
        String b_name = sc.nextLine();
        System.out.print("\nEnter the ISBN Code: ");
        String ISBN = sc.nextLine();
        System.out.print("\nEnter the author of the Book: ");
        String author = sc.nextLine();
        System.out.print("\nEnter the quantity of the Book: ");
        int quantity = sc.nextInt();
        System.out.print("\nEnter the price of the Book: ");
        double price = sc.nextDouble();

        try {
            FileWriter fw = new FileWriter("Books.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(b_name + "," + ISBN + "," + author + "," + quantity + "," + price);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        Books book = new Books(b_name, ISBN, author, quantity, price);
        Main.books.put(ISBN, book);
    }

    public static void loadBooks() {
        try {
            FileReader fr = new FileReader("Books.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String b_name = parts[0];
                String ISBN = parts[1];
                String author = parts[2];
                int quantity = Integer.parseInt(parts[3]);
                double price = Double.parseDouble(parts[4]);
                Books book = new Books(b_name, ISBN, author, quantity, price);
                Main.books.put(ISBN, book);
            }
            br.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void saveBooks() {
        try {
            FileWriter fw = new FileWriter("Books.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            for (Books b : Main.books.values()) {
                bw.write(b.b_name + "," + b.ISBN + "," + b.author + "," + b.quantity + "," + b.price);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void ModifyBooks() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the Book ISBN Code to modify:");
        String ISBN = sc.nextLine();
        Books book = Main.books.get(ISBN);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        System.out.print("Enter the Book Name: ");
        book.b_name = sc.nextLine();
        System.out.print("\nEnter the ISBN Code: ");
        book.ISBN = sc.nextLine();
        System.out.print("\nEnter the author of the Book: ");
        book.author = sc.nextLine();
        System.out.print("\nEnter the quantity of the Book: ");
        book.quantity = sc.nextInt();
        System.out.print("\nEnter the price of the Book: ");
        book.price = sc.nextDouble();

        if (!ISBN.equals(book.ISBN)) {
            Main.books.remove(ISBN);
            Main.books.put(book.ISBN, book);
        }

        saveBooks();

    }

    public static void Delete() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the Book ISBN Code to Delete: ");
        String ISBN = sc.nextLine();
        if (Main.books.remove(ISBN) == null) {
            System.out.println("Book not found.");
            return;
        }
        saveBooks();
    }

    public static void ViewBooks() {
        List<Books> list = new ArrayList<>(Main.books.values());
        printBooks(list);
    }

    public static void ViewBooksSortedByName() {
        List<Books> list = new ArrayList<>(Main.books.values());
        Collections.sort(list, Comparator.comparing(Books::getBookName, String.CASE_INSENSITIVE_ORDER));
        printBooks(list);
    }

    public static void ViewBooksSortedByAvailableQuantity() {
        List<Books> list = new ArrayList<>(Main.books.values());
        Collections.sort(list, Comparator.comparingInt(Books::getQuantity));
        printBooks(list);
    }

    public static void printBooks(List<Books> books) {
        if (Main.books.isEmpty()) {
            System.out.println("No books available in the library.");
            return;
        }
        for (Books book : books) {
            System.out.println("Book ISBN CODE : " + book.ISBN);
            System.out.println("Book Name : " + book.b_name);
            System.out.println("Book Author : " + book.author);
            System.out.println("Book Quantity : " + book.quantity);
            System.out.println("Book Price : " + book.price);
            System.out.println("-----------------------------------");
        }
    }

    public static void SearchBooks() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Search by: 1.ISBN 2.Book Name");
        int choice = Integer.parseInt(sc.nextLine());
        Books book = null;
        if (choice == 1) {
            System.out.println("Enter the Book ISBN Code : ");
            String ISBN = sc.nextLine();
            book = findByISBN(ISBN);
        } else if (choice == 2) {
            System.out.println("Enter the Book Name : ");
            String name = sc.nextLine();
            book = findByName(name);
        }
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        System.out.println("Book ISBN CODE : " + book.ISBN);
        System.out.println("Book Name : " + book.b_name);
        System.out.println("Book Author : " + book.author);
        System.out.println("Book Quantity : " + book.quantity);
        System.out.println("Book Price : " + book.price);
        System.out.println("-----------------------------------");
    }

    public static Books findByISBN(String isbn) {
        return Main.books.get(isbn);
    }

    public static Books findByName(String name) {
        for (Map.Entry<String, Books> entry : Main.books.entrySet()) {
            Books b = entry.getValue();
            if (b.b_name.equalsIgnoreCase(name.trim())) {
                return b;
            }
        }
        return null;
    }

    public static Books findByNameOrISBN(String value) {
        Books byIsbn = findByISBN(value.trim());
        if (byIsbn != null) {
            return byIsbn;
        }
        return findByName(value.trim());
    }
}
