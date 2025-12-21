package inventorysystem.dao;

import inventorysystem.models.Item;
import inventorysystem.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    // ----------------------------------------------------
    // ADD ITEM
    // ----------------------------------------------------
    public boolean addItem(Item item) {

        String sql = """
        INSERT INTO items
        (item_name, barcode, category_id, unit_id, date_acquired,
         status, location_id, incharge_id, added_by, description)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getItemCode());
            stmt.setInt(3, item.getCategoryId());

            // ✅ FIXED UNIT
            stmt.setObject(4, item.getUnitId(), Types.INTEGER);

            if (item.getDateAcquired() != null) {
                stmt.setDate(5, Date.valueOf(item.getDateAcquired()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, item.getStatus());
            stmt.setObject(7, item.getLocationId(), Types.INTEGER);
            stmt.setObject(8, item.getInchargeId(), Types.INTEGER);
            stmt.setString(9, item.getAddedBy());
            stmt.setString(10, item.getDescription());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                return false;
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setItemId(keys.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            System.err.println("❌ Add Item Error: " + e.getMessage());
            return false;
        }
    }

    // ----------------------------------------------------
    // GET ITEM BY BARCODE
    // ----------------------------------------------------
    public Item getItemByBarcode(String barcode) {

        String sql = """
            SELECT i.*,
                   c.category_name,
                   ic.incharge_name,
                   l.location_name
            FROM items i
            LEFT JOIN categories c ON i.category_id = c.category_id
            LEFT JOIN incharge ic ON i.incharge_id = ic.incharge_id
            LEFT JOIN locations l ON i.location_id = l.location_id
            WHERE LOWER(i.barcode) = LOWER(?)
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, barcode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ getItemByBarcode Error: " + e.getMessage());
        }

        return null;
    }

    // ----------------------------------------------------
    // GET ITEM BY ID
    // ----------------------------------------------------
    public Item getItemById(int itemId) {

        String sql = """
            SELECT 
                i.*,
                c.category_name,
                ic.incharge_name,
                l.location_name,
                u.unit_name
            FROM items i
            LEFT JOIN categories c ON i.category_id = c.category_id
            LEFT JOIN incharge ic ON i.incharge_id = ic.incharge_id
            LEFT JOIN locations l ON i.location_id = l.location_id
            LEFT JOIN units u ON i.unit_id = u.unit_id
            WHERE i.item_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ getItemById Error: " + e.getMessage());
        }

        return null;
    }

    // ----------------------------------------------------
    // GET ALL ITEMS
    // ----------------------------------------------------
    public List<Item> getAllItems() {

        List<Item> list = new ArrayList<>();

        String sql = """
        SELECT i.*,
               c.category_name,
               ic.incharge_name,
               l.location_name,
               u.unit_name
        FROM items i
        LEFT JOIN categories c ON i.category_id = c.category_id
        LEFT JOIN incharge ic ON i.incharge_id = ic.incharge_id
        LEFT JOIN locations l ON i.location_id = l.location_id
        LEFT JOIN units u ON i.unit_id = u.unit_id
        ORDER BY i.item_name ASC
    """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Load Items Error: " + e.getMessage());
        }

        return list;
    }

    // ----------------------------------------------------
    // UPDATE ITEM
    // ----------------------------------------------------
    public void updateItem(Item item) {

        String sql = """
        UPDATE items SET
            item_name=?,
            barcode=?,
            category_id=?,
            unit_id=?,
            date_acquired=?,
            status=?,
            location_id=?,
            incharge_id=?,
            added_by=?,
            description=?
        WHERE item_id=?
    """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getItemCode());
            stmt.setInt(3, item.getCategoryId());

            // ✅ FIXED UNIT
            stmt.setObject(4, item.getUnitId(), Types.INTEGER);

            if (item.getDateAcquired() != null) {
                stmt.setDate(5, Date.valueOf(item.getDateAcquired()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, item.getStatus());
            stmt.setObject(7, item.getLocationId(), Types.INTEGER);
            stmt.setObject(8, item.getInchargeId(), Types.INTEGER);
            stmt.setString(9, item.getAddedBy());
            stmt.setString(10, item.getDescription());
            stmt.setInt(11, item.getItemId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Update Item Error: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // ✅ UPDATE ITEM STATUS (THIS IS THE METHOD YOU ASKED ABOUT)
    // ----------------------------------------------------
    public boolean updateItemStatus(int itemId, String status) {

        String sql = "UPDATE items SET status=? WHERE item_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, itemId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Update Item Status Error: " + e.getMessage());
            return false;
        }
    }

    // ----------------------------------------------------
    // DELETE ITEM
    // ----------------------------------------------------
    public void deleteItem(int itemId) {

        String sql = "DELETE FROM items WHERE item_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Delete Item Error: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // MAPPING HELPER
    // ----------------------------------------------------
    private Item map(ResultSet rs) throws SQLException {

        Item item = new Item();

        item.setItemId(rs.getInt("item_id"));
        item.setItemName(rs.getString("item_name"));
        item.setItemCode(rs.getString("barcode"));
        item.setCategoryId(rs.getInt("category_id"));

        // ✅ FIXED UNIT
        item.setUnitId((Integer) rs.getObject("unit_id"));
        item.setUnitName(rs.getString("unit_name"));

        item.setStatus(rs.getString("status"));
        item.setLocationId((Integer) rs.getObject("location_id"));
        item.setInchargeId((Integer) rs.getObject("incharge_id"));
        item.setAddedBy(rs.getString("added_by"));
        item.setDescription(rs.getString("description"));

        Date acquired = rs.getDate("date_acquired");
        if (acquired != null) {
            item.setDateAcquired(acquired.toLocalDate());
        }

        Timestamp last = rs.getTimestamp("last_scanned");
        if (last != null) {
            item.setLastScanned(last.toLocalDateTime());
        }

        // Joined display fields
        item.setCategoryName(rs.getString("category_name"));
        item.setInChargeName(rs.getString("incharge_name"));
        item.setLocationName(rs.getString("location_name"));

        return item;
    }

    public int getTotalItems() {
        String sql = "SELECT COUNT(*) FROM items";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getAvailableItemsCount() {
        String sql = "SELECT COUNT(*) FROM items WHERE status = 'AVAILABLE'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getBorrowedItemsCount() {
        String sql = "SELECT COUNT(*) FROM items WHERE status = 'BORROWED'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getDamagedItemsCount() {
        String sql = "SELECT COUNT(*) FROM items WHERE status = 'DAMAGED'";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
