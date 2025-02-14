package me.BetterAntiSwear;

import java.io.*;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mkremins.fanciful.FancyMessage;
import n.Z3Z.m.HoloGramAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class BetterAntiSwear extends JavaPlugin implements Listener {
    ArrayList<String> globalMute = new ArrayList<>();
    ConfigManager man = new ConfigManager();
    private PluginDescriptionFile pdfFile;
    public static Economy econ = null;
    
    int swears;
    Date now = new Date();
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    
    public static FileConfiguration config;
    
    public void logToFile(String message) {
        try {
            File dataFolder = getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            
            File saveTo = new File(getDataFolder(), "log.txt");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }
            
            FileWriter fw = new FileWriter(saveTo, true);
            
            PrintWriter pw = new PrintWriter(fw);
            
            pw.println(message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = (Economy)rsp.getProvider();
        return (econ != null);
    }
    
    public boolean updateAvailable() {
        double currentVersion = Double.parseDouble(this.pdfFile.getVersion());
        double latestVersion = latestVersion();
        if (currentVersion < latestVersion) {
            return true;
        }
        return false;
    }

    public double latestVersion() {
        double version = 0;
        try {
            URLConnection connection = new URL("https://api.github.com/repos/Acher0ns/BetterAntiSwear/releases/latest").openConnection();
            connection.connect();
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader(connection.getInputStream()));
            JsonObject rootobj = root.getAsJsonObject();
            version = rootobj.get("tag_name").getAsDouble();
        } catch (Exception e) {}
        return version;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent er) {
        Player p = er.getPlayer();
        if (p.hasPermission("antiswear.manage")) {
            if (updateAvailable()) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            String.valueOf(getConfig().getString("prefix")) + " &cThis server is running an older version (" + 
                            this.pdfFile.getVersion() + 
                            ") of BetterAntiswear. The newest version available is " + latestVersion() + ". Only people with the permission" + 
                            " &e&oantiswear.manage&r&c can see this message."));
                (new FancyMessage("Click here to go to the download page.")).color(ChatColor.GREEN)
                    .link("https://github.com/Acher0ns/BetterAntiSwear/releases/latest")
                    .tooltip("BetterAntiSwear download page").send(p);
            } 
        }

        FileConfiguration data = this.man.getConfig("data", (Plugin)this);
        UUID uuid = p.getUniqueId();
        String Suuid = uuid.toString();
        if (!data.contains(Suuid)) {
            data.set(Suuid, 0);
            try {
                this.man.saveConfig(data, "data", (Plugin)this);
                this.man.reloadConfig("data", (Plugin)this);
            } catch (IOException e) {
                if (getConfig().getBoolean("debug")) {
                    e.printStackTrace();
                }
                getLogger().warning("Something went wrong while setting Plan data.");
            }
            return;
        } 
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChatEvent(AsyncPlayerChatEvent e) {
        if (this.globalMute.contains("true")) {
            Player p = e.getPlayer();
            if (p.hasPermission("antiswear.bypass"))
                return; 
            e.setCancelled(true);
            
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + " " + 
                        getConfig().getString("globalmute").replace("%player%", e.getPlayer().getName())));
        } 
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void PlayerCommand(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasPermission("antiswear.bypass")) {
            return;
        }

        String nonLeet = replaceLeet(ChatColor.stripColor(replaceIgnoredWords(e.getMessage().replace("/", "").replaceAll("&", "§"))));
        String message = replaceAllSpecialCharWithSpace(nonLeet);

        if (checkIfSwear(message)) {
            e.setCancelled(true);
            onSwear(e.getPlayer(), e.getMessage(), detectedWords(message));
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().hasPermission("antiswear.bypass")) {
            return;
        }

        String nonLeet = replaceLeet(ChatColor.stripColor(replaceIgnoredWords(e.getMessage().replace("/", "").replaceAll("&", "§"))));
        String message = replaceAllSpecialCharWithSpace(nonLeet);

        if (checkIfSwear(message)) {
            e.setCancelled(true);
            onSwear(e.getPlayer(), e.getMessage(), detectedWords(message));
        }
    }

    public boolean checkIfSwear(String message) {
        // Check for blocked phrases
        if (getConfig().getStringList("blacklist").stream().map(word -> word.toLowerCase()).anyMatch(message.toLowerCase()::contains)) {
            return true;
        }
        return false;
    }

    public List<String> detectedWords(String message) {
        List<String> swears = new ArrayList<>();
        getConfig().getStringList("blacklist").stream().map(word -> word.toLowerCase()).forEach(word -> {
            if (message.toLowerCase().contains(word)) {
                swears.add(word);
            }
        });
        return swears;
    }

    public String replaceLeet(String string) {
        HashMap<String, String> leetDict = new HashMap<>();
        leetDict.put("$", "s");
        leetDict.put("(", "c");
        leetDict.put("5", "s");
        leetDict.put("@", "a");
        leetDict.put("4", "a");
        leetDict.put("3", "e");
        leetDict.put("+", "t");
        leetDict.put("#", "h");
        leetDict.put("0", "o");
        leetDict.put("|\\|", "n");
        leetDict.put("/\\", "a");
        leetDict.put("/-\\", "a");
        leetDict.put("^", "a");
        leetDict.put("\\/", "v");
        leetDict.put("8", "b");
        leetDict.put("|_|", "u");
        leetDict.put("|-|", "h");
        leetDict.put("Я", "r");
        leetDict.put("vv", "w");
        leetDict.put("|<", "k");
        leetDict.put("[)", "d");
        leetDict.put("|)", "d");
        leetDict.put("><", "x");
        leetDict.put("1", "i");
        leetDict.put("!", "i");
        leetDict.put("7", "t");

        string = string.replace("\\/\\/", "w");
        for (String key : leetDict.keySet()) {
            string = string.replace(key, leetDict.get(key));
        }
        return string;
    }

    public String replaceAllSpecialCharWithSpace(String string) {
        return string.replaceAll("[^a-zA-Z0-9\s]+", "");
    }

    public String replaceIgnoredWords(String string) {
        for (String ignoredWord : getConfig().getStringList("whitelist")) {
            string = string.replace(ignoredWord, " ");
        }
        return string;
    }

    public void onSwear(final Player p, String message, List<String> detectedStrings) {
        this.swears++;
        if (getConfig().getBoolean("log")) {
            logToFile("[" + this.format.format(this.now) + "] " + p.getName() + ": " + message);
        }
        
        incrementSwearCount(p);
        if (getConfig().getBoolean("kick")) {
            if (getSwearCount(p.getUniqueId()) % getConfig().getInt("times") == 0) {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> {
                    p.kickPlayer(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + BetterAntiSwear.config.getString("kickmessage").replaceAll("%player%", p.getName())));
                });
            } 
            return;
        } 
        
        if (getConfig().getString("reportmessage") == null) {
            Bukkit.broadcast(
                ChatColor.translateAlternateColorCodes('&', 
                String.valueOf(getConfig().getString("prefix")) + ChatColor.RED + " %player% tried saying '" + 
                ChatColor.DARK_RED + message + ChatColor.RED + "'.")
                .replaceAll("%player%", p.getName()), "antiswear.mod"
            );
        } else {
            Bukkit.broadcast(
                ChatColor.translateAlternateColorCodes('&',
                String.valueOf(getConfig().getString("prefix")) + " " + getConfig().getString("reportmessage"))
                .replaceAll("%player%", p.getName())
                .replaceAll("%message%", message), "antiswear.mod");
        } 

        Bukkit.broadcast(
            ChatColor.translateAlternateColorCodes('&', 
            String.valueOf(getConfig().getString("prefix")) + ChatColor.RED + " Detected: " + 
            ChatColor.DARK_RED + Arrays.toString(detectedStrings.toArray()).replace("[", "").replace("]", "") + ChatColor.RED + "."),
            "antiswear.mod"
        );
        
        if (p.isOnline()) {
            if (!getConfig().getString("command").equalsIgnoreCase("none")) {
                Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), 
                    getConfig().getString("command").replaceAll("%player%", p.getName()));
            }

            if (getConfig().getBoolean("damagetoggle")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> {
                    double damageAmount = Double.parseDouble(BetterAntiSwear.config.getString("damage"));
                    p.damage(damageAmount);
                }, 
                1L);
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> {
                Location loc = p.getLocation().add(p.getLocation().getDirection().normalize().multiply(2.6D));
                Random rand = new Random();
                int random = rand.nextInt(10000);

                if (BetterAntiSwear.this.getServer().getPluginManager().getPlugin("HoloGramAPI") != null)
                {
                    HoloGramAPI.createTempHoloGram(loc, BetterAntiSwear.this.getConfig().getString("holomessage").replaceAll("%player%", p.getName()), Integer.valueOf(random), Integer.valueOf(BetterAntiSwear.this.getConfig().getInt("holotime")), false, false, false, null, null, null);
                }
            });
            
            if (config.getBoolean("actionbar")) {
                ActionBar actionBar = new ActionBar();
                actionBar.setMessage(
                    ChatColor.translateAlternateColorCodes('&', 
                    String.valueOf(getConfig().getString("prefix")) + " " + 
                    getConfig().getString("actionbarmessage"))
                    .replace("%player%", p.getName()));
                actionBar.send(new Player[] { p });
            } 

            
            if (getConfig().getBoolean("sound")) {
                p.playSound(p.getLocation(), 
                    Sound.valueOf(getConfig().getString("soundvalue").toUpperCase()), 1.0F, 1.0F);
            }

            
            if (getConfig().getBoolean("balance") && 
                getServer().getPluginManager().getPlugin("Vault") != null) {
                int amou = getConfig().getInt("balamount");
                EconomyResponse r = econ.withdrawPlayer((OfflinePlayer)p, amou);
                
                if (r.transactionSuccess()) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                        String.valueOf(getConfig().getString("prefix")) + " " + 
                        getConfig().getString("chargemessage")
                        .replaceAll("%player%", p.getName())
                        .replaceAll("%amount%", (new StringBuilder(String.valueOf(amou))).toString())));
                }
            } 

            if (getConfig().getBoolean("chatmessage")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("%s %s", new Object[] {
                    getConfig().getString("prefix"), 
                    getConfig().getString("message").replaceAll("%player%", p.getName())
                })));
            }
        }
    }

    public void onEnable() {
        try {
            this.man.createConfig("data", (Plugin)this);
        } catch (IOException e) {
            if (getConfig().getBoolean("debug")) {
                e.printStackTrace();
            }
            getLogger().warning("Something went wrong while generating a data file.");
        } 

        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                saveDefaultConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 

        this.pdfFile = getDescription();
        double localversion = Double.parseDouble(this.pdfFile.getVersion());
        if (updateAvailable()) {
            getLogger().info("There is an update available. This server is running BetterAntiswear version " + localversion + ", but version " + latestVersion() + " is available.");
        }

        if (getServer().getPluginManager().getPlugin("HoloGramAPI") != null) {
            Bukkit.getConsoleSender()
                .sendMessage(ChatColor.GOLD + "[BetterAntiSwear] " + ChatColor.GREEN + "Hooked into HoloGramAPI.");
        }
        if (this.globalMute.contains("true")) {
            this.globalMute.clear();
        }
        this.globalMute.add("false");

        getLogger()
            .info(String.valueOf(this.pdfFile.getName()) + " Version: " + this.pdfFile.getVersion() + " is enabled!");
        config = getConfig();
        
        Bukkit.getServer().getPluginManager().registerEvents(this, (Plugin)this);

        if (!setupEconomy()) {
            getLogger()
                .warning(String.format("Economy module deactivated - Vault not found", new Object[] { getDescription().getName() }));
            return;
        } 
    }

    public void onDisable() {
        this.pdfFile = getDescription();
        getLogger().info(String.valueOf(this.pdfFile.getName()) + " is now disabled!");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("antiswear")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            "&6]&l&m--------------&r&6[&c&lBetterAntiSwear&6]&l&m--------------&r&6["));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l- &eAuthors: &a&lBrooky1010, Neonix#1337"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                sender.sendMessage(
                        ChatColor.translateAlternateColorCodes('&', "&7&l- &eVersion:&7 " + this.pdfFile.getVersion()));
                if (getServer().getPluginManager().getPlugin("HoloGramAPI") != null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l- &eHologramAPI:&a Active"));
                } else {
                    sender.sendMessage(
                            ChatColor.translateAlternateColorCodes('&', "&7&l- &eHologramAPI:&c Not installed"));
                } 
                sender.sendMessage(
                        ChatColor.translateAlternateColorCodes('&', "&7&l- &e/as help&7 for a list of commands"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            "&6]&l&m--------------&r&6[&c&lBetterAntiSwear&6]&l&m--------------&r&6["));
                return true;
            } 
            
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("antiswear.reload")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                reloadConfig();
                this.man.reloadConfig("data", (Plugin)this);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.RED + getConfig().getString("reload_message")));
            } else if (args[0].equalsIgnoreCase("help")) {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m--------------&r&6[&c&lBetterAntiSwear&6]&l&m--------------&r&6["));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as message &7              Change the chat message."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as prefix &7                 Change chat prefix."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as kickmessage &7         Change kick message."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as add &7                    Add a word to a config list."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as remove &7               Remove a word from config list."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as help 2 &7                 Help menu 2."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m-------------------&r&6[&c&lHelp 1&6]&l&m-------------------&r&6["));
                    return true;
                } 
                
                if (args[1].equalsIgnoreCase("1")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m--------------&r&6[&c&lBetterAntiSwear&6]&l&m--------------&r&6["));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as message &7              Change the chat message."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as prefix &7                 Change chat prefix."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as kickmessage &7         Change kick message."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as add &7                    Add a word to a config list."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as remove &7               Remove a word from config list."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as help 2 &7                 Help menu 2."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m-------------------&r&6[&c&lHelp 1&6]&l&m-------------------&r&6["));
                    return true;
                }

                if (args[1].equalsIgnoreCase("2")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m--------------&r&6[&c&lBetterAntiSwear&6]&l&m--------------&r&6["));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as info &7                   Show BetterAntiSwear info."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as toggle &7                Toggle globalmute."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as cc &7                     Clear the entire chat."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as debug &7                 Send debug output to console."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as check &7                 Check for updates."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as count &7                 Check how many times a player sweared."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m-------------------&r&6[&c&lHelp 2&6]&l&m-------------------&r&6["));
                    return true;
                } 
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            String.valueOf(getConfig().getString("prefix")) + " &cPlease enter 1 or 2."));
            } else if (args[0].equalsIgnoreCase("add")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                }

                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as add <whitelist/blacklist> <word>"));
                    return true;
                }

                String listName;
                if (args[1].equalsIgnoreCase("blacklist")) {
                    listName = "blacklist";
                } else if (args[1].equalsIgnoreCase("whitelist")) {
                    listName = "whitelist";
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as add <whitelist/blacklist> <word>"));
                    return true;
                }
                
                if (args.length == 2) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as add <whitelist/blacklist> <word>"));
                    return true;
                }

                String wordsToAdd = "";
                if (args[2].startsWith("\"") || args[2].startsWith("'")) {
                    for (int i = 2; i < args.length; i++) {
                        wordsToAdd += i == args.length - 1 ? args[i] : args[i] + " ";
                    }
                    wordsToAdd = wordsToAdd.replace("'", "").replace("\"", "").toLowerCase();
                } else {
                    wordsToAdd = args[2].toLowerCase();
                }

                if (getConfig().getStringList(listName).contains(wordsToAdd)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "'" + wordsToAdd+ "' is already on the " + listName + "!"));
                    return true;
                } 

                List<String> list = getConfig().getStringList(listName);
                list.add(wordsToAdd);
                getConfig().set(listName, list);
                saveConfig();
                reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.GREEN + "Word added successfully to the " + listName + "!"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.RED + getConfig().getString("reload_message")));
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 

                String listName;
                if (args[1].equalsIgnoreCase("blacklist")) {
                    listName = "blacklist";
                } else if (args[1].equalsIgnoreCase("whitelist")) {
                    listName = "whitelist";
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as remove <whitelist/blacklist> <word>"));
                    return true;
                }
                
                if (args.length == 2) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as remove <whitelist/blacklist> <word>"));
                    return true;
                }

                String wordsToRemove = "";
                if (args[2].startsWith("\"") || args[2].startsWith("'")) {
                    for (int i = 2; i < args.length; i++) {
                        wordsToRemove += i == args.length - 1 ? args[i] : args[i] + " ";
                    }
                    wordsToRemove = wordsToRemove.replace("'", "").replace("\"", "").toLowerCase();
                } else {
                    wordsToRemove = args[2].toLowerCase();
                }

                if (!getConfig().getStringList(listName).contains(wordsToRemove)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.RED + "'" + wordsToRemove + "' not found."));
                    return true;
                } 

                List<String> list = getConfig().getStringList(listName);
                while (list.contains(wordsToRemove)) {
                    list.remove(wordsToRemove);
                }
                getConfig().set(listName, list);
                saveConfig();
                reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.GREEN + "Word removed successfully from the " + listName + "!"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.RED + getConfig().getString("reload_message")));
            } else if (args[0].equalsIgnoreCase("info")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 

                String blacklist = getConfig().getStringList("words").toString().replace("[", "").replace("]", "");
                String whitelist = getConfig().getStringList("words").toString().replace("[", "").replace("]", "");
                
                sender.sendMessage(
                        ChatColor.RED + "------[ " + ChatColor.GRAY + "BetterAntiSwear Info" + ChatColor.RED + " ]------");
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.GRAY + "Prefix: " + 
                        ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix")));
                sender.sendMessage(ChatColor.GRAY + "Swear Message: " + 
                        ChatColor.translateAlternateColorCodes('&', getConfig().getString("message")));
                sender.sendMessage(
                        ChatColor.GRAY + "Globalmute: " + ChatColor.GOLD + getConfig().getBoolean("mute_status"));
                sender.sendMessage(ChatColor.GRAY + "Kick Message: " + 
                        ChatColor.translateAlternateColorCodes('&', getConfig().getString("kickmessage")));
                sender.sendMessage(ChatColor.GRAY + "Blacklist: " + ChatColor.RED + blacklist);
                sender.sendMessage(ChatColor.GRAY + "Whitelist: " + ChatColor.WHITE + whitelist);
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.RED + "--------------------------");
            } else if (args[0].equalsIgnoreCase("check")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 

                if (!updateAvailable()) {
                    sender.sendMessage(ChatColor.RED + "------[ " + ChatColor.GRAY + "BetterAntiSwear Update" + ChatColor.RED + " ]------"); 
                    sender.sendMessage(" ");
                    sender.sendMessage(ChatColor.GREEN + "No update available.");
                    sender.sendMessage(ChatColor.GREEN + "Contact Neonix#1337 for suggestions");
                    sender.sendMessage(" ");
                    sender.sendMessage(ChatColor.RED + "---------------------------------");
                } else {
                    sender.sendMessage(ChatColor.RED + "------[ " + ChatColor.GRAY + "BetterAntiSwear Update" + ChatColor.RED + " ]------"); 
                    sender.sendMessage(" ");
                    sender.sendMessage(ChatColor.GREEN + "Update available! " + this.pdfFile.getVersion() + " -> " + latestVersion());
                    (new FancyMessage("Click here to go to the download page.")).color(ChatColor.GREEN)
                    .link("https://github.com/Acher0ns/BetterAntiSwear/releases/latest")
                    .tooltip("BetterAntiSwear download page").send(sender);
                    sender.sendMessage(ChatColor.GREEN + "Contact Neonix#1337 for suggestions");
                    sender.sendMessage(" ");
                    sender.sendMessage(ChatColor.RED + "----------------------------------");
                }

            } else if (args[0].equalsIgnoreCase("debug")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.GREEN + "Sent output to console..."));
                getLogger().info("---------- BetterAntiswear Debug ----------");
                getLogger().info("BetterAntiswear version " + this.pdfFile.getVersion());
                getLogger().info("Server version " + Bukkit.getVersion());
                getLogger().info("Damage: " + getConfig().getBoolean("damagetoggle") + " | Amount: " + 
                        getConfig().getInt("damage"));
                getLogger().info("Sound: " + getConfig().getString("soundvalue"));
                getLogger().info("Balance: " + getConfig().getBoolean("balance"));

                if (getServer().getPluginManager().getPlugin("HoloGramAPI") != null) {
                    getLogger().info("HologramAPI: true");
                } else {
                    getLogger().info("HologramAPI: false");
                } 
                
                if (getServer().getPluginManager().getPlugin("Plan") != null) {
                    getLogger().info("Plan: true");
                } else {
                    getLogger().info("Plan: false");
                } 
                
                if (getServer().getPluginManager().getPlugin("Vault") != null) {
                    getLogger().info("Vault: true");
                } else {
                    getLogger().info("Vault: false");
                } 
                
                getLogger().info("----------- End of debug -----------");
            } else if (args[0].equalsIgnoreCase("prefix")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as set prefix <prefix>"));
                    return true;
                } 

                String message = "";
                for (int i = 1; i < args.length; i++) {
                    message = String.valueOf(message) + args[i] + " ";
                }
                message = message.trim();
                
                getConfig().set("prefix", message);
                saveConfig();
                reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.GREEN + "Prefix changed successfully."));
            } else if (args[0].equalsIgnoreCase("message")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as message <message>"));
                    return true;
                } 

                String message = "";
                for (int i = 1; i < args.length; i++) {
                    message = String.valueOf(message) + args[i] + " ";
                }
                message = message.trim();
                
                getConfig().set("message", message);
                saveConfig();
                reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.GREEN + "Message changed successfully."));
            } else if (args[0].equalsIgnoreCase("count")) {
                if (!sender.hasPermission("antiswear.mod")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as count <player>"));
                    return true;
                }
                
                OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
                if (op.hasPlayedBefore()) {
                    String str1 = WordUtils.capitalize(op.getName());
                    int count = getSwearCount(op.getUniqueId());
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.DARK_GREEN + str1 + " &acursed &e" + count + "&a times."));
                    return true;
                } 

                String opname = WordUtils.capitalize(op.getName());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.DARK_RED + opname + " &cnot found."));
            } else if (args[0].equalsIgnoreCase("kickmessage")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as kickmessage <message>"));
                    return true;
                } 

                String message = "";
                for (int i = 1; i < args.length; i++) {
                    message = String.valueOf(message) + args[i] + " ";
                }
                message = message.trim();
                
                getConfig().set("kickmessage", message);
                saveConfig();
                reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.GREEN + "Message changed successfully."));
            } else if (args[0].equalsIgnoreCase("cc")) {
                if (!sender.hasPermission("antiswear.mod")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 

                for (int i = 0; i < 125; i++) {
                    Bukkit.broadcastMessage("");
                }

                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + getConfig().getString("clearmessage").replaceAll("%player%", sender.getName())));
            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (!sender.hasPermission("antiswear.toggle")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                if (this.globalMute.contains("false")) {
                    this.globalMute.clear();
                    this.globalMute.add("true");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', 
                                String.valueOf(getConfig().getString("prefix")) + " " + getConfig().getString("mute_toggle")
                                .replaceAll("%toggle%", getConfig().getString("mute"))));
                } else if (this.globalMute.contains("true")) {
                    this.globalMute.clear();
                    this.globalMute.add("false");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', 
                                String.valueOf(getConfig().getString("prefix")) + " " + getConfig().getString("mute_toggle")
                                .replaceAll("%toggle%", getConfig().getString("unmute"))));
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.RED + "Invalid subcommand." + 
                            ChatColor.GRAY + " Try /as for the command list."));
            } 
        } 
        return true;
    }
    
    public int getSwearCount(UUID uuid) {
        FileConfiguration data = this.man.getConfig("data", (Plugin)this);
        String Suuid = uuid.toString();
        if (!data.contains(Suuid)) {
            data.set(Suuid, Integer.valueOf(0));
            try {
                this.man.saveConfig(data, "data", (Plugin)this);
                this.man.reloadConfig("data", (Plugin)this);
            } catch (IOException e) {
                if (getConfig().getBoolean("debug")) {
                    e.printStackTrace();
                }
                getLogger().warning("Something went wrong while setting Plan data.");
            } 
        } 
        int count = data.getInt(Suuid);
        return count;
    }

    public void incrementSwearCount(final Player player) {
        FileConfiguration data = this.man.getConfig("data", (Plugin)this);
        UUID uuid = player.getUniqueId();
        String Suuid = uuid.toString();
        if (!data.contains(Suuid)) {
            data.set(Suuid, 0);
        }
        data.set(Suuid, getSwearCount(uuid) + 1);

        try {
            this.man.saveConfig(data, "data", (Plugin)this);
            this.man.reloadConfig("data", (Plugin)this);
        } catch (IOException e) {
            if (getConfig().getBoolean("debug")) {
                e.printStackTrace();
            }
            getLogger().warning("Something went wrong while setting Plan data.");
        }
    }
}
