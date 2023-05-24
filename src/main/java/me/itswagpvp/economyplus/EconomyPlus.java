package me.itswagpvp.economyplus;

import me.itswagpvp.economyplus.commands.Bank;
import me.itswagpvp.economyplus.listeners.PlayerHandler;
import me.itswagpvp.economyplus.misc.InterestsManager;
import me.itswagpvp.economyplus.commands.*;
import me.itswagpvp.economyplus.database.misc.DatabaseType;
import me.itswagpvp.economyplus.database.misc.StorageMode;
import me.itswagpvp.economyplus.database.mysql.MySQL;
import me.itswagpvp.economyplus.database.sqlite.SQLite;
import me.itswagpvp.economyplus.hooks.PlaceholderAPI;
import me.itswagpvp.economyplus.hooks.holograms.HolographicDisplays;
import me.itswagpvp.economyplus.metrics.bStats;
import me.itswagpvp.economyplus.misc.*;
import me.itswagpvp.economyplus.hooks.vault.VEconomy;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

import static me.itswagpvp.economyplus.managers.ConfigManager.configManager;
import static me.itswagpvp.economyplus.utils.Config.config;
import static me.itswagpvp.economyplus.utils.Utils.utils;

public class EconomyPlus extends JavaPlugin {

    public static EconomyPlus plugin;

    public boolean basicperms;
    public boolean updater;
    public boolean debug;
    public boolean bank;

    @Override
    public void onEnable() {

        plugin = this;

        long delay = System.currentTimeMillis();

        configManager.load(); // sets up the config file

        // initialize plugin vars
        basicperms = config.getBoolean("require-basic-permissions", true);
        updater = config.getBoolean("updater.use", true);
        debug = config.getBoolean("debug", false);
        bank = config.getBoolean("bank.use", true);

        //

        PLUGIN_VERSION = Double.parseDouble(getDescription().getVersion());
        CONFIG_VERSION = getConfig().getDouble("Version", PLUGIN_VERSION);

        new StorageManager().createStorageConfig();

        if (!setupEconomy()) {
            vaultError("Can't find Vault!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        loadDatabase();

        loadEconomy();

        if (dbType == DatabaseType.UNDEFINED) {
            Bukkit.getConsoleSender().sendMessage("§c[EconomyPlus] Unable to start the plugin without a valid database option!");
            getServer().getPluginManager().disablePlugin(this);
        }

        //

        loadPlaceholderAPI();

        utils.log("§8+------------------------------------+");
        utils.log("             §dEconomy§5Plus");
        utils.log("             §aEnabled §d" + PLUGIN_VERSION);
        utils.log("§8");
        utils.log("§f-> §cLoading core:");
        utils.log("   - §fStorage-Mode: §a" + storageMode.toString());
        utils.log("   - §fDatabase: §bLoaded (" + dbType.toString().replace("H2", "SQLite") + ")");

        enableDatabase();

        utils.log("   - §fVault: " + vault);

        registerCommands();

        loadMessages();

        loadMetrics();

        sanityCheck();

        loadEvents();

        Bukkit.getConsoleSender().sendMessage("§8");

        boolean PlaceholderAPI = config.getBoolean("Hooks.PlaceholderAPI", true);
        boolean HolographicDisplays = config.getBoolean("Hooks.HolographicDisplays", true);

        if (PlaceholderAPI || HolographicDisplays) { // If atleast one of the plugins (to hook into) is set to true in config

            Bukkit.getConsoleSender().sendMessage("§f-> §cLoading hooks:");

            if (PlaceholderAPI) Bukkit.getConsoleSender().sendMessage("   - §fPlaceholderAPI: " + placeholder);
            if (HolographicDisplays) loadHolograms();

            Bukkit.getConsoleSender().sendMessage("§f");

        }

        if (configUpdate != null) { // config was recently updated
            String[] configUpdateSplit = configUpdate.split("\n");
            for (String configmsg : configUpdateSplit) {
                Bukkit.getConsoleSender().sendMessage(configmsg);
            }
            Bukkit.getConsoleSender().sendMessage("");
            configUpdate = null;
        }

        if (CONFIG_VERSION != PLUGIN_VERSION) { /* Config is not updated */

            int outdated; /* Amount of version the plugin is outdated or over updatedby */

            Bukkit.getConsoleSender().sendMessage("§f-> §eYour config.yml is outdated!");

            if (CONFIG_VERSION > PLUGIN_VERSION) { //ahead versions (could auto fix maybe but prob not?)
                outdated = Integer.parseInt(String.valueOf(Math.round((CONFIG_VERSION - PLUGIN_VERSION) / 0.1)).replace(".0", ""));
                Bukkit.getConsoleSender().sendMessage("   - §fConfig: " + "§c" + CONFIG_VERSION + " (" + outdated + " versions ahead" + ")");
            } else { //behind versions (outdated)
                outdated = Integer.parseInt(String.valueOf(Math.round((PLUGIN_VERSION - CONFIG_VERSION) / 0.1)).replace(".0", ""));
                Bukkit.getConsoleSender().sendMessage("   - §fConfig: " + "§c" + CONFIG_VERSION + " (" + outdated + " versions behind" + ")");
            }

            Bukkit.getConsoleSender().sendMessage("   - §fPlugin: " + "§d" + getDescription().getVersion());
            Bukkit.getConsoleSender().sendMessage("");

        }

        Updater.check();

        // If Bank and Interest is enabled start the interest timer
        if (config.getBoolean("bank.use", true) && config.getBoolean("bank.interest.use", true)) {
            new InterestsManager().startBankInterests();
        }

        Bukkit.getConsoleSender().sendMessage("§8+---------------[§a " + (System.currentTimeMillis() - delay) + "ms §8]-------------+");

        if (PLUGIN_VERSION >= Updater.getLatestGitVersion()) {
            Bukkit.getConsoleSender().sendMessage("[EconomyPlus] You are up to date! §d(v" + PLUGIN_VERSION + ")");
        }

    }

    @Override
    public void onDisable() {

        utils.log("§8+------------------------------------+");
        utils.log("             §dEconomy§5Plus");
        utils.log("              §cDisabling\n");
        utils.log("§f-> §cStopping threads...");
        utils.log("§f-> §cClosing database connection");

        try {
            dbType.close();
        } catch (SQLException e) {
            utils.log("§c-> ERROR: " + e.getMessage());
            if (debug) {
                e.printStackTrace();
            }
        }

        utils.log("§8+------------------------------------+");

    }

    // Hook into VaultEconomy
    private void loadEconomy() {

        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
            getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, new VEconomy(this), this, ServicePriority.Normal);
        } catch (ClassNotFoundException e) {
            vault = "§CError\n" + e.getMessage();
            return;
        }

        vault = "§6Hooked";

    }

