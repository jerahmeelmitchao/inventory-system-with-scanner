package inventorysystem.dao;

import inventorysystem.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ScanLogDAO {

    // Add the scan to the scan_log table
    public void addScan(int itemId) {
        String sql = "INSERT INTO scan_log (item_id, scan_date) VALUES (?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ps.executeUpdate();

            // Optional: also update items.last_scanned
            updateLastScanned(itemId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLastScanned(int itemId) {
        String sql = "UPDATE items SET last_scanned = NOW() WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
