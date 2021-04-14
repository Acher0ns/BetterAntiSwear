/*    */ package me.BetterAntiSwear;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Field;
/*    */ import java.lang.reflect.Method;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ActionBar
/*    */ {
/*    */   private static Class<?> craftPlayer;
/*    */   private static Class<?> chatPacket;
/*    */   private static Class<?> chatComponentText;
/*    */   private static Class<?> chatBaseComponent;
/*    */   private static Class<?> chatMessageType;
/*    */   private static Class<?> packet;
/*    */   private static Method getHandle;
/*    */   private static Constructor<?> constructChatPacket;
/*    */   private static Constructor<?> constructChatText;
/*    */   private static boolean invoked;
/*    */   
/*    */   public ActionBar() {
/* 25 */     if (!invoked) {
/*    */       try {
/* 27 */         craftPlayer = Reflection.getCraftClass("entity.CraftPlayer");
/* 28 */         chatPacket = Reflection.getNMSClass("PacketPlayOutChat");
/* 29 */         packet = Reflection.getNMSClass("Packet");
/* 30 */         chatBaseComponent = Reflection.getNMSClass("IChatBaseComponent");
/* 31 */         chatComponentText = Reflection.getNMSClass("ChatComponentText");
/* 32 */         if (versionId >= 12) {
/* 33 */           chatMessageType = Reflection.getNMSClass("ChatMessageType");
/* 34 */           constructChatPacket = chatPacket.getConstructor(new Class[] { chatBaseComponent });
/*    */         } else {
/* 36 */           constructChatPacket = chatPacket.getConstructor(new Class[] { chatBaseComponent, byte.class });
/*    */         } 
/* 38 */         constructChatText = chatComponentText.getConstructor(new Class[] { String.class });
/* 39 */         getHandle = craftPlayer.getDeclaredMethod("getHandle", new Class[0]);
/*    */       }
/* 41 */       catch (Exception e) {
/* 42 */         e.printStackTrace();
/*    */       } 
/* 44 */       invoked = true;
/*    */     } 
/*    */   }
/*    */   
/*    */   public ActionBar setMessage(Object message) {
/* 49 */     this.message = String.valueOf(message);
/* 50 */     return this; } public ActionBar send(Player... players) {
/*    */     try {
/*    */       byte b;
/*    */       int i;
/*    */       Player[] arrayOfPlayer;
/* 55 */       for (i = (arrayOfPlayer = players).length, b = 0; b < i; ) { Object data; Player player = arrayOfPlayer[b];
/*    */         
/* 57 */         Object text = constructChatText.newInstance(new Object[] { this.message });
/* 58 */         if (versionId >= 12) {
/* 59 */           data = constructChatPacket.newInstance(new Object[] { text });
/* 60 */           Field field = data.getClass().getDeclaredField("b");
/* 61 */           field.setAccessible(true);
/* 62 */           field.set(data, Reflection.getField(chatMessageType.getDeclaredField("GAME_INFO")).get(null));
/*    */         } else {
/* 64 */           data = constructChatPacket.newInstance(new Object[] { text, Byte.valueOf((byte)2) });
/*    */         } 
/* 66 */         Object handle = getHandle.invoke(player, new Object[0]);
/* 67 */         Object connection = Reflection.getValue(handle, "playerConnection");
/* 68 */         Method send = Reflection.getMethod(connection, "sendPacket", new Class[] { packet });
/* 69 */         send.invoke(connection, new Object[] { data });
/*    */         b++; }
/*    */     
/* 72 */     } catch (Exception e) {
/* 73 */       e.printStackTrace();
/*    */     } 
/* 75 */     return this;
/*    */   }
/*    */ 
/*    */   
/* 79 */   private static int versionId = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].replace(".", "#").split("#")[1]);
/*    */   private String message;
/*    */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\me\brooky1010\AntiSwear\ActionBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */