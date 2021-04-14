package me.BetterAntiSwear;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EnforcerEvent extends Event {
    Player p;
    Type t;
    
    public EnforcerEvent(Player p, Type t) {
        this.p = p;
        this.t = t;
    }
    
    public Player getPlayer() {
        return this.p;
    }
    
    public Type getType() {
        return this.t;
    }
    
    private static final HandlerList handlers = new HandlerList();
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
