package inventorysystem.dao;

import inventorysystem.models.BorrowRecord;
import inventorysystem.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDAO {

    // -------------------------
    // NEW METHODS FOR WORKFLOW
    // -------------------------

    // Quick insert when borrowing an item from popup
    public boolean insertBorrow(int itemId, int borrowerId) {
        String sql = "INSERT INTO borrow_records (item_id, borrower_id, status) VALUES (?, ?, 'Borrowed')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.setInt(2, borrowerId);
            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Update item status when borrowed
    public void updateItemStatusToBorrowed(int itemId) {
        String sql = "UPDATE items SET status='Borrowed' WHERE item_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get borrow history for a specific item
    public List<BorrowRecord> getBorrowRecordsByItemId(int itemId) {
        List<BorrowRecord> list = new ArrayList<>();

        String sql = """
            SELECT br.*, b.borrower_name
            FROM borrow_records br
            JOIN borrowers b ON br.borrower_id = b.borrower_id
            WHERE br.item_id = ?
            ORDER BY br.borrow_date DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    BorrowRecord record = new BorrowRecord(
                            rs.getInt("record_id"),
                            rs.getInt("item_id"),
                            rs.getInt("borrower_id"),
                            rs.getTimestamp("borrow_date").toLocalDateTime().toLocalDate(),
                            rs.getTimestamp("return_date") != null ?
                                    rs.getTimestamp("return_date").toLocalDateTime().toLocalDate() : null,
                            rs.getString("status")
                    );

                    // Add borrower name to record (must exist in model)
                    record.setBorrowerName(rs.getString("borrower_name"));

                    list.add(record);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // --------------------------------------------------
    // ORIGINAL METHODS (KEPT INTACT, ALREADY WORKING)
    // --------------------------------------------------

    // Insert a full borrow record (used in other parts of your system)
    public void addBorrowRecord(BorrowRecord record) {
        String sql = "INSERT INTO borrow_records (item_id, borrower_id, borrow_date, return_date, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, record.getItemId());
            stmt.setInt(2, record.getBorrowerId());
            stmt.setDate(3, Date.valueOf(record.getBorrowDate()));

            if (record.getReturnDate() != null) {
                stmt.setDate(4, Date.valueOf(record.getReturnDate()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setString(5, record.getStatus());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all borrow records
    public List<BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BorrowRecord r = new BorrowRecord(
                        rs.getInt("record_id"),
                        rs.getInt("item_id"),
                        rs.getInt("borrower_id"),
                        rs.getDate("borrow_date").toLocalDate(),
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                        rs.getString("status")
                );

                list.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Get a borrow record by ID
    public BorrowRecord getBorrowRecordById(int id) {
        String sql = "SELECT * FROM borrow_records WHERE record_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new BorrowRecord(
                            rs.getInt("record_id"),
                            rs.getInt("item_id"),
                            rs.getInt("borrower_id"),
                            rs.getDate("borrow_date").toLocalDate(),
                            rs.getDate("return_date") != null ?
                                    rs.getDate("return_date").toLocalDate() : null,
                            rs.getString("status")
                    );
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update a borrow record fully
    public void updateBorrowRecord(BorrowRecord record) {
        String sql = "UPDATE borrow_records SET item_id=?, borrower_id=?, borrow_date=?, return_date=?, status=? WHERE record_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, record.getItemId());
            stmt.setInt(2, record.getBorrowerId());
            stmt.setDate(3, Date.valueOf(record.getBorrowDate()));

            if (record.getReturnDate() != null) {
                stmt.setDate(4, Date.valueOf(record.getReturnDate()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setString(5, record.getStatus());
            stmt.setInt(6, record.getRecordId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a borrow record
    public void deleteBorrowRecord(int id) {
        String sql = "DELETE FROM borrow_records WHERE record_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get borrow records by borrower
    public List<BorrowRecord> getBorrowRecordsByBorrower(int borrowerId) {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE borrower_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, borrowerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord r = new BorrowRecord(
                            rs.getInt("record_id"),
                            rs.getInt("item_id"),
                            rs.getInt("borrower_id"),
                            rs.getDate("borrow_date").toLocalDate(),
                            rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                            rs.getString("status")
                    );

                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Mark a borrow as returned
    public void returnBorrowRecord(int recordId, LocalDate returnDate, String remarks) {
        String sql = "UPDATE borrow_records SET return_date=?, status=?, remarks=? WHERE record_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(returnDate));
            stmt.setString(2, "Returned");
            stmt.setString(3, remarks);
            stmt.setInt(4, recordId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
