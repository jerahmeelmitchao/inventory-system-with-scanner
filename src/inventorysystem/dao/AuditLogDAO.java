package inventorysystem.dao;

import inventorysystem.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AuditLogDAO {

    public static void log(String username, String action, String details) {
        String sql = "INSERT INTO audit_log (username, action_type, description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, action);
            ps.setString(3, details);

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
