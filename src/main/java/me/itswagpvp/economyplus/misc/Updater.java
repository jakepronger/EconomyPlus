package me.itswagpvp.economyplus.misc;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDate;
import java.util.regex.Matcher;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;
import static me.itswagpvp.economyplus.utils.Config.config;
import static me.itswagpvp.economyplus.utils.Sounds.sounds;

public class Updater implements Listener {

    int behind = 0;
    int ahead = 0;

    boolean alreadyDownloaded = false;
    double currentVersion = config.getVersion();
    double latestGitVersion = 0;

    public void check() {

        if (!plugin.updater || alreadyDownloaded) return;

        if (currentVersion < getLatestGitVersion()) {

            behind = Integer.parseInt(String.valueOf(Math.round((getLatestGitVersion() - currentVersion) / 0.1)).replace(".0", ""));

            Bukkit.getConsoleSender().sendMessage("§f-> §dEconomy§5Plus §cis outdated! (" + "v" + currentVersion + ")");
            Bukkit.getConsoleSender().sendMessage("   - §fYou are behind §c" + behind + " §fversion/s!");
            Bukkit.getConsoleSender().sendMessage("   - §fUpdate to §dv" + getLatestGitVersion() + " §fusing §d/ep update");
            Bukkit.getConsoleSender().sendMessage("");

        } else if (currentVersion > getLatestGitVersion()) {

            ahead = Integer.parseInt(String.valueOf(Math.round((currentVersion - getLatestGitVersion()) / 0.1)).replace(".0", ""));

            Bukkit.getConsoleSender().sendMessage("§f-> §dEconomy§5Plus §cis over updated! (" + "v" + currentVersion + ")");
            Bukkit.getConsoleSender().sendMessage("   - §fYou are ahead §d" + ahead + " §fversion/s!");
            Bukkit.getConsoleSender().sendMessage("   - §fDowngrade to §dv" + getLatestGitVersion() + " §ffor a stable build.");
            Bukkit.getConsoleSender().sendMessage("");

        }

    }

    public double getLatestGitVersion() {

        if (latestGitVersion != 0) {
            return latestGitVersion;
        }

        File file = new File(plugin.getDataFolder() + File.separator + "storage.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        int days = plugin.getConfig().getInt("Updater.Check-Github", 1);
        if (days < 0) { days = 0; }

        if (!(days == 0)) {
            if (!(config.get("last-checked") == null && config.get("git-version") == null)) {
                int checked = config.getInt("last-checked");
                double version = config.getDouble("git-version");
                if ((LocalDate.now().getDayOfYear() - checked) < days) {
                    latestGitVersion = version;
                    return version;
                }
            }
        }

        try {

            URL url = new URL("https://api.github.com/repos/ItsWagPvP/EconomyPlus/tags");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = br.readLine()) != null) {

                if (line.contains("name")) {

                    line = line.split(":")[1];

                    line = line.replaceAll("\"", "");
                    line = line.replace(",zipball_url", "");

                    // line may not be needed as we migrate to tags with just numbers and no letters like "V"
                    line = line.replaceAll("V", ""); //removes V in version if there is a V in the tag name

                    latestGitVersion = Double.parseDouble(line);

                }

            }

            connection.disconnect();
            inputStream.close();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!(days == 0)) {
            config.set("git-version", latestGitVersion);
            config.set("last-checked", LocalDate.now().getDayOfYear());
            try {
                config.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (latestGitVersion == 0) {
            plugin.getLogger().warning("Error with updater finding latest version!");
            return currentVersion;
        }

        return latestGitVersion;

    }

    private boolean getUpdateAvailable() { return !alreadyDownloaded && currentVersion < getLatestGitVersion(); }

    public void checkForPlayerUpdate(Player p) {

        if (!plugin.updater || alreadyDownloaded) return;

        boolean notifications = plugin.getConfig().getBoolean("Updater.Notifications", true);

        if (!getUpdateAvailable()) {
            return;
        }

        if (notifications) {
            behind = Integer.parseInt(String.valueOf(Math.round((getLatestGitVersion() - currentVersion) / 0.1)).replace(".0", ""));
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
            p.sendMessage("" +
                    "§7An update is available for §dEconomyPlus§7! §d(v" + getLatestGitVersion() + ")" +
                    "\n§7You are §c" + behind + " §7versions behind! §c(v" + currentVersion + ")" +
                    "\n§7You can download it with §a/ep update");
        }

    }

    public void downloadUpdate(CommandSender p) {

        if (!plugin.updater) {
            p.sendMessage("§cThe updater is disabled.");
            sounds.error(p);
            return;
        }

        if (!p.hasPermission("economyplus.update")) {
            p.sendMessage(plugin.getMessage("noPerms"));
            return;
        }

        if (!getUpdateAvailable() || alreadyDownloaded) {
            p.sendMessage(("§cThere is no update to download!"));
            sounds.error(p);
            return;
        }

        String jarName = "EconomyPlus.jar";
        String[] slashParts = plugin.getDataFolder().toString().split(Matcher.quoteReplacement(File.separator));
        StringBuilder pluginsPath = new StringBuilder();


        int i = 0;
        for (String part : slashParts) {
            pluginsPath.append(part).append(File.separator);
            i++;
            if (i + 1 >= slashParts.length) break;
        }

        File oldJar = new File(pluginsPath + jarName);
        if (!oldJar.exists()) {
            Bukkit.getLogger().warning("[EconomyPlus] Unable to find jar " + pluginsPath + jarName);
            p.sendMessage("§cUnable to find old jar! §7Please make sure it matches the format §fEconomyPlus.jar§7!");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            try {
                URL website = new URL("https://github.com/ItsWagPvP/EconomyPlus/releases/download/V" + getLatestGitVersion() + "/EconomyPlus.jar");
                HttpURLConnection con = (HttpURLConnection) website.openConnection();

                ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
                FileOutputStream fos = new FileOutputStream(pluginsPath + "EconomyPlus.jar");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                con.disconnect();
                rbc.close();
                fos.close();
                p.sendMessage("§aDone! §c§nIt is recommended you restart your server now for the plugin to update.");
                alreadyDownloaded = true;
            } catch (Exception e) {
                p.sendMessage("§cAn error occurred while trying to download the newest version. Check console for more info");
                e.printStackTrace();
            }

        });

    }

}