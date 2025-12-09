package inventorysystem.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDateTime;

public class BorrowRecord {

    private int recordId;
    private int itemId;
    private int borrowerId;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private String status;

    // NEW: remarks field
    private String remarks;

    // JavaFX properties
    private StringProperty borrowerName = new SimpleStringProperty("");
    private StringProperty borrowDateProperty = new SimpleStringProperty("");
    private StringProperty returnDateProperty = new SimpleStringProperty("");
    private StringProperty statusProperty = new SimpleStringProperty("");
    private StringProperty remarksProperty = new SimpleStringProperty("");

    public BorrowRecord() {}

    // ✔ FIXED: Constructor now uses LocalDateTime instead of LocalDate
    public BorrowRecord(int recordId, int itemId, int borrowerId,
                        LocalDateTime borrowDate, LocalDateTime returnDate,
                        String status, String remarks) {

        this.recordId = recordId;
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
        this.remarks = remarks;

        // Set JavaFX properties
        this.borrowDateProperty.set(borrowDate != null ? borrowDate.toString() : "");
        this.returnDateProperty.set(returnDate != null ? returnDate.toString() : "");
        this.statusProperty.set(status);
        this.remarksProperty.set(remarks != null ? remarks : "");
    }

    // --------------------------------------------
    // Normal getters & setters
    // --------------------------------------------
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getBorrowerId() { return borrowerId; }
    public void setBorrowerId(int borrowerId) { this.borrowerId = borrowerId; }

    // ✔ FIXED: return LocalDateTime
    public LocalDateTime getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
        this.borrowDateProperty.set(borrowDate != null ? borrowDate.toString() : "");
    }

    // ✔ FIXED: return LocalDateTime
    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
        this.returnDateProperty.set(returnDate != null ? returnDate.toString() : "");
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.statusProperty.set(status);
    }

    // REMARKS
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
        this.remarksProperty.set(remarks != null ? remarks : "");
    }

    // --------------------------------------------
    // JavaFX Properties
    // --------------------------------------------
    public String getBorrowerName() { return borrowerName.get(); }
    public void setBorrowerName(String name) { borrowerName.set(name); }
    public StringProperty borrowerNameProperty() { return borrowerName; }

    public StringProperty borrowDateProperty() { return borrowDateProperty; }
    public StringProperty returnDateProperty() { return returnDateProperty; }
    public StringProperty statusProperty() { return statusProperty; }
    public StringProperty remarksProperty() { return remarksProperty; }

    @Override
    public String toString() {
        return "Record #" + recordId
                + " | ItemID: " + itemId
                + " | BorrowerID: " + borrowerId
                + " | BorrowDate: " + borrowDate
                + " | ReturnDate: " + returnDate
                + " | Status: " + status
                + " | Remarks: " + remarks
                + " | BorrowerName: " + getBorrowerName();
    }
}
