package me.BetterAntiSwear;

import org.bukkit.Bukkit;

enum ServerPackage
{
    MINECRAFT("net.minecraft.server." + getServerVersion()),
    CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion());
    
    private final String path;
    
    ServerPackage(String path) {
        this.path = path;
    }

    
    public String toString() {
        return this.path;
    }
    
    public Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(String.valueOf(toString()) + "." + className);
    }
    
    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }
}
