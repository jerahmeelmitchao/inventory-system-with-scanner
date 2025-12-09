package inventorysystem.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;

public class BorrowRecord {

    private int recordId;
    private int itemId;
    private int borrowerId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String status;

    // 🔹 JavaFX properties for TableView
    private StringProperty borrowerName = new SimpleStringProperty("");
    private StringProperty borrowDateProperty = new SimpleStringProperty("");
    private StringProperty returnDateProperty = new SimpleStringProperty("");
    private StringProperty statusProperty = new SimpleStringProperty("");

    public BorrowRecord() {
    }

    public BorrowRecord(int recordId, int itemId, int borrowerId,
            LocalDate borrowDate, LocalDate returnDate, String status) {

        this.recordId = recordId;
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;

        // Set JavaFX properties
        this.borrowDateProperty.set(borrowDate != null ? borrowDate.toString() : "");
        this.returnDateProperty.set(returnDate != null ? returnDate.toString() : "");
        this.statusProperty.set(status);
    }

    // ----------------------------------------------------
    // Getters & setters (normal fields)
    // ----------------------------------------------------
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
        this.borrowDateProperty.set(borrowDate != null ? borrowDate.toString() : "");
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.returnDateProperty.set(returnDate != null ? returnDate.toString() : "");
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.statusProperty.set(status);
    }

    // ----------------------------------------------------
    // Borrower Name (used in the pop-up TableView)
    // ----------------------------------------------------
    public String getBorrowerName() {
        return borrowerName.get();
    }

    public void setBorrowerName(String name) {
        borrowerName.set(name);
    }

    public StringProperty borrowerNameProperty() {
        return borrowerName;
    }

    // ----------------------------------------------------
    // JavaFX Properties used by your TableView columns
    // ----------------------------------------------------
    public StringProperty borrowDateProperty() {
        return borrowDateProperty;
    }

    public StringProperty returnDateProperty() {
        return returnDateProperty;
    }

    public StringProperty statusProperty() {
        return statusProperty;
    }

    @Override
    public String toString() {
        return "Record #" + recordId
                + " | ItemID: " + itemId
                + " | BorrowerID: " + borrowerId
                + " | BorrowDate: " + borrowDate
                + " | ReturnDate: " + returnDate
                + " | Status: " + status
                + " | BorrowerName: " + getBorrowerName();
    }
}
