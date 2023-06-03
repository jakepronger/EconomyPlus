package me.itswagpvp.economyplus.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;

public class Initializer {

    public static Connection con;

    final String user = plugin.getConfig().getString("Database.User");
    final String password = plugin.getConfig().getString("Database.Password");
    final String host = plugin.getConfig().getString("Database.Host");
    final String port = plugin.getConfig().getString("Database.Port");
    final String database = plugin.getConfig().getString("Database.Database");
    final static String table = plugin.getConfig().getString("Database.Table");
    final boolean autoReconnect = plugin.getConfig().getBoolean("Database.AutoReconnect");
    final boolean useSSL = plugin.getConfig().getBoolean("Database.useSSL", false);
    final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=" + autoReconnect + "&useSSL=" + useSSL + "&characterEncoding=utf8";

    // Connect to the database
    public void connect() {

        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close the database connection if not null
    public void closeConnection() {

        try {
            if (con != null && !con.isClosed()) con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createTable() {

        String sql = "CREATE TABLE " + table + " ("
                + "player VARCHAR(45) NOT NULL,"
                + "moneys DOUBLE NOT NULL,"
                + "bank DOUBLE NOT NULL,"
                + "PRIMARY KEY (player))";
        try {

            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.executeUpdate();

        } catch (SQLException e) {

            if (e.toString().contains("Table '" + table + "' already exists")) {
                return;
            }

            e.printStackTrace();

        }

    }

    public void updateTable() {

        String sql = "ALTER TABLE " + table + " ADD COLUMN bank DOUBLE";

        try {

            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.toString().contains("Duplicate column name 'bank'")) {
                return;
            }
            e.printStackTrace();
        }

    }

}
