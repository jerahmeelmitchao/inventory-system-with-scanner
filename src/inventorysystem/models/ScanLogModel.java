package inventorysystem.models;

import java.time.LocalDateTime;

public class ScanLogModel {

    private long scanId;
    private int itemId;
    private LocalDateTime scanDate;

    public ScanLogModel(long scanId, int itemId, LocalDateTime scanDate) {
        this.scanId = scanId;
        this.itemId = itemId;
        this.scanDate = scanDate;
    }

    public long getScanId() {
        return scanId;
    }

    public int getItemId() {
        return itemId;
    }

    public LocalDateTime getScanDate() {
        return scanDate;
    }

    // Shortcut to log scan directly
    public static void logScan(int itemId) {
        new inventorysystem.dao.ScanLogDAO().addScan(itemId);
    }
}
