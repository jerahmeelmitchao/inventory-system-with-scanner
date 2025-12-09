package inventorysystem.models;

import java.time.LocalDate;

public class Item {

    private int itemId;
    private String itemName;
    private String barcode;
    private int categoryId;
    private String unit;
    private LocalDate dateAcquired;
    private String status;
    private String storageLocation;
    private int inchargeId;
    private String inChargeName;
    private String categoryName;
    private String addedBy;
    private LocalDate lastScanned;
    private String description;

    // ✅ FIX: Remove unsupported operation – REQUIRED by ItemDAO
    public Item() {
        // Empty constructor for DAO use
    }

    public Item(int itemId, String itemName, String barcode, int categoryId,
                String unit, LocalDate dateAcquired, String status,
                String storageLocation, int inchargeId, String addedBy) {

        this.itemId = itemId;
        this.itemName = itemName;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.unit = unit;
        this.dateAcquired = dateAcquired;
        this.status = status;
        this.storageLocation = storageLocation;
        this.inchargeId = inchargeId;
        this.addedBy = addedBy;
    }

    // -------------------------------
    // Getters & Setters
    // -------------------------------

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDate getDateAcquired() { return dateAcquired; }
    public void setDateAcquired(LocalDate dateAcquired) { this.dateAcquired = dateAcquired; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public int getInchargeId() { return inchargeId; }
    public void setInchargeId(int inchargeId) { this.inchargeId = inchargeId; }

    public String getInChargeName() { return inChargeName; }
    public void setInChargeName(String inChargeName) { this.inChargeName = inChargeName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }

    public LocalDate getLastScanned() { return lastScanned; }
    public void setLastScanned(LocalDate lastScanned) { this.lastScanned = lastScanned; }

}
