package me.BetterAntiSwear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
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















public class AntiSwear
    extends JavaPlugin
    implements Listener
{
    HashMap<String, Integer> swearCount = new HashMap<>();
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
        }
        catch (IOException e) {
            
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
    
    public double updatecheck() {
        double ver = 0.0D;
        try {
            HttpURLConnection con = (HttpURLConnection)(new URL("https://api.spigotmc.org/legacy/update.php?resource=16354"))
                .openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            String version = (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();
            if (version.length() <= 7)
            {
                ver = Double.parseDouble(version);
            }

            
            con.disconnect();
        
        }
        catch (Exception ex) {
            getLogger().info("Failed to check for an update on Spigot.");
        } 
        return ver;
    }



    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent er) {
        Player p = er.getPlayer();
        if (p.hasPermission("antiswear.manage")) {
            
            double version = updatecheck();
            double localversion = Double.parseDouble(this.pdfFile.getVersion());
            if (localversion < version) {
                
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            String.valueOf(getConfig().getString("prefix")) + " &cThis server is running an older version (" + 
                            this.pdfFile.getVersion() + 
                            ") of Advanced Antiswear. The newest version available is " + version + ". Only people with the permission" + 
                            " &e&oantiswear.manage&r&c can see this message."));
                (new FancyMessage("Click here to go to the download page.")).color(ChatColor.GREEN)
                    .link("https://www.spigotmc.org/resources/advanced-antiswear.16354/")
                    .tooltip("Advanced AntiSwear download page").send(p);
            } 
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
        
        String me1 = e.getMessage().replace("/", "");
        String message = ChatColor.stripColor(me1.replaceAll("&", "§"));

        
        if (message.startsWith("as") || message.startsWith("antiswear") || message.startsWith("asw")) {
            return;
        }

        
        for (String word : getConfig().getStringList("words")) {
            byte b; int i; String[] arrayOfString; for (i = (arrayOfString = e.getMessage().split(" ")).length, b = 0; b < i; ) { String message1 = arrayOfString[b];
                
                StringBuilder builder = new StringBuilder(); byte b1; int j; char[] arrayOfChar;
                for (j = (arrayOfChar = message1.toCharArray()).length, b1 = 0; b1 < j; ) { char character = arrayOfChar[b1];
                    if ((character >= '0' && character <= '9') || (character >= 'A' && character <= 'Z') || (
                        character >= 'a' && character <= 'z'))
                        builder.append(character); 
                    b1++; }
                
                String filtered = builder.toString();
                
                if (filtered.toLowerCase().equalsIgnoreCase(word.toLowerCase()))
                {
                    e.setCancelled(true);
                }
                
                b++; }
        
        } 
        if (e.isCancelled())
        {
            onSwear(e.getPlayer(), e.getMessage());
        }
    }





    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().hasPermission("antiswear.bypass")) {
            return;
        }
        
        String me2 = e.getMessage().replace("/", "");
        String messagens = ChatColor.stripColor(me2.replaceAll("&", "§"));
        
        for (String word : getConfig().getStringList("words")) {
            byte b; int i; String[] arrayOfString; for (i = (arrayOfString = messagens.split(" ")).length, b = 0; b < i; ) { String message = arrayOfString[b];
                
                StringBuilder builder = new StringBuilder(); byte b1; int j; char[] arrayOfChar;
                for (j = (arrayOfChar = message.toCharArray()).length, b1 = 0; b1 < j; ) { char character = arrayOfChar[b1];
                    if ((character >= '0' && character <= '9') || (character >= 'A' && character <= 'Z') || (
                        character >= 'a' && character <= 'z')) {
                        builder.append(character);
                    }
                    b1++; }
                
                String filtered = builder.toString();
                
                if (filtered.toLowerCase().equalsIgnoreCase(word.toLowerCase()))
                {
                    
                    e.setCancelled(true);
                }

                
                b++; }
        
        } 
        
        if (e.isCancelled())
        {
            onSwear(e.getPlayer(), e.getMessage());
        }
    }


    
    public void onSwear(final Player p, String message) {
        this.swears++;
        
        if (getConfig().getBoolean("log")) {
            logToFile("[" + this.format.format(this.now) + "] " + p.getName() + ": " + message);
        }
        
        if (!this.swearCount.containsKey(p.getName())) {
            this.swearCount.put(p.getName(), Integer.valueOf(1));
        } else {
            
            this.swearCount.put(p.getName(), Integer.valueOf(((Integer)this.swearCount.get(p.getName())).intValue() + 1));
        } 
        
        if (getConfig().getBoolean("kick")) {
            if (((Integer)this.swearCount.get(p.getName())).intValue() >= getConfig().getInt("times")) {
                this.swearCount.put(p.getName(), Integer.valueOf(0));
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, new Runnable() {
                            public void run() {
                                p.kickPlayer(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + 
                                            AntiSwear.config.getString("kickmessage").replaceAll("%player%", p.getName())));
                            }
                        });
            } 

            
            return;
        } 
        
        if (getConfig().getString("reportmessage") == null) {
            Bukkit.broadcast(
                    ChatColor.translateAlternateColorCodes('&', 
                        String.valueOf(getConfig().getString("prefix")) + ChatColor.RED + " %player% tried saying '" + 
                        ChatColor.DARK_RED + message + ChatColor.RED + "'.")
                    .replaceAll("%player%", p.getName()), "antiswear.mod");
        } else {
            Bukkit.broadcast(
                    ChatColor.translateAlternateColorCodes('&', 
                        String.valueOf(getConfig().getString("prefix")) + " " + getConfig().getString("reportmessage"))
                    .replaceAll("%player%", p.getName()).replaceAll("%message%", message), "antiswear.mod");
        } 
        
        if (p.isOnline()) {
            
            if (!getConfig().getString("command").equalsIgnoreCase("none")) {
                Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), 
                        getConfig().getString("command").replaceAll("%player%", p.getName()));
            }

            
            if (getConfig().getBoolean("damagetoggle"))
            {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, new Runnable()
                        {
                            public void run() {
                                double damageAmount = Double.parseDouble(AntiSwear.config.getString("damage"));
                                p.damage(damageAmount);
                            }
                        }, 
                        1L);
            }

            
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, new Runnable()
                    {
                        public void run() {
                            Location loc = p.getLocation()
                                .add(p.getLocation().getDirection().normalize().multiply(2.6D));
                            
                            Random rand = new Random();
                            
                            int random = rand.nextInt(10000);


                            
                            if (AntiSwear.this.getServer().getPluginManager().getPlugin("HoloGramAPI") != null)
                            {
                                HoloGramAPI.createTempHoloGram(loc, AntiSwear.this.getConfig().getString("holomessage").replaceAll("%player%", p.getName()), Integer.valueOf(random), Integer.valueOf(AntiSwear.this.getConfig().getInt("holotime")), false, false, false, null, null, null);


                            
                            }
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
                
                if (r.transactionSuccess())
                {
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
        double version = updatecheck();
        double localversion = Double.parseDouble(this.pdfFile.getVersion());
        if (localversion < version) {
            getLogger().info("There is an update available. This server is running Antiswear version " + localversion + ", but version " + version + "is available.");
        }


        


        
        if (getServer().getPluginManager().getPlugin("HoloGramAPI") != null) {
            Bukkit.getConsoleSender()
                .sendMessage(ChatColor.GOLD + "[AntiSwear] " + ChatColor.GREEN + "Hooked into HoloGramAPI.");
        }
        if (this.globalMute.contains("true")) {
            this.globalMute.clear();
        }
        this.globalMute.add("false");

        
        getLogger()
            .info(String.valueOf(this.pdfFile.getName()) + " Version: " + this.pdfFile.getVersion() + " by brooky1010 is enabled!");
        config = getConfig();
        
        Bukkit.getServer().getPluginManager().registerEvents(this, (Plugin)this);


        
        if (!setupEconomy()) {
            getLogger()
                .severe(String.format("Economy module deactivated - Vault not found", new Object[] { getDescription().getName() }));
            return;
        } 
    }



    
    public void onDisable() {
        this.pdfFile = getDescription();
        getLogger().info(String.valueOf(this.pdfFile.getName()) + " by brooky1010 is now disabled!");
        getLogger().info("Website: themilkywalrus.com");
    }


    
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("antiswear")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            "&6]&l&m-------------&r&6[&c&lAdvanced AntiSwear&6]&l&m-------------&r&6["));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l- &eAuthor: &a&lBrooky1010"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l- &eTwitter: &a&l@TheMilkyWalrus"));
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
                            "&6]&l&m-------------&r&6[&c&lAdvanced AntiSwear&6]&l&m-------------&r&6["));
                return true;
            } 
            
            if (args[0].equalsIgnoreCase("reload")) {


                
                if (!sender.hasPermission("antiswear.reload")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                reloadConfig();
                
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.RED + getConfig().getString("reload_message")));
            }
            else if (args[0].equalsIgnoreCase("help")) {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m-------------&r&6[&c&lAdvanced AntiSwear&6]&l&m-------------&r&6["));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as message &7                     Change the chat message."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as prefix &7                            Change chat prefix."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as kickmessage &7        Change kick message."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as add &7                                    Add a swear word."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as remove &7                        Remove a swear word."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as help 2 &7                            Help menu 2."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m-------------------&r&6[&c&lHelp 1&6]&l&m-------------------&r&6["));
                    return true;
                } 
                
                if (args[1].equalsIgnoreCase("1")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m-------------&r&6[&c&lAdvanced AntiSwear&6]&l&m-------------&r&6["));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as message &7                     Change the chat message."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as prefix &7                            Change chat prefix."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as kickmessage &7        Change kick message."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as add &7                                    Add a swear word."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as remove &7                        Remove a swear word."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as help 2 &7                            Help menu 2."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m-------------------&r&6[&c&lHelp 1&6]&l&m-------------------&r&6["));
                    return true;
                } 
                if (args[1].equalsIgnoreCase("2")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m-------------&r&6[&c&lAdvanced AntiSwear&6]&l&m-------------&r&6["));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as info &7                                    Show Antiswear info."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as toggle &7                            Toggle globalmute."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as cc &7                                         Clear the entire chat."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as debug &7                             Send debug output to console."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as check &7                             Check for updates."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&7&l- &e/as count    &7                             Check how many times a player sweared."));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                "&6]&l&m-------------------&r&6[&c&lHelp 2&6]&l&m-------------------&r&6["));
                    return true;
                } 
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            String.valueOf(getConfig().getString("prefix")) + " &cPlease enter 1 or 2."));


            
            }
            else if (args[0].equalsIgnoreCase("add")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.RED + "Please enter a word."));
                    return true;
                } 

                
                if (getConfig().getStringList("words").contains(args[1])) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "This word is already on the list!"));
                    return true;
                } 

                
                List<String> list = getConfig().getStringList("words");
                list.add(args[1].toLowerCase());
                getConfig().set("words", list);
                saveConfig();
                reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.GREEN + "Word added successfully to the list!"));

            
            }
            else if (args[0].equalsIgnoreCase("info")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 

                
                String wordlist = getConfig().getStringList("words").toString().replace("[", "").replace("]", "");
                
                sender.sendMessage(
                        ChatColor.RED + "------[ " + ChatColor.GRAY + "AntiSwear Info" + ChatColor.RED + " ]------");
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.GRAY + "Prefix: " + 
                        ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix")));
                sender.sendMessage(ChatColor.GRAY + "Swear Message: " + 
                        ChatColor.translateAlternateColorCodes('&', getConfig().getString("message")));
                sender.sendMessage(
                        ChatColor.GRAY + "Globalmute: " + ChatColor.GOLD + getConfig().getBoolean("mute_status"));
                sender.sendMessage(ChatColor.GRAY + "Kick Message: " + 
                        ChatColor.translateAlternateColorCodes('&', getConfig().getString("kickmessage")));
                sender.sendMessage(ChatColor.GRAY + "Words: " + ChatColor.RED + wordlist);
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.RED + "--------------------------");

            
            }
            else if (args[0].equalsIgnoreCase("check")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 


                
                double version = updatecheck();
                double localversion = Double.parseDouble(this.pdfFile.getVersion());

                
                if (localversion < version) {
                    sender.sendMessage(ChatColor.RED + "------[ " + 
                            ChatColor.GRAY + "AntiSwear Update" + ChatColor.RED + 
                            " ]------"); sender.sendMessage(" ");
                    sender.sendMessage(ChatColor.DARK_RED + 
                            "This server is running an older version (" + 
                            this.pdfFile.getVersion() + 
                            "), the newest version available is " + version + ".");
                    sender.sendMessage(" ");
                    (new FancyMessage("Click here to go to the download page."))
                        .color(ChatColor.RED).link(
                            "https://www.spigotmc.org/resources/advanced-antiswear.16354/")
                        .tooltip("Download page").send(sender);
                    sender.sendMessage(" "); sender.sendMessage(ChatColor.RED + 
                            "-----------------------------");
                }
                else {
                    
                    sender.sendMessage(ChatColor.RED + "------[ " + 
                            ChatColor.GRAY + "AntiSwear Update" + ChatColor.RED + 
                            " ]------"); sender.sendMessage(" ");
                    sender.sendMessage(ChatColor.GREEN + 
                            "This server is running the latest version of Advanced AntiSwear.");
                    sender.sendMessage(" ");
                    sender.sendMessage(ChatColor.RED + 
                            "-----------------------------");


                
                }


            
            }
            else if (args[0].equalsIgnoreCase("debug")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 

                
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.GREEN + "Sent output to console. Please copy and paste it."));
                getLogger().info("---------- Antiswear Debug ----------");
                getLogger().info("Antiswear version " + this.pdfFile.getVersion());
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

            
            }
            else if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("antiswear.manage")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.RED + "Please enter a word."));
                    return true;
                } 

                
                if (!getConfig().getStringList("words").contains(args[1])) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                                String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.RED + "Word not found."));
                    return true;
                } 

                
                List<String> list = getConfig().getStringList("words");
                list.remove(args[1].toLowerCase());
                getConfig().set("words", list);
                saveConfig();
                reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.GREEN + "Word removed successfully from the list!"));

            
            }
            else if (args[0].equalsIgnoreCase("prefix")) {
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

            
            }
            else if (args[0].equalsIgnoreCase("message")) {
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
            }
            else if (args[0].equalsIgnoreCase("count")) {
                if (!sender.hasPermission("antiswear.mod")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as count <player>"));
                    return true;
                } 

                
                FileConfiguration data = this.man.getConfig("data", (Plugin)this);
                
                OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
                if (op.hasPlayedBefore()) {
                    UUID uuid = op.getUniqueId();
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
                    
                    String str1 = WordUtils.capitalize(op.getName());
                    int count = data.getInt(Suuid);
                    
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                                " " + ChatColor.DARK_GREEN + str1 + " &acursed &e" + count + "&a times."));
                    return true;
                } 

                
                String opname = WordUtils.capitalize(op.getName());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + ChatColor.DARK_RED + opname + " &cnot found."));


            
            }
            else if (args[0].equalsIgnoreCase("kickmessage")) {
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
            
            }
            else if (args[0].equalsIgnoreCase("cc")) {
                if (!sender.hasPermission("antiswear.mod")) {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
                    return true;
                } 
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
                            " " + getConfig().getString("clearmessage").replaceAll("%player%", sender.getName())));

            
            }
            else if (args[0].equalsIgnoreCase("toggle")) {
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
            
            }
            else {
                
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
}
