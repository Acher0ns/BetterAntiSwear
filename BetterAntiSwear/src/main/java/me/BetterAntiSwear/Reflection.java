/*    */ package me.BetterAntiSwear;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ import java.lang.reflect.Method;
/*    */ import org.bukkit.Bukkit;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Reflection
/*    */ {
/*    */   public static Class<?> getNMSClass(String name) {
/* 12 */     return getClass("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(23) + "." + name);
/*    */   }
/*    */   
/*    */   public static Class<?> getCraftClass(String name) {
/* 16 */     return getClass("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().substring(23) + "." + name);
/*    */   }
/*    */   
/*    */   public static Class<?> getClass(String name) {
/*    */     try {
/* 21 */       return Class.forName(name);
/*    */     }
/* 23 */     catch (Exception e) {
/* 24 */       e.printStackTrace();
/* 25 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static Object getValue(Object o, String fieldName) {
/*    */     try {
/* 31 */       Field field = o.getClass().getDeclaredField(fieldName);
/* 32 */       if (!field.isAccessible()) {
/* 33 */         field.setAccessible(true);
/*    */       }
/* 35 */       return field.get(o);
/*    */     }
/* 37 */     catch (Exception e) {
/* 38 */       e.printStackTrace();
/* 39 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static Method getMethod(Object o, String methodName, Class... params) {
/*    */     try {
/* 45 */       Method method = o.getClass().getMethod(methodName, params);
/* 46 */       if (!method.isAccessible()) {
/* 47 */         method.setAccessible(true);
/*    */       }
/* 49 */       return method;
/*    */     }
/* 51 */     catch (Exception e) {
/* 52 */       e.printStackTrace();
/* 53 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static Field getField(Field field) {
/* 58 */     field.setAccessible(true);
/* 59 */     return field;
/*    */   }
/*    */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\me\brooky1010\AntiSwear\Reflection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */