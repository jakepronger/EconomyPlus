package me.itswagpvp.economyplus;

import me.itswagpvp.economyplus.misc.Updater;
import me.itswagpvp.economyplus.hooks.vault.Economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;

public class PlayerHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean("Updater.Notifications", true)) {
            if (p.hasPermission("economyplus.update")) {
                Updater.checkForPlayerUpdate(p);
            }
        }

        Economy eco = new Economy(p);
        if (!(eco.hasAccount(p))) { //player doesn't have an account
            eco.setBalance(plugin.getConfig().getDouble("Starting-Balance", 0.00D));
            eco.setBank(plugin.getConfig().getDouble("Starting-Bank-Balance", 0.00D));
        }

        saveName(p);

    }

    public static OfflinePlayer getPlayer(String name) {

        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op.getName() != null && op.getName().equalsIgnoreCase(name)) {
                return op;
            }
        }

        return null;

    }

    public static String getName(UUID uuid, boolean ifInvalidReturnUUID) {

        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);

        if (op.getName() != null) return op.getName();

        // check stored names
        if (plugin.SAVE_NAMES) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(getStorageFile());
            if (config.getString("usernames." + uuid) != null) { // check if name is stored
                return config.getString("usernames." + uuid); // if so return it
            }
        }

        // USER IS INVALID

        if (ifInvalidReturnUUID) {
            // will return uuid
            return String.valueOf(uuid);
        }

        return "Invalid User";
    }

    public static void saveName(OfflinePlayer player) {

        if (!plugin.SAVE_NAMES || player == null) {
            return;
        }

        String name = Bukkit.getOfflinePlayer(player.getUniqueId()).getName();
        if (name == null) {
            return;
        }

        File file = getStorageFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("usernames." + player.getUniqueId(), name);

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static File getStorageFile() {

        File file = new File(plugin.getDataFolder() + File.separator + "storage.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

}