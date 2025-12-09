package inventorysystem.dao;

import inventorysystem.models.BorrowRecord;
import inventorysystem.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDAO {

    // Quick insert when borrowing an item
    public boolean insertBorrow(int itemId, int borrowerId) {
        String sql = "INSERT INTO borrow_records (item_id, borrower_id, status) VALUES (?, ?, 'Borrowed')";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.setInt(2, borrowerId);
            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Update item status
    public void updateItemStatusToBorrowed(int itemId) {
        String sql = "UPDATE items SET status='Borrowed' WHERE item_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Borrow records by item
    public List<BorrowRecord> getBorrowRecordsByItemId(int itemId) {
        List<BorrowRecord> list = new ArrayList<>();

        String sql = """
            SELECT br.*, b.borrower_name
            FROM borrow_records br
            JOIN borrowers b ON br.borrower_id = b.borrower_id
            WHERE br.item_id = ?
            ORDER BY br.borrow_date DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    BorrowRecord record = new BorrowRecord(
                            rs.getInt("record_id"),
                            rs.getInt("item_id"),
                            rs.getInt("borrower_id"),
                            rs.getTimestamp("borrow_date").toLocalDateTime(),
                            rs.getTimestamp("return_date") != null
                            ? rs.getTimestamp("return_date").toLocalDateTime() : null,
                            rs.getString("status"),
                            rs.getString("remarks")
                    );

                    record.setBorrowerName(rs.getString("borrower_name"));
                    list.add(record);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Insert a full borrow record
    public void addBorrowRecord(BorrowRecord record) {
        String sql = "INSERT INTO borrow_records (item_id, borrower_id, borrow_date, return_date, status, remarks) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, record.getItemId());
            stmt.setInt(2, record.getBorrowerId());
            stmt.setTimestamp(3, Timestamp.valueOf(record.getBorrowDate()));

            if (record.getReturnDate() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(record.getReturnDate()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setString(5, record.getStatus());
            stmt.setString(6, record.getRemarks());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all borrow records
    public List<BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BorrowRecord r = new BorrowRecord(
                        rs.getInt("record_id"),
                        rs.getInt("item_id"),
                        rs.getInt("borrower_id"),
                        rs.getTimestamp("borrow_date").toLocalDateTime(),
                        rs.getTimestamp("return_date") != null
                        ? rs.getTimestamp("return_date").toLocalDateTime() : null,
                        rs.getString("status"),
                        rs.getString("remarks")
                );

                list.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Get one record by ID
    public BorrowRecord getBorrowRecordById(int id) {
        String sql = "SELECT * FROM borrow_records WHERE record_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new BorrowRecord(
                            rs.getInt("record_id"),
                            rs.getInt("item_id"),
                            rs.getInt("borrower_id"),
                            rs.getTimestamp("borrow_date").toLocalDateTime(),
                            rs.getTimestamp("return_date") != null
                            ? rs.getTimestamp("return_date").toLocalDateTime() : null,
                            rs.getString("status"),
                            rs.getString("remarks")
                    );
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update full record
    public void updateBorrowRecord(BorrowRecord record) {
        String sql = "UPDATE borrow_records SET item_id=?, borrower_id=?, borrow_date=?, return_date=?, status=?, remarks=? WHERE record_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, record.getItemId());
            stmt.setInt(2, record.getBorrowerId());
            stmt.setTimestamp(3, Timestamp.valueOf(record.getBorrowDate()));

            if (record.getReturnDate() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(record.getReturnDate()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setString(5, record.getStatus());
            stmt.setString(6, record.getRemarks());
            stmt.setInt(7, record.getRecordId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete
    public void deleteBorrowRecord(int id) {
        String sql = "DELETE FROM borrow_records WHERE record_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Borrow records by borrower
    public List<BorrowRecord> getBorrowRecordsByBorrower(int borrowerId) {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE borrower_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, borrowerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord r = new BorrowRecord(
                            rs.getInt("record_id"),
                            rs.getInt("item_id"),
                            rs.getInt("borrower_id"),
                            rs.getTimestamp("borrow_date").toLocalDateTime(),
                            rs.getTimestamp("return_date") != null
                            ? rs.getTimestamp("return_date").toLocalDateTime() : null,
                            rs.getString("status"),
                            rs.getString("remarks")
                    );

                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Mark as returned
    public void returnBorrowRecord(int recordId, LocalDateTime returnDate, String remarks) {
        String sql = "UPDATE borrow_records SET return_date=?, status=?, remarks=? WHERE record_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(returnDate));
            stmt.setString(2, "Returned");
            stmt.setString(3, remarks);
            stmt.setInt(4, recordId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotalBorrowRecords() {
        String sql = "SELECT COUNT(*) AS total FROM borrow_records";
        try (var conn = DatabaseConnection.getConnection(); var stmt = conn.prepareStatement(sql); var rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("❌ getTotalBorrowRecords error: " + e.getMessage());
        }
        return 0;
    }

    public int getActiveBorrowRecordsCount() {
        String sql = "SELECT COUNT(*) AS total FROM borrow_records WHERE status = 'Borrowed'";
        try (var conn = DatabaseConnection.getConnection(); var stmt = conn.prepareStatement(sql); var rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("❌ getActiveBorrowRecordsCount error: " + e.getMessage());
        }
        return 0;
    }

    public int getBorrowedTodayCount() {
        String sql = "SELECT COUNT(*) AS total FROM borrow_records WHERE status = 'Borrowed' AND DATE(borrow_date) = CURDATE()";
        try (var conn = DatabaseConnection.getConnection(); var stmt = conn.prepareStatement(sql); var rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("❌ getBorrowedTodayCount error: " + e.getMessage());
        }
        return 0;
    }

    public int getReturnedTodayCount() {
        String sql = "SELECT COUNT(*) AS total FROM borrow_records WHERE status = 'Returned' AND DATE(return_date) = CURDATE()";
        try (var conn = DatabaseConnection.getConnection(); var stmt = conn.prepareStatement(sql); var rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("❌ getReturnedTodayCount error: " + e.getMessage());
        }
        return 0;
    }

}