    private void loadDatabase() {

        // Select how the plugin needs to storage the player datas
        if (getConfig().getString("Database.Mode", "UUID").equalsIgnoreCase("UUID")) {
            storageMode = StorageMode.UUID;
        } else if (getConfig().getString("Database.Mode", "UUID").equalsIgnoreCase("NICKNAME")) {
            storageMode = StorageMode.NICKNAME;
        } else {
            storageMode = StorageMode.UUID;
        }

        String type = getConfig().getString("Database.Type");
        if (type == null) {
            type = "H2";
        }

        // Detect and set the type of database
        if (type.equalsIgnoreCase("MySQL")) {

            try {
                new MySQL().connect();
                new MySQL().createTable();
                new MySQL().updateTable();
            } catch (Exception e) {
                utils.log("§cDatabase: " + e.getMessage() + " (MySQL ERROR)");
                if (debug) {
                    e.printStackTrace();
                }
                return;
            }

            dbType = DatabaseType.MySQL;

        } else if (type.equalsIgnoreCase("H2")) {

            try {
                new SQLite().load();
            } catch (Exception e) {
                utils.log("§cDatabase: " + e.getMessage() + " (H2 ERROR)");
                if (debug) {
                    e.printStackTrace();
                }
                return;
            }

            dbType = DatabaseType.H2;

        } else if (type.equalsIgnoreCase("YAML")) {

            try {
                createYMLStorage();
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("§cDatabase: " + e.getMessage() + " (YAML ERROR)");
                if (debug) {
                    e.printStackTrace();
                }
                return;
            }

            dbType = DatabaseType.YAML;

        }

        else {
            dbType = DatabaseType.UNDEFINED;
        }

    }

