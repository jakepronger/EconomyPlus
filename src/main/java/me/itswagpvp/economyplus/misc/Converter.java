package me.itswagpvp.economyplus.misc;

import me.itswagpvp.economyplus.EconomyPlus;
import me.itswagpvp.economyplus.database.misc.DatabaseType;
import me.itswagpvp.economyplus.database.misc.StorageMode;
import me.itswagpvp.economyplus.database.mysql.MySQL;
import me.itswagpvp.economyplus.database.sqlite.SQLite;
import me.itswagpvp.economyplus.PlayerHandler;
import static me.itswagpvp.economyplus.EconomyPlus.plugin;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;

public class Converter {

    public int NameToUUID() {

        if (EconomyPlus.getStorageMode() == StorageMode.UUID) return -1;

        int accounts = 0;

        plugin.setStorageMode("UUID");

        switch (EconomyPlus.getDBType()) {

            case YAML:

                for (String user : plugin.getYMLData().getConfigurationSection("Data").getKeys(false)) {

                    double money = plugin.getYMLData().getDouble("Data." + user + ".money");
                    double bank = plugin.getYMLData().getDouble("Data." + user + ".bank");

                    OfflinePlayer p = PlayerHandler.getPlayer(user);
                    if (p == null) {
                        continue; // invalid user
                    }

                    plugin.getYMLData().set("Data." + user, null);

                    plugin.getYMLData().set("Data." + p.getUniqueId() + ".money", money);
                    plugin.getYMLData().set("Data." + p.getUniqueId() + ".bank", bank);

                    accounts++;

                }

                plugin.saveYMLConfig();

            case MySQL:

                for (String user : new MySQL().getList()) {

                    OfflinePlayer p = PlayerHandler.getPlayer(user);

                    if (p == null) {
                        continue; // invalid player; skip
                    }

                    new MySQL().convertUser(p, "UUID");
                    accounts++;

                }

            case H2:

                for (String user : new SQLite().getList()) {

                    double money = new SQLite().getTokens(user);
                    double bank = new SQLite().getBank(user);

                    OfflinePlayer p = PlayerHandler.getPlayer(user);
                    if (p == null) {
                        continue; // invalid player; skip
                    }

                    new SQLite().removeUser(user);
                    new SQLite().setTokens(String.valueOf(p.getUniqueId()), money);
                    new SQLite().setBank(String.valueOf(p.getUniqueId()), bank);

                    accounts++;

                }

        }

        return accounts;
    }

    public int UUIDToName() {

        if (EconomyPlus.getStorageMode() == StorageMode.NICKNAME) return -1;
        int accounts = 0;

        plugin.setStorageMode("NICKNAME");

        DatabaseType type = EconomyPlus.getDBType();

        if (type == DatabaseType.YAML) {

        } else if (type == DatabaseType.H2) {

        } else if (type == DatabaseType.MySQL) {

        }

        switch (EconomyPlus.getDBType()) {

            case YAML:

                ConfigurationSection cs = plugin.getYMLData().getConfigurationSection("Data");

                if (cs == null) {
                    return 0;
                }

                for (String user : cs.getKeys(false)) {

                    double money = plugin.getYMLData().getDouble("Data." + user + ".money");
                    double bank = plugin.getYMLData().getDouble("Data." + user + ".bank");

                    plugin.getYMLData().set("Data." + user, null);

                    String name = PlayerHandler.getName(UUID.fromString(user), true);
                    if (name.equalsIgnoreCase(user)) {
                        continue; // skip invalid user
                    }

                    plugin.getYMLData().set("Data." + name + ".money", money);
                    plugin.getYMLData().set("Data." + name + ".bank", bank);

                    /*

                    if (CacheManager.getCache(1).containsKey(user)) {
                        CacheManager.getCache(1).remove(user);
                        CacheManager.getCache(1).put(name, money);
                    }

                    if (CacheManager.getCache(2).containsKey(user)) {
                        CacheManager.getCache(2).remove(user);
                        CacheManager.getCache(2).put(name, bank);
                    }

                     */

                    accounts++;

                }

                plugin.saveYMLConfig();

            case H2:

                for (String user : new SQLite().getList()) {

                    double money = new SQLite().getTokens(user);
                    double bank = new SQLite().getBank(user);

                    String name = PlayerHandler.getName(UUID.fromString(user), true);
                    if (name.equalsIgnoreCase(user)) {
                        continue; // skip invalid user
                    }

                    new SQLite().removeUser(user);

                    new SQLite().setTokens(name, money);
                    new SQLite().setBank(name, bank);

                    accounts++;

                }

            case MySQL:

                for (String user : new MySQL().getList()) {
                    new MySQL().convertUser(PlayerHandler.getPlayer(user), "NICKNAME");
                    accounts++;
                }

        }

        return accounts;
    }

}
