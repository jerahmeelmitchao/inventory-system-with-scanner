package inventorysystem.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Item {

    private int itemId;
    private String itemName;

    // DB column: item_code
    private String itemCode;

    private int categoryId;
    private String unit;
    private LocalDate dateAcquired;

    private String status;
    private Integer locationId;
    private String locationName; // joined display only

    private int inchargeId;

    // Joined fields (optional)
    private String inChargeName;
    private String categoryName;

    private String addedBy;

    // DB is DATETIME → LocalDateTime required
    private LocalDateTime lastScanned;

    private String description;

    public Item(int itemId, String itemName, String itemCode, int categoryId, String unit, LocalDate dateAcquired, String status, int locationId, int inchargeId, String inChargeName, String categoryName, String addedBy, LocalDateTime lastScanned, String description) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.categoryId = categoryId;
        this.unit = unit;
        this.dateAcquired = dateAcquired;
        this.status = status;
        this.locationId = locationId;
        this.inchargeId = inchargeId;
        this.inChargeName = inChargeName;
        this.categoryName = categoryName;
        this.addedBy = addedBy;
        this.lastScanned = lastScanned;
        this.description = description;
    }

    // Required empty constructor
    public Item() {
    }

    public Item(int itemId, String itemName, String itemCode, int categoryId,
            String unit, LocalDate dateAcquired, String status,
            int locationId, int inchargeId, String addedBy) {

        this.itemId = itemId;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.categoryId = categoryId;
        this.unit = unit;
        this.dateAcquired = dateAcquired;
        this.status = status;
        this.locationId = locationId;
        this.inchargeId = inchargeId;
        this.addedBy = addedBy;
    }

    // -------------------------------
    // Getters / Setters
    // -------------------------------
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }        // FIXED

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getBarcode() {
        return itemCode;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDate getDateAcquired() {
        return dateAcquired;
    }

    public void setDateAcquired(LocalDate dateAcquired) {
        this.dateAcquired = dateAcquired;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getInchargeId() {
        return inchargeId;
    }

    public void setInchargeId(int inchargeId) {
        this.inchargeId = inchargeId;
    }

    public String getInChargeName() {
        return inChargeName;
    }

    public void setInChargeName(String inChargeName) {
        this.inChargeName = inChargeName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public LocalDateTime getLastScanned() {
        return lastScanned;
    }    // FIXED for DATETIME

    public void setLastScanned(LocalDateTime lastScanned) {
        this.lastScanned = lastScanned;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
