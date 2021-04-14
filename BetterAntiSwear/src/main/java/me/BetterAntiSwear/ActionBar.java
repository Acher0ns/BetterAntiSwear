package me.BetterAntiSwear;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;



public class ActionBar
{
    private static Class<?> craftPlayer;
    private static Class<?> chatPacket;
    private static Class<?> chatComponentText;
    private static Class<?> chatBaseComponent;
    private static Class<?> chatMessageType;
    private static Class<?> packet;
    private static Method getHandle;
    private static Constructor<?> constructChatPacket;
    private static Constructor<?> constructChatText;
    private static boolean invoked;
    
    public ActionBar() {
        if (!invoked) {
            try {
                craftPlayer = Reflection.getCraftClass("entity.CraftPlayer");
                chatPacket = Reflection.getNMSClass("PacketPlayOutChat");
                packet = Reflection.getNMSClass("Packet");
                chatBaseComponent = Reflection.getNMSClass("IChatBaseComponent");
                chatComponentText = Reflection.getNMSClass("ChatComponentText");
                if (versionId >= 12) {
                    chatMessageType = Reflection.getNMSClass("ChatMessageType");
                    constructChatPacket = chatPacket.getConstructor(new Class[] { chatBaseComponent });
                } else {
                    constructChatPacket = chatPacket.getConstructor(new Class[] { chatBaseComponent, byte.class });
                } 
                constructChatText = chatComponentText.getConstructor(new Class[] { String.class });
                getHandle = craftPlayer.getDeclaredMethod("getHandle", new Class[0]);
            }
            catch (Exception e) {
                e.printStackTrace();
            } 
            invoked = true;
        } 
    }
    
    public ActionBar setMessage(Object message) {
        this.message = String.valueOf(message);
        return this; } public ActionBar send(Player... players) {
        try {
            byte b;
            int i;
            Player[] arrayOfPlayer;
            for (i = (arrayOfPlayer = players).length, b = 0; b < i; ) { Object data; Player player = arrayOfPlayer[b];
                
                Object text = constructChatText.newInstance(new Object[] { this.message });
                if (versionId >= 12) {
                    data = constructChatPacket.newInstance(new Object[] { text });
                    Field field = data.getClass().getDeclaredField("b");
                    field.setAccessible(true);
                    field.set(data, Reflection.getField(chatMessageType.getDeclaredField("GAME_INFO")).get(null));
                } else {
                    data = constructChatPacket.newInstance(new Object[] { text, Byte.valueOf((byte)2) });
                } 
                Object handle = getHandle.invoke(player, new Object[0]);
                Object connection = Reflection.getValue(handle, "playerConnection");
                Method send = Reflection.getMethod(connection, "sendPacket", new Class[] { packet });
                send.invoke(connection, new Object[] { data });
                b++; }
        
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return this;
    }

    
    private static int versionId = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].replace(".", "#").split("#")[1]);
    private String message;
}
