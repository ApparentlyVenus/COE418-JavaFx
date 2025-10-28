package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {

    // ✅ MANUAL TABLE CREATION with custom columns
    public static boolean createCustomTable(String tableName, String columns) {
        if (tableName == null || tableName.trim().isEmpty()) {
            System.out.println("❌ Table name cannot be empty!");
            return false;
        }
        
        String query = "CREATE TABLE IF NOT EXISTS " + tableName.trim() + " (" + columns + ")";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.execute();
            System.out.println("✅ Table '" + tableName + "' created successfully!");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error creating table '" + tableName + "': " + e.getMessage());
            return false;
        }
    }

    // ✅ GET ALL TABLE NAMES in database
    public static List<String> getAllTables() {
        List<String> tables = new ArrayList<>();
        String query = "SHOW TABLES";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching tables: " + e.getMessage());
        }
        return tables;
    }

    // ✅ DESCRIBE TABLE STRUCTURE
    public static String getTableStructure(String tableName) {
        StringBuilder structure = new StringBuilder();
        String query = "DESCRIBE " + tableName;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            structure.append("Table: ").append(tableName).append("\n");
            structure.append("Field | Type | Null | Key | Default | Extra\n");
            structure.append("--------------------------------------------\n");
            
            while (rs.next()) {
                structure.append(rs.getString("Field")).append(" | ")
                         .append(rs.getString("Type")).append(" | ")
                         .append(rs.getString("Null")).append(" | ")
                         .append(rs.getString("Key")).append(" | ")
                         .append(rs.getString("Default")).append(" | ")
                         .append(rs.getString("Extra")).append("\n");
            }
        } catch (SQLException e) {
            return "❌ Error describing table: " + e.getMessage();
        }
        return structure.toString();
    }

    // ✅ INSERT DATA into any table
    public static boolean insertData(String tableName, String[] columns, String[] values) {
        if (columns.length != values.length) {
            System.out.println("❌ Columns and values count mismatch!");
            return false;
        }
        
        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                columnBuilder.append(", ");
                valueBuilder.append(", ");
            }
            columnBuilder.append(columns[i]);
            valueBuilder.append("?");
        }
        
        String query = "INSERT INTO " + tableName + " (" + columnBuilder + ") VALUES (" + valueBuilder + ")";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            
            ps.executeUpdate();
            System.out.println("✅ Data inserted into '" + tableName + "' successfully!");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error inserting data: " + e.getMessage());
            return false;
        }
    }

    // ✅ SELECT DATA from any table with LIMIT
    public static List<String[]> selectData(String tableName, int limit) {
        List<String[]> results = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Add column headers
            String[] headers = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                headers[i-1] = metaData.getColumnName(i);
            }
            results.add(headers);
            
            // Add data rows
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i-1] = rs.getString(i);
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error selecting data: " + e.getMessage());
        }
        return results;
    }

    // ✅ CUSTOM JOIN between two tables
    public static List<String[]> customJoin(String table1, String table2, String joinCondition, int limit) {
        List<String[]> results = new ArrayList<>();
        String query = "SELECT * FROM " + table1 + " JOIN " + table2 + " ON " + joinCondition + " LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Add column headers
            String[] headers = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                headers[i-1] = metaData.getColumnName(i);
            }
            results.add(headers);
            
            // Add data rows
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i-1] = rs.getString(i);
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error in JOIN: " + e.getMessage());
        }
        return results;
    }

    // ✅ DROP any table
    public static boolean dropTable(String tableName) {
        String query = "DROP TABLE IF EXISTS " + tableName;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.execute();
            System.out.println("⚠️ Table '" + tableName + "' dropped!");
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error dropping table: " + e.getMessage());
            return false;
        }
    }

    // ✅ UPDATE data in any table
    public static boolean updateData(String tableName, String setClause, String whereClause) {
        String query = "UPDATE " + tableName + " SET " + setClause + " WHERE " + whereClause;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            int rows = ps.executeUpdate();
            System.out.println("✅ " + rows + " row(s) updated in '" + tableName + "'");
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating data: " + e.getMessage());
            return false;
        }
    }

    // ✅ DELETE data from any table
    public static boolean deleteData(String tableName, String whereClause) {
        String query = "DELETE FROM " + tableName + " WHERE " + whereClause;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            int rows = ps.executeUpdate();
            System.out.println("✅ " + rows + " row(s) deleted from '" + tableName + "'");
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error deleting data: " + e.getMessage());
            return false;
        }
    }
}