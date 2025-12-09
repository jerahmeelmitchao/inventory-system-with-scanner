package inventorysystem.dao;

import inventorysystem.models.BorrowRecord;
import inventorysystem.utils.DatabaseConnection;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDAO {

    // ---------------------------------------------------------
    // INSERT BORROW (simple insert when scanning)
    // ---------------------------------------------------------
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

    // =========================================================
    // 🔥 DAYS OVERDUE CALCULATOR (Based on 5-day limit)
    // =========================================================
    private int calculateDaysOverdue(LocalDateTime borrowDate) {
        if (borrowDate == null) {
            return 0;
        }

        long days = Duration.between(
                borrowDate.toLocalDate().atStartOfDay(),
                LocalDate.now().atStartOfDay()
        ).toDays();

        return days > 5 ? (int) (days - 5) : 0;
    }

    // =========================================================
    // GET OVERDUE RECORDS (Over X days)
    // =========================================================
    public List<BorrowRecord> getOverdueRecords(int days) {
        List<BorrowRecord> list = new ArrayList<>();

        String sql = """
            SELECT r.*, b.borrower_name
            FROM borrow_records r
            JOIN borrowers b ON r.borrower_id = b.borrower_id
            WHERE r.status = 'Borrowed'
            AND TIMESTAMPDIFF(DAY, r.borrow_date, NOW()) > ?
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BorrowRecord rec = new BorrowRecord();
                rec.setRecordId(rs.getInt("record_id"));
                rec.setItemId(rs.getInt("item_id"));
                rec.setBorrowerId(rs.getInt("borrower_id"));
                rec.setBorrowerName(rs.getString("borrower_name"));
                rec.setBorrowDate(rs.getTimestamp("borrow_date").toLocalDateTime());
                rec.setStatus("Missing");

                int overdue = calculateDaysOverdue(rec.getBorrowDate());
                rec.setDaysOverdue(overdue);

                list.add(rec);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 🚀 Get overdue records and auto-mark Missing
    public List<BorrowRecord> getAndMarkOverdue(int days) {
        List<BorrowRecord> list = new ArrayList<>();

        String sql = """
        SELECT r.*, b.borrower_name 
        FROM borrow_records r
        JOIN borrowers b ON r.borrower_id = b.borrower_id
        WHERE r.status = 'Borrowed'
          AND r.return_date IS NULL
          AND TIMESTAMPDIFF(DAY, r.borrow_date, NOW()) > ?
    """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                BorrowRecord rec = new BorrowRecord();
                rec.setRecordId(rs.getInt("record_id"));
                rec.setItemId(rs.getInt("item_id"));
                rec.setBorrowerId(rs.getInt("borrower_id"));
                rec.setBorrowerName(rs.getString("borrower_name"));
                rec.setBorrowDate(rs.getTimestamp("borrow_date").toLocalDateTime());
                rec.setStatus("Missing");

                list.add(rec);

                // Update borrow record to Missing
//                markMissing(rec.getRecordId());

                // Update item table as Missing
                updateItemMissing(rec.getItemId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

// ✔ update borrow record status
//    private void markMissing(int recordId) {
//        String sql = "UPDATE borrow_records SET status='Missing' WHERE record_id=?";
//        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, recordId);
//            ps.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

// ✔ update items table
    private void updateItemMissing(int itemId) {
        String sql = "UPDATE items SET status='Missing' WHERE item_id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// ✔ count overdue for notification badge
    public int getOverdueCount(int days) {
        String sql = """
        SELECT COUNT(*) 
        FROM borrow_records
        WHERE status = 'Borrowed'
          AND return_date IS NULL
          AND TIMESTAMPDIFF(DAY, borrow_date, NOW()) > ?
    """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // ---------------------------------------------------------
    // UPDATE STATUS: BORROWED
    // ---------------------------------------------------------
    public void updateItemStatusToBorrowed(int itemId) {
        String sql = "UPDATE items SET status='Borrowed' WHERE item_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // GET RECORDS BY ITEM ID
    // =========================================================
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
                            ? rs.getTimestamp("return_date").toLocalDateTime()
                            : null,
                            rs.getString("status"),
                            rs.getString("remarks")
                    );

                    record.setBorrowerName(rs.getString("borrower_name"));

                    // NEW: overdue detection
                    int overdue = calculateDaysOverdue(record.getBorrowDate());
                    record.setDaysOverdue(overdue);

                    if (overdue > 0 && record.getReturnDate() == null) {
                        record.setStatus("Missing");
                    }

                    list.add(record);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================================================
    // INSERT FULL BORROW RECORD
    // =========================================================
    public void addBorrowRecord(BorrowRecord record) {
        String sql = """
            INSERT INTO borrow_records 
            (item_id, borrower_id, borrow_date, return_date, status, remarks)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

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

    // =========================================================
    // GET ALL RECORDS
    // =========================================================
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
                        ? rs.getTimestamp("return_date").toLocalDateTime()
                        : null,
                        rs.getString("status"),
                        rs.getString("remarks")
                );

                int overdue = calculateDaysOverdue(r.getBorrowDate());
                r.setDaysOverdue(overdue);

                if (overdue > 0 && r.getReturnDate() == null) {
                    r.setStatus("Missing");
                }

                list.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================================================
    // GET BY RECORD ID
    // =========================================================
    public BorrowRecord getBorrowRecordById(int id) {
        String sql = "SELECT * FROM borrow_records WHERE record_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    BorrowRecord r = new BorrowRecord(
                            rs.getInt("record_id"),
                            rs.getInt("item_id"),
                            rs.getInt("borrower_id"),
                            rs.getTimestamp("borrow_date").toLocalDateTime(),
                            rs.getTimestamp("return_date") != null
                            ? rs.getTimestamp("return_date").toLocalDateTime()
                            : null,
                            rs.getString("status"),
                            rs.getString("remarks")
                    );

                    int overdue = calculateDaysOverdue(r.getBorrowDate());
                    r.setDaysOverdue(overdue);

                    if (overdue > 0 && r.getReturnDate() == null) {
                        r.setStatus("Missing");
                    }

                    return r;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================================================
    // UPDATE RECORD
    // =========================================================
    public void updateBorrowRecord(BorrowRecord record) {
        String sql = """
            UPDATE borrow_records 
            SET item_id=?, borrower_id=?, borrow_date=?, return_date=?, status=?, remarks=? 
            WHERE record_id=?
        """;

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

    // =========================================================
    // DELETE RECORD
    // =========================================================
    public void deleteBorrowRecord(int id) {
        String sql = "DELETE FROM borrow_records WHERE record_id=?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // GET RECORDS BY BORROWER
    // =========================================================
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
                            ? rs.getTimestamp("return_date").toLocalDateTime()
                            : null,
                            rs.getString("status"),
                            rs.getString("remarks")
                    );

                    int overdue = calculateDaysOverdue(r.getBorrowDate());
                    r.setDaysOverdue(overdue);

                    if (overdue > 0 && r.getReturnDate() == null) {
                        r.setStatus("Missing");
                    }

                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================================================
    // MARK AS RETURNED
    // =========================================================
    public void returnBorrowRecord(int recordId, LocalDateTime returnDate, String remarks) {
        String sql = """
            UPDATE borrow_records 
            SET return_date=?, status=?, remarks=? 
            WHERE record_id=?
        """;

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

    // =========================================================
    // COUNTS (Dashboard)
    // =========================================================
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
