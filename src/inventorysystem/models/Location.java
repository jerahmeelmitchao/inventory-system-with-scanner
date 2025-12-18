package inventorysystem.models;

public class Location {

    private int locationId;
    private String locationName;
    private String description;

    public Location() {}

    public Location(int locationId, String locationName, String description) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.description = description;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
