package inventorysystem.dao;

import inventorysystem.models.Item;
import inventorysystem.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    // --------------------------
    // ADD ITEM
    // --------------------------
    public boolean addItem(Item item) {

        String sql = """
            INSERT INTO items 
            (item_name, barcode, category_id, unit, date_acquired, status, 
             storage_location, incharge_id, added_by, description)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getBarcode());
            stmt.setInt(3, item.getCategoryId());
            stmt.setString(4, item.getUnit());
            stmt.setDate(5, Date.valueOf(item.getDateAcquired()));
            stmt.setString(6, item.getStatus());
            stmt.setString(7, item.getStorageLocation());

            if (item.getInchargeId() > 0) {
                stmt.setInt(8, item.getInchargeId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, item.getAddedBy());
            stmt.setString(10, item.getDescription());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                return false;
            }

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                item.setItemId(keys.getInt(1));
            }

            return true;

        } catch (SQLException e) {
            System.err.println("❌ Add Item Error: " + e.getMessage());
        }
        return false;
    }

    // --------------------------
    // GET ITEM BY BARCODE
    // --------------------------
    public Item getItemByBarcode(String barcode) {

        String sql = """
            SELECT * FROM items 
            WHERE LOWER(barcode) = LOWER(?)
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, barcode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Item item = new Item();

                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setBarcode(rs.getString("barcode"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setUnit(rs.getString("unit"));
                item.setDateAcquired(rs.getDate("date_acquired").toLocalDate());
                item.setStatus(rs.getString("status"));
                item.setStorageLocation(rs.getString("storage_location"));
                item.setInchargeId(rs.getInt("incharge_id"));
                item.setAddedBy(rs.getString("added_by"));
                item.setDescription(rs.getString("description"));

                Timestamp lastScan = rs.getTimestamp("last_scanned");
                if (lastScan != null) {
                    item.setLastScanned(lastScan.toLocalDateTime().toLocalDate());
                }

                return item;
            }

        } catch (Exception e) {
            System.err.println("❌ getItemByBarcode Error: " + e.getMessage());
        }

        return null;
    }

    // --------------------------
    // GET ALL ITEMS
    // --------------------------
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        String sql = """
            SELECT 
                i.*, 
                c.category_name,
                ic.incharge_name
            FROM items i
            LEFT JOIN categories c ON i.category_id = c.category_id
            LEFT JOIN incharge ic ON i.incharge_id = ic.incharge_id
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Item item = new Item();

                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setBarcode(rs.getString("barcode"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setUnit(rs.getString("unit"));
                item.setDateAcquired(rs.getDate("date_acquired").toLocalDate());
                item.setStatus(rs.getString("status"));
                item.setStorageLocation(rs.getString("storage_location"));
                item.setInchargeId(rs.getInt("incharge_id"));
                item.setAddedBy(rs.getString("added_by"));
                item.setDescription(rs.getString("description"));

                if (rs.getTimestamp("last_scanned") != null) {
                    item.setLastScanned(rs.getTimestamp("last_scanned").toLocalDateTime().toLocalDate());
                }

                item.setCategoryName(rs.getString("category_name"));
                item.setInChargeName(rs.getString("incharge_name"));

                items.add(item);
            }

        } catch (SQLException e) {
            System.err.println("❌ Load Items Error: " + e.getMessage());
        }

        return items;
    }

    // --------------------------
    // UPDATE ITEM
    // --------------------------
    public void updateItem(Item item) {

        String sql = """
            UPDATE items SET 
                item_name=?, barcode=?, category_id=?, unit=?, 
                date_acquired=?, status=?, storage_location=?, 
                incharge_id=?, added_by=?, description=?
            WHERE item_id=?
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getBarcode());
            stmt.setInt(3, item.getCategoryId());
            stmt.setString(4, item.getUnit());
            stmt.setDate(5, Date.valueOf(item.getDateAcquired()));
            stmt.setString(6, item.getStatus());
            stmt.setString(7, item.getStorageLocation());

            if (item.getInchargeId() > 0) {
                stmt.setInt(8, item.getInchargeId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, item.getAddedBy());
            stmt.setString(10, item.getDescription());

            stmt.setInt(11, item.getItemId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Update Item Error: " + e.getMessage());
        }
    }

    // --------------------------
    // DELETE ITEM
    // --------------------------
    public void deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Delete Item Error: " + e.getMessage());
        }
    }

    // --------------------------
    // UPDATE STATUS ONLY
    // --------------------------
    public boolean updateItemStatus(int itemId, String status) {
        String sql = "UPDATE items SET status=? WHERE item_id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, itemId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Update Item Status Error: " + e.getMessage());
        }
        return false;
    }
    // --------------------------
// GET ITEM BY ID
// --------------------------

    public Item getItemById(int itemId) {

        String sql = """
        SELECT 
            i.*, 
            c.category_name,
            ic.incharge_name
        FROM items i
        LEFT JOIN categories c ON i.category_id = c.category_id
        LEFT JOIN incharge ic ON i.incharge_id = ic.incharge_id
        WHERE i.item_id = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Item item = new Item();

                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setBarcode(rs.getString("barcode"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setUnit(rs.getString("unit"));
                item.setDateAcquired(rs.getDate("date_acquired").toLocalDate());
                item.setStatus(rs.getString("status"));
                item.setStorageLocation(rs.getString("storage_location"));
                item.setInchargeId(rs.getInt("incharge_id"));
                item.setAddedBy(rs.getString("added_by"));
                item.setDescription(rs.getString("description"));

                // last scanned (nullable)
                Timestamp lastScan = rs.getTimestamp("last_scanned");
                if (lastScan != null) {
                    item.setLastScanned(lastScan.toLocalDateTime().toLocalDate());
                }

                // category name + incharge name (from JOIN)
                item.setCategoryName(rs.getString("category_name"));
                item.setInChargeName(rs.getString("incharge_name"));

                return item;
            }

        } catch (SQLException e) {
            System.err.println("❌ getItemById Error: " + e.getMessage());
        }

        return null;
    }

}
