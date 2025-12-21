package inventorysystem.models;

public class Unit {

    private int unitId;
    private String unitName;
    private String description;

    public Unit() {}

    // ✅ Used when loading from DB
    public Unit(int unitId, String unitName, String description) {
        this.unitId = unitId;
        this.unitName = unitName;
        this.description = description;
    }

    // ✅ Used when ADDING (NO ID – auto increment)
    public Unit(String unitName, String description) {
        this.unitName = unitName;
        this.description = description;
    }

    public int getUnitId() { return unitId; }
    public void setUnitId(int unitId) { this.unitId = unitId; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
