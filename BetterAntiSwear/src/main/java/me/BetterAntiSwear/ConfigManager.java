/*     */ package me.BetterAntiSwear;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigManager
/*     */ {
/*     */   public FileConfiguration createConfig(String name, String subdirectory, Plugin plugin) throws IOException {
/*  14 */     File dir = new File(plugin.getDataFolder() + File.separator + subdirectory);
/*  15 */     if (!dir.isDirectory()) {
/*  16 */       dir.mkdirs();
/*     */     }
/*  18 */     File file = new File(dir, String.valueOf(name) + ".yml");
/*  19 */     if (!file.exists()) {
/*  20 */       file.createNewFile();
/*     */     }
/*  22 */     return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
/*     */   }
/*     */   
/*     */   public FileConfiguration createConfig(String name, Plugin plugin) throws IOException {
/*  26 */     File dir = plugin.getDataFolder();
/*  27 */     if (!dir.isDirectory()) {
/*  28 */       dir.mkdirs();
/*     */     }
/*  30 */     File file = new File(plugin.getDataFolder(), String.valueOf(name) + ".yml");
/*  31 */     if (!file.exists()) {
/*  32 */       file.createNewFile();
/*     */     }
/*  34 */     return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
/*     */   }
/*     */   
/*     */   public FileConfiguration createConfig(String name, String directory) throws IOException {
/*  38 */     File dir = new File(directory);
/*  39 */     if (!dir.isDirectory()) {
/*  40 */       dir.mkdirs();
/*     */     }
/*  42 */     File file = new File(dir, String.valueOf(name) + ".yml");
/*  43 */     if (!file.exists()) {
/*  44 */       file.createNewFile();
/*     */     }
/*  46 */     return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
/*     */   }
/*     */   
/*     */   public FileConfiguration createConfig(String name, File directory) throws IOException {
/*  50 */     if (!directory.isDirectory()) {
/*  51 */       directory.mkdirs();
/*     */     }
/*  53 */     File file = new File(directory, String.valueOf(name) + ".yml");
/*  54 */     if (!file.exists()) {
/*  55 */       file.createNewFile();
/*     */     }
/*  57 */     return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
/*     */   }
/*     */   
/*     */   public void saveConfig(FileConfiguration config, String name, String subdirectory, Plugin plugin) throws IOException {
/*  61 */     File dir = new File(plugin.getDataFolder() + File.separator + subdirectory);
/*  62 */     if (!dir.isDirectory()) {
/*  63 */       dir.mkdirs();
/*     */     }
/*  65 */     File file = new File(dir, String.valueOf(name) + ".yml");
/*  66 */     config.save(file);
/*     */   }
/*     */   
/*     */   public void saveConfig(FileConfiguration config, String name, Plugin plugin) throws IOException {
/*  70 */     File dir = plugin.getDataFolder();
/*  71 */     if (!dir.isDirectory()) {
/*  72 */       dir.mkdirs();
/*     */     }
/*  74 */     File file = new File(plugin.getDataFolder(), String.valueOf(name) + ".yml");
/*  75 */     config.save(file);
/*     */   }
/*     */   
/*     */   public void saveConfig(FileConfiguration config, String name, String directory) throws IOException {
/*  79 */     File dir = new File(directory);
/*  80 */     if (!dir.isDirectory()) {
/*  81 */       dir.mkdirs();
/*     */     }
/*  83 */     File file = new File(dir, String.valueOf(name) + ".yml");
/*  84 */     config.save(file);
/*     */   }
/*     */   
/*     */   public void saveConfig(FileConfiguration config, String name, File directory) throws IOException {
/*  88 */     if (!directory.isDirectory()) {
/*  89 */       directory.mkdirs();
/*     */     }
/*  91 */     File file = new File(directory, String.valueOf(name) + ".yml");
/*  92 */     config.save(file);
/*     */   }
/*     */   
/*     */   public FileConfiguration getConfig(String name, String subdirectory, Plugin plugin) {
/*  96 */     File dir = new File(plugin.getDataFolder() + File.separator + subdirectory);
/*  97 */     if (!dir.isDirectory()) {
/*  98 */       return null;
/*     */     }
/* 100 */     File file = new File(dir, String.valueOf(name) + ".yml");
/* 101 */     if (!file.exists()) {
/* 102 */       return null;
/*     */     }
/* 104 */     return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
/*     */   }
/*     */   
/*     */   public FileConfiguration getConfig(String name, Plugin plugin) {
/* 108 */     File dir = plugin.getDataFolder();
/* 109 */     if (!dir.isDirectory()) {
/* 110 */       return null;
/*     */     }
/* 112 */     File file = new File(dir, String.valueOf(name) + ".yml");
/* 113 */     if (!file.exists()) {
/* 114 */       return null;
/*     */     }
/* 116 */     return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
/*     */   }
/*     */   
/*     */   public FileConfiguration getConfig(String name, String directory) {
/* 120 */     File dir = new File(directory);
/* 121 */     if (!dir.isDirectory()) {
/* 122 */       return null;
/*     */     }
/* 124 */     File file = new File(dir, String.valueOf(name) + ".yml");
/* 125 */     if (!file.exists()) {
/* 126 */       return null;
/*     */     }
/* 128 */     return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
/*     */   }
/*     */   
/*     */   public FileConfiguration getConfig(String name, File directory) {
/* 132 */     if (!directory.isDirectory()) {
/* 133 */       return null;
/*     */     }
/* 135 */     File file = new File(directory, String.valueOf(name) + ".yml");
/* 136 */     if (!file.exists()) {
/* 137 */       return null;
/*     */     }
/* 139 */     return (FileConfiguration)YamlConfiguration.loadConfiguration(file);
/*     */   }
/*     */   
/*     */   public FileConfiguration reloadConfig(String name, String subdirectory, Plugin plugin) {
/* 143 */     File dir = new File(plugin.getDataFolder() + File.separator + subdirectory);
/* 144 */     if (!dir.isDirectory()) {
/* 145 */       return null;
/*     */     }
/* 147 */     FileConfiguration config = getConfig(name, subdirectory, plugin);
/*     */     try {
/* 149 */       config.load(new File(dir, String.valueOf(name) + ".yml"));
/* 150 */     } catch (IOException|org.bukkit.configuration.InvalidConfigurationException e) {
/* 151 */       e.printStackTrace();
/*     */     } 
/* 153 */     return config;
/*     */   }
/*     */   
/*     */   public FileConfiguration reloadConfig(String name, Plugin plugin) {
/* 157 */     File dir = plugin.getDataFolder();
/* 158 */     if (!dir.isDirectory()) {
/* 159 */       return null;
/*     */     }
/* 161 */     FileConfiguration config = getConfig(name, plugin);
/*     */     try {
/* 163 */       config.load(new File(dir, String.valueOf(name) + ".yml"));
/* 164 */     } catch (IOException|org.bukkit.configuration.InvalidConfigurationException e) {
/* 165 */       e.printStackTrace();
/*     */     } 
/* 167 */     return config;
/*     */   }
/*     */   
/*     */   public FileConfiguration reloadConfig(String name, String directory) {
/* 171 */     File dir = new File(directory);
/* 172 */     if (!dir.isDirectory()) {
/* 173 */       return null;
/*     */     }
/* 175 */     FileConfiguration config = getConfig(name, directory);
/*     */     try {
/* 177 */       config.load(new File(dir, String.valueOf(name) + ".yml"));
/* 178 */     } catch (IOException|org.bukkit.configuration.InvalidConfigurationException e) {
/* 179 */       e.printStackTrace();
/*     */     } 
/* 181 */     return config;
/*     */   }
/*     */   
/*     */   public FileConfiguration reloadConfig(String name, File directory) {
/* 185 */     if (!directory.isDirectory()) {
/* 186 */       return null;
/*     */     }
/* 188 */     FileConfiguration config = getConfig(name, directory);
/*     */     try {
/* 190 */       config.load(new File(directory, String.valueOf(name) + ".yml"));
/* 191 */     } catch (IOException|org.bukkit.configuration.InvalidConfigurationException e) {
/* 192 */       e.printStackTrace();
/*     */     } 
/* 194 */     return config;
/*     */   }
/*     */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\me\brooky1010\AntiSwear\ConfigManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */