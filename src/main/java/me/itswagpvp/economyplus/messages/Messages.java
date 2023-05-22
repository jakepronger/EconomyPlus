package me.itswagpvp.economyplus.messages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static me.itswagpvp.economyplus.EconomyPlus.plugin;

public class Messages {

    public static Messages messages = new Messages();

    private static final String path = plugin.getDataFolder() + "/messages";

    public void load() {

        List<String> languages = getLanguages();

        for (String name : languages) {

            File file = new File(path, name);

            if (!file.exists()) {
                plugin.saveResource("language" + File.separator + name, false);
            }

        }

    }

    public FileConfiguration getMessageConfig(String name) {
        File file = new File(path, name + ".yml");
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(new File(path, name + ".yml"));
        }
        return null;
    }

    public List<String> getLanguages() { // returns a list of file names that don't contain a / in the path

        // loop through all files in recourses folder

        // exclude config.yml, data.yml, plugin.yml and storage.yml
        List<String> exclude = Arrays.asList("config.yml", "data.yml", "plugin.yml", "storage.yml");

        List<String> list = new ArrayList<>();

        try {

            String s = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath();

            JarFile jarFile = new JarFile(s);
            Enumeration<? extends JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.contains("/") && name.contains(".yml") && name.length() == 6) {
                    list.add(name);
                }
            }

            // excludes files that aren't language files
            for (String entry : exclude) {
                if (exclude.contains(entry)) {
                    list.remove(entry);
                }
            }

            return list;

        } catch (IOException ex) {
            ex.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[EconomyPlus] Error loading languages!");
        } catch (URISyntaxException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[EconomyPlus] Error loading languages!");
            throw new RuntimeException(e);
        }

        return list;

    }

}
