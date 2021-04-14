/*    */ package me.BetterAntiSwear;
/*    */ 
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.Event;
/*    */ import org.bukkit.event.HandlerList;
/*    */ 
/*    */ public class EnforcerEvent
/*    */   extends Event {
/*    */   Player p;
/*    */   Type t;
/*    */   
/*    */   public EnforcerEvent(Player p, Type t) {
/* 13 */     this.p = p;
/* 14 */     this.t = t;
/*    */   }
/*    */   
/*    */   public Player getPlayer() {
/* 18 */     return this.p;
/*    */   }
/*    */   
/*    */   public Type getType() {
/* 22 */     return this.t;
/*    */   }
/*    */   
/* 25 */   private static final HandlerList handlers = new HandlerList();
/*    */   
/*    */   public HandlerList getHandlers() {
/* 28 */     return handlers;
/*    */   }
/*    */   
/*    */   public static HandlerList getHandlerList() {
/* 32 */     return handlers;
/*    */   }
/*    */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\me\brooky1010\AntiSwear\EnforcerEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */