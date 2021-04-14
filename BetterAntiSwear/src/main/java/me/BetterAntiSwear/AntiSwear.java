/*      */ package me.BetterAntiSwear;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.URL;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Random;
/*      */ import java.util.UUID;
/*      */ import mkremins.fanciful.FancyMessage;
/*      */ import n.Z3Z.m.HoloGramAPI;
/*      */ import net.milkbowl.vault.economy.Economy;
/*      */ import net.milkbowl.vault.economy.EconomyResponse;
/*      */ import org.apache.commons.lang.WordUtils;
/*      */ import org.bukkit.Bukkit;
/*      */ import org.bukkit.ChatColor;
/*      */ import org.bukkit.Location;
/*      */ import org.bukkit.OfflinePlayer;
/*      */ import org.bukkit.Sound;
/*      */ import org.bukkit.command.Command;
/*      */ import org.bukkit.command.CommandSender;
/*      */ import org.bukkit.configuration.file.FileConfiguration;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.event.EventHandler;
/*      */ import org.bukkit.event.EventPriority;
/*      */ import org.bukkit.event.Listener;
/*      */ import org.bukkit.event.player.AsyncPlayerChatEvent;
/*      */ import org.bukkit.event.player.PlayerCommandPreprocessEvent;
/*      */ import org.bukkit.event.player.PlayerJoinEvent;
/*      */ import org.bukkit.plugin.Plugin;
/*      */ import org.bukkit.plugin.PluginDescriptionFile;
/*      */ import org.bukkit.plugin.RegisteredServiceProvider;
/*      */ import org.bukkit.plugin.java.JavaPlugin;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class AntiSwear
/*      */   extends JavaPlugin
/*      */   implements Listener
/*      */ {
/*   61 */   HashMap<String, Integer> swearCount = new HashMap<>();
/*   62 */   ArrayList<String> globalMute = new ArrayList<>();
/*   63 */   ConfigManager man = new ConfigManager();
/*      */   private PluginDescriptionFile pdfFile;
/*   65 */   public static Economy econ = null;
/*      */   
/*      */   int swears;
/*   68 */   Date now = new Date();
/*   69 */   SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
/*      */   
/*      */   public static FileConfiguration config;
/*      */ 
/*      */   
/*      */   public void logToFile(String message) {
/*      */     try {
/*   76 */       File dataFolder = getDataFolder();
/*   77 */       if (!dataFolder.exists()) {
/*   78 */         dataFolder.mkdir();
/*      */       }
/*      */       
/*   81 */       File saveTo = new File(getDataFolder(), "log.txt");
/*   82 */       if (!saveTo.exists()) {
/*   83 */         saveTo.createNewFile();
/*      */       }
/*      */       
/*   86 */       FileWriter fw = new FileWriter(saveTo, true);
/*      */       
/*   88 */       PrintWriter pw = new PrintWriter(fw);
/*      */       
/*   90 */       pw.println(message);
/*      */       
/*   92 */       pw.flush();
/*      */       
/*   94 */       pw.close();
/*      */     }
/*   96 */     catch (IOException e) {
/*      */       
/*   98 */       e.printStackTrace();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean setupEconomy() {
/*  107 */     if (getServer().getPluginManager().getPlugin("Vault") == null) {
/*  108 */       return false;
/*      */     }
/*  110 */     RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
/*  111 */     if (rsp == null) {
/*  112 */       return false;
/*      */     }
/*  114 */     econ = (Economy)rsp.getProvider();
/*  115 */     return (econ != null);
/*      */   }
/*      */   
/*      */   public double updatecheck() {
/*  119 */     double ver = 0.0D;
/*      */     try {
/*  121 */       HttpURLConnection con = (HttpURLConnection)(new URL("https://api.spigotmc.org/legacy/update.php?resource=16354"))
/*  122 */         .openConnection();
/*  123 */       con.setDoOutput(true);
/*  124 */       con.setRequestMethod("GET");
/*  125 */       String version = (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();
/*  126 */       if (version.length() <= 7)
/*      */       {
/*  128 */         ver = Double.parseDouble(version);
/*      */       }
/*      */ 
/*      */       
/*  132 */       con.disconnect();
/*      */     
/*      */     }
/*  135 */     catch (Exception ex) {
/*  136 */       getLogger().info("Failed to check for an update on Spigot.");
/*      */     } 
/*  138 */     return ver;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @EventHandler
/*      */   public void onPlayerJoin(PlayerJoinEvent er) {
/*  146 */     Player p = er.getPlayer();
/*  147 */     if (p.hasPermission("antiswear.manage")) {
/*      */       
/*  149 */       double version = updatecheck();
/*  150 */       double localversion = Double.parseDouble(this.pdfFile.getVersion());
/*  151 */       if (localversion < version) {
/*      */         
/*  153 */         p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  154 */               String.valueOf(getConfig().getString("prefix")) + " &cThis server is running an older version (" + 
/*  155 */               this.pdfFile.getVersion() + 
/*  156 */               ") of Advanced Antiswear. The newest version available is " + version + ". Only people with the permission" + 
/*  157 */               " &e&oantiswear.manage&r&c can see this message."));
/*  158 */         (new FancyMessage("Click here to go to the download page.")).color(ChatColor.GREEN)
/*  159 */           .link("https://www.spigotmc.org/resources/advanced-antiswear.16354/")
/*  160 */           .tooltip("Advanced AntiSwear download page").send(p);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
/*      */   public void onChatEvent(AsyncPlayerChatEvent e) {
/*  171 */     if (this.globalMute.contains("true")) {
/*  172 */       Player p = e.getPlayer();
/*  173 */       if (p.hasPermission("antiswear.bypass"))
/*      */         return; 
/*  175 */       e.setCancelled(true);
/*      */       
/*  177 */       p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + " " + 
/*  178 */             getConfig().getString("globalmute").replace("%player%", e.getPlayer().getName())));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
/*      */   public void PlayerCommand(PlayerCommandPreprocessEvent e) {
/*  187 */     if (e.getPlayer().hasPermission("antiswear.bypass")) {
/*      */       return;
/*      */     }
/*      */     
/*  191 */     String me1 = e.getMessage().replace("/", "");
/*  192 */     String message = ChatColor.stripColor(me1.replaceAll("&", "§"));
/*      */ 
/*      */     
/*  195 */     if (message.startsWith("as") || message.startsWith("antiswear") || message.startsWith("asw")) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  200 */     for (String word : getConfig().getStringList("words")) {
/*  201 */       byte b; int i; String[] arrayOfString; for (i = (arrayOfString = e.getMessage().split(" ")).length, b = 0; b < i; ) { String message1 = arrayOfString[b];
/*      */         
/*  203 */         StringBuilder builder = new StringBuilder(); byte b1; int j; char[] arrayOfChar;
/*  204 */         for (j = (arrayOfChar = message1.toCharArray()).length, b1 = 0; b1 < j; ) { char character = arrayOfChar[b1];
/*  205 */           if ((character >= '0' && character <= '9') || (character >= 'A' && character <= 'Z') || (
/*  206 */             character >= 'a' && character <= 'z'))
/*  207 */             builder.append(character); 
/*      */           b1++; }
/*      */         
/*  210 */         String filtered = builder.toString();
/*      */         
/*  212 */         if (filtered.toLowerCase().equalsIgnoreCase(word.toLowerCase()))
/*      */         {
/*  214 */           e.setCancelled(true);
/*      */         }
/*      */         
/*      */         b++; }
/*      */     
/*      */     } 
/*  220 */     if (e.isCancelled())
/*      */     {
/*  222 */       onSwear(e.getPlayer(), e.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
/*      */   public void onPlayerChat(AsyncPlayerChatEvent e) {
/*  233 */     if (e.getPlayer().hasPermission("antiswear.bypass")) {
/*      */       return;
/*      */     }
/*      */     
/*  237 */     String me2 = e.getMessage().replace("/", "");
/*  238 */     String messagens = ChatColor.stripColor(me2.replaceAll("&", "§"));
/*      */     
/*  240 */     for (String word : getConfig().getStringList("words")) {
/*  241 */       byte b; int i; String[] arrayOfString; for (i = (arrayOfString = messagens.split(" ")).length, b = 0; b < i; ) { String message = arrayOfString[b];
/*      */         
/*  243 */         StringBuilder builder = new StringBuilder(); byte b1; int j; char[] arrayOfChar;
/*  244 */         for (j = (arrayOfChar = message.toCharArray()).length, b1 = 0; b1 < j; ) { char character = arrayOfChar[b1];
/*  245 */           if ((character >= '0' && character <= '9') || (character >= 'A' && character <= 'Z') || (
/*  246 */             character >= 'a' && character <= 'z')) {
/*  247 */             builder.append(character);
/*      */           }
/*      */           b1++; }
/*      */         
/*  251 */         String filtered = builder.toString();
/*      */         
/*  253 */         if (filtered.toLowerCase().equalsIgnoreCase(word.toLowerCase()))
/*      */         {
/*      */           
/*  256 */           e.setCancelled(true);
/*      */         }
/*      */ 
/*      */         
/*      */         b++; }
/*      */     
/*      */     } 
/*      */     
/*  264 */     if (e.isCancelled())
/*      */     {
/*  266 */       onSwear(e.getPlayer(), e.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void onSwear(final Player p, String message) {
/*  273 */     this.swears++;
/*      */     
/*  275 */     if (getConfig().getBoolean("log")) {
/*  276 */       logToFile("[" + this.format.format(this.now) + "] " + p.getName() + ": " + message);
/*      */     }
/*      */     
/*  279 */     if (!this.swearCount.containsKey(p.getName())) {
/*  280 */       this.swearCount.put(p.getName(), Integer.valueOf(1));
/*      */     } else {
/*      */       
/*  283 */       this.swearCount.put(p.getName(), Integer.valueOf(((Integer)this.swearCount.get(p.getName())).intValue() + 1));
/*      */     } 
/*      */     
/*  286 */     if (getConfig().getBoolean("kick")) {
/*  287 */       if (((Integer)this.swearCount.get(p.getName())).intValue() >= getConfig().getInt("times")) {
/*  288 */         this.swearCount.put(p.getName(), Integer.valueOf(0));
/*  289 */         Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, new Runnable() {
/*      */               public void run() {
/*  291 */                 p.kickPlayer(ChatColor.translateAlternateColorCodes('&', ChatColor.RED + 
/*  292 */                       AntiSwear.config.getString("kickmessage").replaceAll("%player%", p.getName())));
/*      */               }
/*  294 */             });
/*      */       } 
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  301 */     if (getConfig().getString("reportmessage") == null) {
/*  302 */       Bukkit.broadcast(
/*  303 */           ChatColor.translateAlternateColorCodes('&', 
/*  304 */             String.valueOf(getConfig().getString("prefix")) + ChatColor.RED + " %player% tried saying '" + 
/*  305 */             ChatColor.DARK_RED + message + ChatColor.RED + "'.")
/*  306 */           .replaceAll("%player%", p.getName()), "antiswear.mod");
/*      */     } else {
/*  308 */       Bukkit.broadcast(
/*  309 */           ChatColor.translateAlternateColorCodes('&', 
/*  310 */             String.valueOf(getConfig().getString("prefix")) + " " + getConfig().getString("reportmessage"))
/*  311 */           .replaceAll("%player%", p.getName()).replaceAll("%message%", message), "antiswear.mod");
/*      */     } 
/*      */     
/*  314 */     if (p.isOnline()) {
/*      */       
/*  316 */       if (!getConfig().getString("command").equalsIgnoreCase("none")) {
/*  317 */         Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getServer().getConsoleSender(), 
/*  318 */             getConfig().getString("command").replaceAll("%player%", p.getName()));
/*      */       }
/*      */ 
/*      */       
/*  322 */       if (getConfig().getBoolean("damagetoggle"))
/*      */       {
/*  324 */         Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, new Runnable()
/*      */             {
/*      */               public void run() {
/*  327 */                 double damageAmount = Double.parseDouble(AntiSwear.config.getString("damage"));
/*  328 */                 p.damage(damageAmount);
/*      */               }
/*      */             }, 
/*  331 */             1L);
/*      */       }
/*      */ 
/*      */       
/*  335 */       Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, new Runnable()
/*      */           {
/*      */             public void run() {
/*  338 */               Location loc = p.getLocation()
/*  339 */                 .add(p.getLocation().getDirection().normalize().multiply(2.6D));
/*      */               
/*  341 */               Random rand = new Random();
/*      */               
/*  343 */               int random = rand.nextInt(10000);
/*      */ 
/*      */ 
/*      */               
/*  347 */               if (AntiSwear.this.getServer().getPluginManager().getPlugin("HoloGramAPI") != null)
/*      */               {
/*  349 */                 HoloGramAPI.createTempHoloGram(loc, AntiSwear.this.getConfig().getString("holomessage").replaceAll("%player%", p.getName()), Integer.valueOf(random), Integer.valueOf(AntiSwear.this.getConfig().getInt("holotime")), false, false, false, null, null, null);
/*      */ 
/*      */ 
/*      */               
/*      */               }
/*      */             }
/*  355 */           });
/*      */       
/*  357 */       if (config.getBoolean("actionbar")) {
/*  358 */         ActionBar actionBar = new ActionBar();
/*  359 */         actionBar.setMessage(
/*  360 */             ChatColor.translateAlternateColorCodes('&', 
/*  361 */               String.valueOf(getConfig().getString("prefix")) + " " + 
/*  362 */               getConfig().getString("actionbarmessage"))
/*  363 */             .replace("%player%", p.getName()));
/*  364 */         actionBar.send(new Player[] { p });
/*      */       } 
/*      */ 
/*      */       
/*  368 */       if (getConfig().getBoolean("sound")) {
/*  369 */         p.playSound(p.getLocation(), 
/*  370 */             Sound.valueOf(getConfig().getString("soundvalue").toUpperCase()), 1.0F, 1.0F);
/*      */       }
/*      */ 
/*      */       
/*  374 */       if (getConfig().getBoolean("balance") && 
/*  375 */         getServer().getPluginManager().getPlugin("Vault") != null) {
/*      */         
/*  377 */         int amou = getConfig().getInt("balamount");
/*      */         
/*  379 */         EconomyResponse r = econ.withdrawPlayer((OfflinePlayer)p, amou);
/*      */         
/*  381 */         if (r.transactionSuccess())
/*      */         {
/*  383 */           p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  384 */                 String.valueOf(getConfig().getString("prefix")) + " " + 
/*  385 */                 getConfig().getString("chargemessage")
/*  386 */                 .replaceAll("%player%", p.getName())
/*  387 */                 .replaceAll("%amount%", (new StringBuilder(String.valueOf(amou))).toString())));
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  395 */       if (getConfig().getBoolean("chatmessage")) {
/*  396 */         p.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("%s %s", new Object[] {
/*  397 */                   getConfig().getString("prefix"), 
/*  398 */                   getConfig().getString("message").replaceAll("%player%", p.getName())
/*      */                 })));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void onEnable() {
/*      */     try {
/*  411 */       this.man.createConfig("data", (Plugin)this);
/*  412 */     } catch (IOException e) {
/*  413 */       if (getConfig().getBoolean("debug")) {
/*  414 */         e.printStackTrace();
/*      */       }
/*  416 */       getLogger().warning("Something went wrong while generating a data file.");
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/*  421 */       if (!getDataFolder().exists()) {
/*  422 */         getDataFolder().mkdirs();
/*      */       }
/*  424 */       File file = new File(getDataFolder(), "config.yml");
/*  425 */       if (!file.exists()) {
/*  426 */         saveDefaultConfig();
/*      */       }
/*  428 */     } catch (Exception e) {
/*  429 */       e.printStackTrace();
/*      */     } 
/*      */ 
/*      */     
/*  433 */     this.pdfFile = getDescription();
/*  434 */     double version = updatecheck();
/*  435 */     double localversion = Double.parseDouble(this.pdfFile.getVersion());
/*  436 */     if (localversion < version) {
/*  437 */       getLogger().info("There is an update available. This server is running Antiswear version " + localversion + ", but version " + version + "is available.");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */ 
/*      */ 
/*      */     
/*  454 */     if (getServer().getPluginManager().getPlugin("HoloGramAPI") != null) {
/*  455 */       Bukkit.getConsoleSender()
/*  456 */         .sendMessage(ChatColor.GOLD + "[AntiSwear] " + ChatColor.GREEN + "Hooked into HoloGramAPI.");
/*      */     }
/*  458 */     if (this.globalMute.contains("true")) {
/*  459 */       this.globalMute.clear();
/*      */     }
/*  461 */     this.globalMute.add("false");
/*      */ 
/*      */     
/*  464 */     getLogger()
/*  465 */       .info(String.valueOf(this.pdfFile.getName()) + " Version: " + this.pdfFile.getVersion() + " by brooky1010 is enabled!");
/*  466 */     config = getConfig();
/*      */     
/*  468 */     Bukkit.getServer().getPluginManager().registerEvents(this, (Plugin)this);
/*      */ 
/*      */ 
/*      */     
/*  472 */     if (!setupEconomy()) {
/*  473 */       getLogger()
/*  474 */         .severe(String.format("Economy module deactivated - Vault not found", new Object[] { getDescription().getName() }));
/*      */       return;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void onDisable() {
/*  483 */     this.pdfFile = getDescription();
/*  484 */     getLogger().info(String.valueOf(this.pdfFile.getName()) + " by brooky1010 is now disabled!");
/*  485 */     getLogger().info("Website: themilkywalrus.com");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
/*  491 */     if (cmd.getName().equalsIgnoreCase("antiswear")) {
/*  492 */       if (args.length == 0) {
/*  493 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  494 */               "&6]&l&m-------------&r&6[&c&lAdvanced AntiSwear&6]&l&m-------------&r&6["));
/*  495 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
/*  496 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l- &eAuthor: &a&lBrooky1010"));
/*  497 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l- &eTwitter: &a&l@TheMilkyWalrus"));
/*  498 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
/*  499 */         sender.sendMessage(
/*  500 */             ChatColor.translateAlternateColorCodes('&', "&7&l- &eVersion:&7 " + this.pdfFile.getVersion()));
/*  501 */         if (getServer().getPluginManager().getPlugin("HoloGramAPI") != null) {
/*  502 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l- &eHologramAPI:&a Active"));
/*      */         } else {
/*  504 */           sender.sendMessage(
/*  505 */               ChatColor.translateAlternateColorCodes('&', "&7&l- &eHologramAPI:&c Not installed"));
/*      */         } 
/*  507 */         sender.sendMessage(
/*  508 */             ChatColor.translateAlternateColorCodes('&', "&7&l- &e/as help&7 for a list of commands"));
/*  509 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
/*  510 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  511 */               "&6]&l&m-------------&r&6[&c&lAdvanced AntiSwear&6]&l&m-------------&r&6["));
/*  512 */         return true;
/*      */       } 
/*      */       
/*  515 */       if (args[0].equalsIgnoreCase("reload")) {
/*      */ 
/*      */ 
/*      */         
/*  519 */         if (!sender.hasPermission("antiswear.reload")) {
/*  520 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  521 */           return true;
/*      */         } 
/*  523 */         reloadConfig();
/*      */         
/*  525 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  526 */               " " + ChatColor.RED + getConfig().getString("reload_message")));
/*      */       }
/*  528 */       else if (args[0].equalsIgnoreCase("help")) {
/*  529 */         if (args.length == 1) {
/*  530 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  531 */                 "&6]&l&m-------------&r&6[&c&lAdvanced AntiSwear&6]&l&m-------------&r&6["));
/*  532 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
/*  533 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  534 */                 "&7&l- &e/as message &7           Change the chat message."));
/*  535 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  536 */                 "&7&l- &e/as prefix &7              Change chat prefix."));
/*  537 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  538 */                 "&7&l- &e/as kickmessage &7    Change kick message."));
/*  539 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  540 */                 "&7&l- &e/as add &7                  Add a swear word."));
/*  541 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  542 */                 "&7&l- &e/as remove &7            Remove a swear word."));
/*  543 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  544 */                 "&7&l- &e/as help 2 &7              Help menu 2."));
/*  545 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
/*  546 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  547 */                 "&6]&l&m-------------------&r&6[&c&lHelp 1&6]&l&m-------------------&r&6["));
/*  548 */           return true;
/*      */         } 
/*      */         
/*  551 */         if (args[1].equalsIgnoreCase("1")) {
/*  552 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  553 */                 "&6]&l&m-------------&r&6[&c&lAdvanced AntiSwear&6]&l&m-------------&r&6["));
/*  554 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
/*  555 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  556 */                 "&7&l- &e/as message &7           Change the chat message."));
/*  557 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  558 */                 "&7&l- &e/as prefix &7              Change chat prefix."));
/*  559 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  560 */                 "&7&l- &e/as kickmessage &7    Change kick message."));
/*  561 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  562 */                 "&7&l- &e/as add &7                  Add a swear word."));
/*  563 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  564 */                 "&7&l- &e/as remove &7            Remove a swear word."));
/*  565 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  566 */                 "&7&l- &e/as help 2 &7              Help menu 2."));
/*  567 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
/*  568 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  569 */                 "&6]&l&m-------------------&r&6[&c&lHelp 1&6]&l&m-------------------&r&6["));
/*  570 */           return true;
/*      */         } 
/*  572 */         if (args[1].equalsIgnoreCase("2")) {
/*  573 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  574 */                 "&6]&l&m-------------&r&6[&c&lAdvanced AntiSwear&6]&l&m-------------&r&6["));
/*  575 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
/*  576 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  577 */                 "&7&l- &e/as info &7                  Show Antiswear info."));
/*  578 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  579 */                 "&7&l- &e/as toggle &7              Toggle globalmute."));
/*  580 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  581 */                 "&7&l- &e/as cc &7                     Clear the entire chat."));
/*  582 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  583 */                 "&7&l- &e/as debug &7               Send debug output to console."));
/*  584 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  585 */                 "&7&l- &e/as check &7               Check for updates."));
/*  586 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  587 */                 "&7&l- &e/as count  &7               Check how many times a player sweared."));
/*  588 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
/*  589 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  590 */                 "&6]&l&m-------------------&r&6[&c&lHelp 2&6]&l&m-------------------&r&6["));
/*  591 */           return true;
/*      */         } 
/*  593 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  594 */               String.valueOf(getConfig().getString("prefix")) + " &cPlease enter 1 or 2."));
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  599 */       else if (args[0].equalsIgnoreCase("add")) {
/*  600 */         if (!sender.hasPermission("antiswear.manage")) {
/*  601 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  602 */           return true;
/*      */         } 
/*      */         
/*  605 */         if (args.length == 1) {
/*  606 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  607 */                 String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.RED + "Please enter a word."));
/*  608 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  612 */         if (getConfig().getStringList("words").contains(args[1])) {
/*  613 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  614 */                 " " + ChatColor.RED + "This word is already on the list!"));
/*  615 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  619 */         List<String> list = getConfig().getStringList("words");
/*  620 */         list.add(args[1].toLowerCase());
/*  621 */         getConfig().set("words", list);
/*  622 */         saveConfig();
/*  623 */         reloadConfig();
/*  624 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  625 */               " " + ChatColor.GREEN + "Word added successfully to the list!"));
/*      */ 
/*      */       
/*      */       }
/*  629 */       else if (args[0].equalsIgnoreCase("info")) {
/*  630 */         if (!sender.hasPermission("antiswear.manage")) {
/*  631 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  632 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  636 */         String wordlist = getConfig().getStringList("words").toString().replace("[", "").replace("]", "");
/*      */         
/*  638 */         sender.sendMessage(
/*  639 */             ChatColor.RED + "------[ " + ChatColor.GRAY + "AntiSwear Info" + ChatColor.RED + " ]------");
/*  640 */         sender.sendMessage(" ");
/*  641 */         sender.sendMessage(ChatColor.GRAY + "Prefix: " + 
/*  642 */             ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix")));
/*  643 */         sender.sendMessage(ChatColor.GRAY + "Swear Message: " + 
/*  644 */             ChatColor.translateAlternateColorCodes('&', getConfig().getString("message")));
/*  645 */         sender.sendMessage(
/*  646 */             ChatColor.GRAY + "Globalmute: " + ChatColor.GOLD + getConfig().getBoolean("mute_status"));
/*  647 */         sender.sendMessage(ChatColor.GRAY + "Kick Message: " + 
/*  648 */             ChatColor.translateAlternateColorCodes('&', getConfig().getString("kickmessage")));
/*  649 */         sender.sendMessage(ChatColor.GRAY + "Words: " + ChatColor.RED + wordlist);
/*  650 */         sender.sendMessage(" ");
/*  651 */         sender.sendMessage(ChatColor.RED + "--------------------------");
/*      */ 
/*      */       
/*      */       }
/*  655 */       else if (args[0].equalsIgnoreCase("check")) {
/*  656 */         if (!sender.hasPermission("antiswear.manage")) {
/*  657 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  658 */           return true;
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  663 */         double version = updatecheck();
/*  664 */         double localversion = Double.parseDouble(this.pdfFile.getVersion());
/*      */ 
/*      */         
/*  667 */         if (localversion < version) {
/*  668 */           sender.sendMessage(ChatColor.RED + "------[ " + 
/*  669 */               ChatColor.GRAY + "AntiSwear Update" + ChatColor.RED + 
/*  670 */               " ]------"); sender.sendMessage(" ");
/*  671 */           sender.sendMessage(ChatColor.DARK_RED + 
/*  672 */               "This server is running an older version (" + 
/*  673 */               this.pdfFile.getVersion() + 
/*  674 */               "), the newest version available is " + version + ".");
/*  675 */           sender.sendMessage(" ");
/*  676 */           (new FancyMessage("Click here to go to the download page."))
/*  677 */             .color(ChatColor.RED).link(
/*  678 */               "https://www.spigotmc.org/resources/advanced-antiswear.16354/")
/*  679 */             .tooltip("Download page").send(sender);
/*  680 */           sender.sendMessage(" "); sender.sendMessage(ChatColor.RED + 
/*  681 */               "-----------------------------");
/*      */         }
/*      */         else {
/*      */           
/*  685 */           sender.sendMessage(ChatColor.RED + "------[ " + 
/*  686 */               ChatColor.GRAY + "AntiSwear Update" + ChatColor.RED + 
/*  687 */               " ]------"); sender.sendMessage(" ");
/*  688 */           sender.sendMessage(ChatColor.GREEN + 
/*  689 */               "This server is running the latest version of Advanced AntiSwear.");
/*  690 */           sender.sendMessage(" ");
/*  691 */           sender.sendMessage(ChatColor.RED + 
/*  692 */               "-----------------------------");
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  701 */       else if (args[0].equalsIgnoreCase("debug")) {
/*  702 */         if (!sender.hasPermission("antiswear.manage")) {
/*  703 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  704 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  708 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  709 */               " " + ChatColor.GREEN + "Sent output to console. Please copy and paste it."));
/*  710 */         getLogger().info("---------- Antiswear Debug ----------");
/*  711 */         getLogger().info("Antiswear version " + this.pdfFile.getVersion());
/*  712 */         getLogger().info("Server version " + Bukkit.getVersion());
/*  713 */         getLogger().info("Damage: " + getConfig().getBoolean("damagetoggle") + " | Amount: " + 
/*  714 */             getConfig().getInt("damage"));
/*  715 */         getLogger().info("Sound: " + getConfig().getString("soundvalue"));
/*  716 */         getLogger().info("Balance: " + getConfig().getBoolean("balance"));
/*      */ 
/*      */ 
/*      */         
/*  720 */         if (getServer().getPluginManager().getPlugin("HoloGramAPI") != null) {
/*  721 */           getLogger().info("HologramAPI: true");
/*      */         } else {
/*  723 */           getLogger().info("HologramAPI: false");
/*      */         } 
/*      */         
/*  726 */         if (getServer().getPluginManager().getPlugin("Plan") != null) {
/*  727 */           getLogger().info("Plan: true");
/*      */         } else {
/*  729 */           getLogger().info("Plan: false");
/*      */         } 
/*      */         
/*  732 */         if (getServer().getPluginManager().getPlugin("Vault") != null) {
/*  733 */           getLogger().info("Vault: true");
/*      */         } else {
/*  735 */           getLogger().info("Vault: false");
/*      */         } 
/*      */         
/*  738 */         getLogger().info("----------- End of debug -----------");
/*      */ 
/*      */       
/*      */       }
/*  742 */       else if (args[0].equalsIgnoreCase("remove")) {
/*  743 */         if (!sender.hasPermission("antiswear.manage")) {
/*  744 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  745 */           return true;
/*      */         } 
/*      */         
/*  748 */         if (args.length == 1) {
/*  749 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  750 */                 String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.RED + "Please enter a word."));
/*  751 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  755 */         if (!getConfig().getStringList("words").contains(args[1])) {
/*  756 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  757 */                 String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.RED + "Word not found."));
/*  758 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  762 */         List<String> list = getConfig().getStringList("words");
/*  763 */         list.remove(args[1].toLowerCase());
/*  764 */         getConfig().set("words", list);
/*  765 */         saveConfig();
/*  766 */         reloadConfig();
/*  767 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  768 */               " " + ChatColor.GREEN + "Word removed successfully from the list!"));
/*      */ 
/*      */       
/*      */       }
/*  772 */       else if (args[0].equalsIgnoreCase("prefix")) {
/*  773 */         if (!sender.hasPermission("antiswear.manage")) {
/*  774 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  775 */           return true;
/*      */         } 
/*      */         
/*  778 */         if (args.length == 1) {
/*  779 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  780 */                 " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as set prefix <prefix>"));
/*  781 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  785 */         String message = "";
/*  786 */         for (int i = 1; i < args.length; i++) {
/*  787 */           message = String.valueOf(message) + args[i] + " ";
/*      */         }
/*  789 */         message = message.trim();
/*      */         
/*  791 */         getConfig().set("prefix", message);
/*  792 */         saveConfig();
/*  793 */         reloadConfig();
/*  794 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/*  795 */               String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.GREEN + "Prefix changed successfully."));
/*      */ 
/*      */       
/*      */       }
/*  799 */       else if (args[0].equalsIgnoreCase("message")) {
/*  800 */         if (!sender.hasPermission("antiswear.manage")) {
/*  801 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  802 */           return true;
/*      */         } 
/*      */         
/*  805 */         if (args.length == 1) {
/*  806 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  807 */                 " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as message <message>"));
/*  808 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  812 */         String message = "";
/*  813 */         for (int i = 1; i < args.length; i++) {
/*  814 */           message = String.valueOf(message) + args[i] + " ";
/*      */         }
/*  816 */         message = message.trim();
/*      */         
/*  818 */         getConfig().set("message", message);
/*  819 */         saveConfig();
/*  820 */         reloadConfig();
/*  821 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  822 */               " " + ChatColor.GREEN + "Message changed successfully."));
/*      */       }
/*  824 */       else if (args[0].equalsIgnoreCase("count")) {
/*  825 */         if (!sender.hasPermission("antiswear.mod")) {
/*  826 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  827 */           return true;
/*      */         } 
/*      */         
/*  830 */         if (args.length == 1) {
/*  831 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  832 */                 " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as count <player>"));
/*  833 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  837 */         FileConfiguration data = this.man.getConfig("data", (Plugin)this);
/*      */         
/*  839 */         OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
/*  840 */         if (op.hasPlayedBefore()) {
/*  841 */           UUID uuid = op.getUniqueId();
/*  842 */           String Suuid = uuid.toString();
/*      */ 
/*      */           
/*  845 */           if (!data.contains(Suuid)) {
/*  846 */             data.set(Suuid, Integer.valueOf(0));
/*      */             try {
/*  848 */               this.man.saveConfig(data, "data", (Plugin)this);
/*  849 */               this.man.reloadConfig("data", (Plugin)this);
/*  850 */             } catch (IOException e) {
/*  851 */               if (getConfig().getBoolean("debug")) {
/*  852 */                 e.printStackTrace();
/*      */               }
/*  854 */               getLogger().warning("Something went wrong while setting Plan data.");
/*      */             } 
/*      */           } 
/*      */           
/*  858 */           String str1 = WordUtils.capitalize(op.getName());
/*  859 */           int count = data.getInt(Suuid);
/*      */           
/*  861 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  862 */                 " " + ChatColor.DARK_GREEN + str1 + " &acursed &e" + count + "&a times."));
/*  863 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  867 */         String opname = WordUtils.capitalize(op.getName());
/*  868 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  869 */               " " + ChatColor.DARK_RED + opname + " &cnot found."));
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  874 */       else if (args[0].equalsIgnoreCase("kickmessage")) {
/*  875 */         if (!sender.hasPermission("antiswear.manage")) {
/*  876 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  877 */           return true;
/*      */         } 
/*      */         
/*  880 */         if (args.length == 1) {
/*  881 */           sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  882 */                 " " + ChatColor.RED + "Usage: " + ChatColor.GRAY + "/as kickmessage <message>"));
/*  883 */           return true;
/*      */         } 
/*      */ 
/*      */         
/*  887 */         String message = "";
/*  888 */         for (int i = 1; i < args.length; i++) {
/*  889 */           message = String.valueOf(message) + args[i] + " ";
/*      */         }
/*  891 */         message = message.trim();
/*      */         
/*  893 */         getConfig().set("kickmessage", message);
/*  894 */         saveConfig();
/*  895 */         reloadConfig();
/*  896 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/*  897 */               " " + ChatColor.GREEN + "Message changed successfully."));
/*      */       
/*      */       }
/*  900 */       else if (args[0].equalsIgnoreCase("cc")) {
/*  901 */         if (!sender.hasPermission("antiswear.mod")) {
/*  902 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/*  903 */           return true;
/*      */         } 
/*  905 */         Bukkit.broadcastMessage("");
/*  906 */         Bukkit.broadcastMessage("");
/*  907 */         Bukkit.broadcastMessage("");
/*  908 */         Bukkit.broadcastMessage("");
/*  909 */         Bukkit.broadcastMessage("");
/*  910 */         Bukkit.broadcastMessage("");
/*  911 */         Bukkit.broadcastMessage("");
/*  912 */         Bukkit.broadcastMessage("");
/*  913 */         Bukkit.broadcastMessage("");
/*  914 */         Bukkit.broadcastMessage("");
/*  915 */         Bukkit.broadcastMessage("");
/*  916 */         Bukkit.broadcastMessage("");
/*  917 */         Bukkit.broadcastMessage("");
/*  918 */         Bukkit.broadcastMessage("");
/*  919 */         Bukkit.broadcastMessage("");
/*  920 */         Bukkit.broadcastMessage("");
/*  921 */         Bukkit.broadcastMessage("");
/*  922 */         Bukkit.broadcastMessage("");
/*  923 */         Bukkit.broadcastMessage("");
/*  924 */         Bukkit.broadcastMessage("");
/*  925 */         Bukkit.broadcastMessage("");
/*  926 */         Bukkit.broadcastMessage("");
/*  927 */         Bukkit.broadcastMessage("");
/*  928 */         Bukkit.broadcastMessage("");
/*  929 */         Bukkit.broadcastMessage("");
/*  930 */         Bukkit.broadcastMessage("");
/*  931 */         Bukkit.broadcastMessage("");
/*  932 */         Bukkit.broadcastMessage("");
/*  933 */         Bukkit.broadcastMessage("");
/*  934 */         Bukkit.broadcastMessage("");
/*  935 */         Bukkit.broadcastMessage("");
/*  936 */         Bukkit.broadcastMessage("");
/*  937 */         Bukkit.broadcastMessage("");
/*  938 */         Bukkit.broadcastMessage("");
/*  939 */         Bukkit.broadcastMessage("");
/*  940 */         Bukkit.broadcastMessage("");
/*  941 */         Bukkit.broadcastMessage("");
/*  942 */         Bukkit.broadcastMessage("");
/*  943 */         Bukkit.broadcastMessage("");
/*  944 */         Bukkit.broadcastMessage("");
/*  945 */         Bukkit.broadcastMessage("");
/*  946 */         Bukkit.broadcastMessage("");
/*  947 */         Bukkit.broadcastMessage("");
/*  948 */         Bukkit.broadcastMessage("");
/*  949 */         Bukkit.broadcastMessage("");
/*  950 */         Bukkit.broadcastMessage("");
/*  951 */         Bukkit.broadcastMessage("");
/*  952 */         Bukkit.broadcastMessage("");
/*  953 */         Bukkit.broadcastMessage("");
/*  954 */         Bukkit.broadcastMessage("");
/*  955 */         Bukkit.broadcastMessage("");
/*  956 */         Bukkit.broadcastMessage("");
/*  957 */         Bukkit.broadcastMessage("");
/*  958 */         Bukkit.broadcastMessage("");
/*  959 */         Bukkit.broadcastMessage("");
/*  960 */         Bukkit.broadcastMessage("");
/*  961 */         Bukkit.broadcastMessage("");
/*  962 */         Bukkit.broadcastMessage("");
/*  963 */         Bukkit.broadcastMessage("");
/*  964 */         Bukkit.broadcastMessage("");
/*  965 */         Bukkit.broadcastMessage("");
/*  966 */         Bukkit.broadcastMessage("");
/*  967 */         Bukkit.broadcastMessage("");
/*  968 */         Bukkit.broadcastMessage("");
/*  969 */         Bukkit.broadcastMessage("");
/*  970 */         Bukkit.broadcastMessage("");
/*  971 */         Bukkit.broadcastMessage("");
/*  972 */         Bukkit.broadcastMessage("");
/*  973 */         Bukkit.broadcastMessage("");
/*  974 */         Bukkit.broadcastMessage("");
/*  975 */         Bukkit.broadcastMessage("");
/*  976 */         Bukkit.broadcastMessage("");
/*  977 */         Bukkit.broadcastMessage("");
/*  978 */         Bukkit.broadcastMessage("");
/*  979 */         Bukkit.broadcastMessage("");
/*  980 */         Bukkit.broadcastMessage("");
/*  981 */         Bukkit.broadcastMessage("");
/*  982 */         Bukkit.broadcastMessage("");
/*  983 */         Bukkit.broadcastMessage("");
/*  984 */         Bukkit.broadcastMessage("");
/*  985 */         Bukkit.broadcastMessage("");
/*  986 */         Bukkit.broadcastMessage("");
/*  987 */         Bukkit.broadcastMessage("");
/*  988 */         Bukkit.broadcastMessage("");
/*  989 */         Bukkit.broadcastMessage("");
/*  990 */         Bukkit.broadcastMessage("");
/*  991 */         Bukkit.broadcastMessage("");
/*  992 */         Bukkit.broadcastMessage("");
/*  993 */         Bukkit.broadcastMessage("");
/*  994 */         Bukkit.broadcastMessage("");
/*  995 */         Bukkit.broadcastMessage("");
/*  996 */         Bukkit.broadcastMessage("");
/*  997 */         Bukkit.broadcastMessage("");
/*  998 */         Bukkit.broadcastMessage("");
/*  999 */         Bukkit.broadcastMessage("");
/* 1000 */         Bukkit.broadcastMessage("");
/* 1001 */         Bukkit.broadcastMessage("");
/* 1002 */         Bukkit.broadcastMessage("");
/* 1003 */         Bukkit.broadcastMessage("");
/* 1004 */         Bukkit.broadcastMessage("");
/* 1005 */         Bukkit.broadcastMessage("");
/* 1006 */         Bukkit.broadcastMessage("");
/* 1007 */         Bukkit.broadcastMessage("");
/* 1008 */         Bukkit.broadcastMessage("");
/* 1009 */         Bukkit.broadcastMessage("");
/* 1010 */         Bukkit.broadcastMessage("");
/* 1011 */         Bukkit.broadcastMessage("");
/* 1012 */         Bukkit.broadcastMessage("");
/* 1013 */         Bukkit.broadcastMessage("");
/* 1014 */         Bukkit.broadcastMessage("");
/* 1015 */         Bukkit.broadcastMessage("");
/* 1016 */         Bukkit.broadcastMessage("");
/* 1017 */         Bukkit.broadcastMessage("");
/* 1018 */         Bukkit.broadcastMessage("");
/* 1019 */         Bukkit.broadcastMessage("");
/* 1020 */         Bukkit.broadcastMessage("");
/* 1021 */         Bukkit.broadcastMessage("");
/* 1022 */         Bukkit.broadcastMessage("");
/* 1023 */         Bukkit.broadcastMessage("");
/* 1024 */         Bukkit.broadcastMessage("");
/* 1025 */         Bukkit.broadcastMessage("");
/* 1026 */         Bukkit.broadcastMessage("");
/* 1027 */         Bukkit.broadcastMessage("");
/* 1028 */         Bukkit.broadcastMessage("");
/* 1029 */         Bukkit.broadcastMessage("");
/* 1030 */         Bukkit.broadcastMessage("");
/* 1031 */         Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(getConfig().getString("prefix")) + 
/* 1032 */               " " + getConfig().getString("clearmessage").replaceAll("%player%", sender.getName())));
/*      */ 
/*      */       
/*      */       }
/* 1036 */       else if (args[0].equalsIgnoreCase("toggle")) {
/* 1037 */         if (!sender.hasPermission("antiswear.toggle")) {
/* 1038 */           sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
/* 1039 */           return true;
/*      */         } 
/* 1041 */         if (this.globalMute.contains("false")) {
/* 1042 */           this.globalMute.clear();
/* 1043 */           this.globalMute.add("true");
/* 1044 */           Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', 
/* 1045 */                 String.valueOf(getConfig().getString("prefix")) + " " + getConfig().getString("mute_toggle")
/* 1046 */                 .replaceAll("%toggle%", getConfig().getString("mute"))));
/* 1047 */         } else if (this.globalMute.contains("true")) {
/* 1048 */           this.globalMute.clear();
/* 1049 */           this.globalMute.add("false");
/* 1050 */           Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', 
/* 1051 */                 String.valueOf(getConfig().getString("prefix")) + " " + getConfig().getString("mute_toggle")
/* 1052 */                 .replaceAll("%toggle%", getConfig().getString("unmute"))));
/*      */         }
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1058 */         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
/* 1059 */               String.valueOf(getConfig().getString("prefix")) + " " + ChatColor.RED + "Invalid subcommand." + 
/* 1060 */               ChatColor.GRAY + " Try /as for the command list."));
/*      */       } 
/*      */     } 
/*      */     
/* 1064 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getSwearCount(UUID uuid) {
/* 1069 */     FileConfiguration data = this.man.getConfig("data", (Plugin)this);
/* 1070 */     String Suuid = uuid.toString();
/*      */     
/* 1072 */     if (!data.contains(Suuid)) {
/* 1073 */       data.set(Suuid, Integer.valueOf(0));
/*      */       try {
/* 1075 */         this.man.saveConfig(data, "data", (Plugin)this);
/* 1076 */         this.man.reloadConfig("data", (Plugin)this);
/* 1077 */       } catch (IOException e) {
/* 1078 */         if (getConfig().getBoolean("debug")) {
/* 1079 */           e.printStackTrace();
/*      */         }
/* 1081 */         getLogger().warning("Something went wrong while setting Plan data.");
/*      */       } 
/*      */     } 
/*      */     
/* 1085 */     int count = data.getInt(Suuid);
/* 1086 */     return count;
/*      */   }
/*      */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\me\brooky1010\AntiSwear\AntiSwear.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */