/*    */ package me.BetterAntiSwear;
/*    */ 
/*    */ import org.bukkit.Bukkit;
/*    */ 
/*    */ enum ServerPackage
/*    */ {
/*  7 */   MINECRAFT("net.minecraft.server." + getServerVersion()),
/*  8 */   CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion());
/*    */   
/*    */   private final String path;
/*    */   
/*    */   ServerPackage(String path) {
/* 13 */     this.path = path;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 18 */     return this.path;
/*    */   }
/*    */   
/*    */   public Class<?> getClass(String className) throws ClassNotFoundException {
/* 22 */     return Class.forName(String.valueOf(toString()) + "." + className);
/*    */   }
/*    */   
/*    */   public static String getServerVersion() {
/* 26 */     return Bukkit.getServer().getClass().getPackage().getName().substring(23);
/*    */   }
/*    */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\me\brooky1010\AntiSwear\ServerPackage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */