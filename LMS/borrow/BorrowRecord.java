package borrow;

import java.time.LocalDate;

public class BorrowRecord {

    public String userEmail;
    public String isbn;
    public LocalDate borrowDate;
    public LocalDate dueDate;
    public LocalDate returnDate;
    public String status;
    public int extensionCount;

    public BorrowRecord(String userEmail, String isbn, LocalDate borrowDate, LocalDate dueDate,
            LocalDate returnDate, String status, int extensionCount) {
        this.userEmail = userEmail;
        this.isbn = isbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.extensionCount = extensionCount;
    }
}
