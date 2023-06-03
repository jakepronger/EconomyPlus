package me.itswagpvp.economyplus.database.mysql;

import me.itswagpvp.economyplus.listeners.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;

public class MySQL {

    public static MySQL mySQL = new MySQL();
    public Initializer initializer = new Initializer();

    public Connection con = Initializer.con;
    public String table = Initializer.table;

    public Double getTokens(String player) { // Retrieve the balance of the player

        try (
                PreparedStatement ps = con.prepareStatement("SELECT * FROM " + table + " WHERE player = '" + player + "';");
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                if (rs.getString("player").equalsIgnoreCase(player)) {
                    return rs.getDouble("moneys");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
        }

        return 0.00;

    }

    // Save the balance to the player's database
    public void setTokens(String player, Double tokens) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try (
                    PreparedStatement ps = con.prepareStatement("REPLACE INTO " + table + " (player,moneys,bank) VALUES(?,?,?)")
            ) {

                ps.setString(1, player);

                ps.setDouble(2, tokens);

                ps.setDouble(3, getBank(player));

                ps.executeUpdate();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
            }
        });
    }

    // Retrieve the bank of the player
    public double getBank(String player) {

        try (
                PreparedStatement ps = con.prepareStatement("SELECT * FROM " + table + " WHERE player = '" + player + "';");
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                if (rs.getString("player").equalsIgnoreCase(player)) {
                    return rs.getDouble("bank");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
        }

        return 0;
    }

    // Save the balance to the player's database
    public void setBank(String player, Double tokens) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try (
                    PreparedStatement ps = con.prepareStatement("REPLACE INTO " + table + " (player,moneys,bank) VALUES(?,?,?)")
            ) {

                ps.setString(1, player);

                ps.setDouble(2, getTokens(player));

                ps.setDouble(3, tokens);

                ps.executeUpdate();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
            }
        });
    }

    // Get the list of the players saved
    public List<String> getList() {

        List<String> list = new ArrayList<>();

        try (
                PreparedStatement ps = con.prepareStatement("SELECT player FROM " + table);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                list.add(rs.getString("player"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public LinkedHashMap<String, Double> getOrderedList() {

        LinkedHashMap<String, Double> map = new LinkedHashMap<>();

        try (
                PreparedStatement ps = con.prepareStatement("SELECT * FROM " + new MySQL().table + " ORDER BY moneys DESC");
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                map.put(rs.getString("player"), rs.getDouble(2));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return map;
    }

    // Create a player account
    public boolean createPlayer(String player) {
        setTokens(player, plugin.getConfig().getDouble("Starting-Balance"));
        setBank(player, plugin.getConfig().getDouble("Starting-Bank-Balance"));
        return true;
    }

    // Remove a user (UUID/NICKNAME) from the database
    public void removeUser(String user) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "DELETE FROM " + table + " where player = '" + user + "'";
            try {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Convert a user (UUID/NICKNAME) from the database
    public void convertUser(OfflinePlayer user, String convertTo) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            String name = PlayerHandler.getName(user.getUniqueId(), true);
            if (name.equalsIgnoreCase(user.getUniqueId().toString())) {
                return; // invalid user whilst trying to change
            }

            String uuid = String.valueOf(user.getUniqueId());

            if (convertTo.equalsIgnoreCase("UUID")) {

                try {
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE " + table + " " +
                                    "SET player = \"" + uuid + "\" " +
                                    "WHERE player = \"" + name + "\"");
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
                }

            } else if (convertTo.equalsIgnoreCase("NICKNAME")) {

                try {
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE " + table + " " +
                                    "SET player = \"" + name + "\" " +
                                    "WHERE player = \"" + uuid + "\"");
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
                }

            }
        });
    }

}