    private void enableDatabase() {
        // Load the cache for the database - Vault API
        //new CacheManager().cacheDatabase();
        //new CacheManager().startAutoSave();
        //Bukkit.getConsoleSender().sendMessage("     - §fCaching accounts...");
    }

    private void loadEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerHandler(), this);
    }

    private void registerCommands() {

        registerCommand("baltop", new BalanceTop(), new TabCompleterLoader());
        registerCommand("economyplus", new Main(), new TabCompleterLoader());
        registerCommand("bal", new Balance(), new TabCompleterLoader());
        registerCommand("pay", new Pay(), new TabCompleterLoader());
        registerCommand("eco", new Eco(), new TabCompleterLoader());
        registerCommand("paytoggle", new PayToggle(), new TabCompleterLoader());

        if (bank) { // if bank is enabled
            registerCommand("bank", new Bank(), new TabCompleterLoader());
        }

        utils.log("   - §fCommands: §aLoaded");

    }

    private void registerCommand(String label, CommandExecutor executor, TabCompleter... tabCompleter) {

        PluginCommand command = getCommand(label);
        if (command != null) {
            command.setExecutor(executor);
            if (tabCompleter[0] != null) {
                command.setTabCompleter(tabCompleter[0]);
            }
        } else {
            utils.log("&cError while loading command /" + label + "!");
        }

    }

    // Loads the bStats metrics
    private void loadMetrics() {

        if (!config.getBoolean("Metrics", true)) return;

        try {
            new bStats(this, 11565);
        } catch (Exception e) {
            utils.log("   - §cError loading bStats: " + e.getMessage());
            if (debug) {
                e.printStackTrace();
            }
        }
    }

    private static String placeholder;

    private void loadPlaceholderAPI() {

        if (!config.getBoolean("Hooks.PlaceholderAPI", true)) return;

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            placeholder = "§cCan't find the jar!";
            return;
        }

        try {
            new PlaceholderAPI(plugin).register();
        } catch (Exception e) {
            placeholder = "§cError!\n" + e.getMessage();
        } finally {
            placeholder = "§aHooked!";
        }

    }

    private void loadHolograms() {

        if (!config.getBoolean("Hooks.HolographicDisplays", true)) return;

        if (getServer().getPluginManager().getPlugin("HolographicDisplays") == null) {
            utils.log("   - §fHolographicDisplays: §cCan't find the jar!");
            return;
        }

        try {

            if (new StorageManager().getStorageConfig().getString("Hologram.BalTop.World") != null) {

                utils.log("   - §fHolographicDisplays: §aHooked!");

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> new HolographicDisplays().createHologram(), 1);

                return;
            }

        } catch (Exception e) {
            utils.log("   - §fHolographicDisplays: §c" + e.getMessage() + " (ERROR)");
            if (debug) {
                e.printStackTrace();
            }
            return;

        }

        utils.log("   - §fHolographicDisplays: §aHooked!");

    }

    private void loadMessages() {

        messages.load();

        String messages = getConfig().getString("Language");
        if (!(Messages.getMessageConfig(messages.toUpperCase()) == null)) {
            lang = messages.toUpperCase();
            Bukkit.getConsoleSender().sendMessage("   - §fMessages: §a" + lang);
        } else {
            Bukkit.getConsoleSender().sendMessage("   - §fMessages: §cInvalid file! (" + messages + "), using EN");
        }

    }

    public BalTopManager getBalTopManager() {
        return new BalTopManager();
    }

    // Controls if there's Vault installed
    private boolean setupEconomy() {
        return getServer().getPluginManager().getPlugin("Vault") != null;
    }

    private void sanityCheck() {
        if (getServer().getPluginManager().isPluginEnabled("Vault")) return;

        vaultError("Vault is not enabled.");
        getServer().getPluginManager().disablePlugin(this);
    }

    private void vaultError(String specific) {
        Bukkit.getConsoleSender().sendMessage("§8+------------------------------------+");
        Bukkit.getConsoleSender().sendMessage("             §dEconomy§5Plus");
        Bukkit.getConsoleSender().sendMessage("              §cDisabling");
        Bukkit.getConsoleSender().sendMessage("§8");
        Bukkit.getConsoleSender().sendMessage("§f-> §c" + specific);
        Bukkit.getConsoleSender().sendMessage("§8+------------------------------------+");
    }

}