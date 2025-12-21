package inventorysystem.dao;

import inventorysystem.models.Unit;
import inventorysystem.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnitDAO {

    public List<Unit> getAllUnits() {
        List<Unit> list = new ArrayList<>();
        String sql = "SELECT * FROM units ORDER BY unit_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Unit(
                        rs.getInt("unit_id"),
                        rs.getString("unit_name"),
                        rs.getString("description")
                ));
            }

        } catch (SQLException e) {
            System.err.println("❌ Load Units Error: " + e.getMessage());
        }
        return list;
    }

    public boolean addUnit(Unit unit) {
        String sql = "INSERT INTO units (unit_name, description) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, unit.getUnitName());
            ps.setString(2, unit.getDescription());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Add Unit Error: " + e.getMessage());
        }
        return false;
    }

    public boolean updateUnit(Unit unit) {
        String sql = "UPDATE units SET unit_name=?, description=? WHERE unit_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, unit.getUnitName());
            ps.setString(2, unit.getDescription());
            ps.setInt(3, unit.getUnitId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Update Unit Error: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteUnit(int id) {
        String sql = "DELETE FROM units WHERE unit_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Delete Unit Error: " + e.getMessage());
        }
        return false;
    }
}
