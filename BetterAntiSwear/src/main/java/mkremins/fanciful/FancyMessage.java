package mkremins.fanciful;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.amoebaman.util.ArrayWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

public class FancyMessage implements JsonRepresentedObject, Cloneable, Iterable<MessagePart>, ConfigurationSerializable {
    private List<MessagePart> messageParts;
    private String jsonString;
    private boolean dirty;
    
    static {
        ConfigurationSerialization.registerClass(FancyMessage.class);
    }

    public FancyMessage clone() throws CloneNotSupportedException {
        FancyMessage instance = (FancyMessage)super.clone();
        instance.messageParts = new ArrayList<>(this.messageParts.size());
        for (int i = 0; i < this.messageParts.size(); i++) {
            instance.messageParts.add(i, ((MessagePart)this.messageParts.get(i)).clone());
        }
        instance.dirty = false;
        instance.jsonString = null;
        return instance;
    }

    public FancyMessage(String firstPartText) {
        this(TextualComponent.rawText(firstPartText));
    }
    
    public FancyMessage(TextualComponent firstPartText) {
        this.messageParts = new ArrayList<>();
        this.messageParts.add(new MessagePart(firstPartText));
        this.jsonString = null;
        this.dirty = false;
    }

    public FancyMessage() {
        this((TextualComponent)null);
    }

    public FancyMessage text(String text) {
        MessagePart latest = latest();
        latest.text = TextualComponent.rawText(text);
        this.dirty = true;
        return this;
    }

    public FancyMessage text(TextualComponent text) {
        MessagePart latest = latest();
        latest.text = text;
        this.dirty = true;
        return this;
    }

    public FancyMessage color(ChatColor color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException(String.valueOf(color.name()) + " is not a color");
        }
        (latest()).color = color;
        this.dirty = true;
        return this;
    }

    public FancyMessage style(ChatColor... styles) {
        byte b;
        int i;
        ChatColor[] arrayOfChatColor;
        for (i = (arrayOfChatColor = styles).length, b = 0; b < i; ) { ChatColor style = arrayOfChatColor[b];
            if (!style.isFormat())
                throw new IllegalArgumentException(String.valueOf(style.name()) + " is not a style"); 
            b++; }
        
        (latest()).styles.addAll(Arrays.asList(styles));
        this.dirty = true;
        return this;
    }

    public FancyMessage file(String path) {
        onClick("open_file", path);
        return this;
    }

    public FancyMessage link(String url) {
        onClick("open_url", url);
        return this;
    }

    public FancyMessage suggest(String command) {
        onClick("suggest_command", command);
        return this;
    }

    public FancyMessage insert(String command) {
        (latest()).insertionData = command;
        this.dirty = true;
        return this;
    }

    public FancyMessage command(String command) {
        onClick("run_command", command);
        return this;
    }

    public FancyMessage achievementTooltip(String name) {
        onHover("show_achievement", new JsonString("achievement." + name));
        return this;
    }

    public FancyMessage tooltip(String text) {
        onHover("show_text", new JsonString(text));
        return this;
    }

    public FancyMessage tooltip(Iterable<String> lines) {
        tooltip((String[])ArrayWrapper.toArray(lines, String.class));
        return this;
    }

    public FancyMessage tooltip(String... lines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            builder.append(lines[i]);
            if (i != lines.length - 1) {
                builder.append('\n');
            }
        } 
        tooltip(builder.toString());
        return this;
    }

    public FancyMessage formattedTooltip(FancyMessage text) {
        for (MessagePart component : text.messageParts) {
            if (component.clickActionData != null && component.clickActionName != null)
                throw new IllegalArgumentException("The tooltip text cannot have click data."); 
            if (component.hoverActionData != null && component.hoverActionName != null) {
                throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
            }
        } 
        onHover("show_text", text);
        return this;
    }

    public FancyMessage formattedTooltip(FancyMessage... lines) {
        if (lines.length < 1) {
            onHover(null, null);
            return this;
        } 
        
        FancyMessage result = new FancyMessage();
        result.messageParts.clear();
        
        for (int i = 0; i < lines.length; i++) {
            try {
                for (MessagePart component : lines[i]) {
                    if (component.clickActionData != null && component.clickActionName != null)
                        throw new IllegalArgumentException("The tooltip text cannot have click data."); 
                    if (component.hoverActionData != null && component.hoverActionName != null) {
                        throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
                    }
                    if (component.hasText()) {
                        result.messageParts.add(component.clone());
                    }
                } 
                if (i != lines.length - 1) {
                    result.messageParts.add(new MessagePart(TextualComponent.rawText("\n")));
                }
            } catch (CloneNotSupportedException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to clone object", e);
                return this;
            } 
        } 
        return formattedTooltip(result.messageParts.isEmpty() ? null : result);
    }

    public FancyMessage formattedTooltip(Iterable<FancyMessage> lines) {
        return formattedTooltip((FancyMessage[])ArrayWrapper.toArray(lines, FancyMessage.class));
    }

    public FancyMessage translationReplacements(String... replacements) {
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = replacements).length, b = 0; b < i; ) { String str = arrayOfString[b];
            (latest()).translationReplacements.add(new JsonString(str)); b++; }
        
        this.dirty = true;
        
        return this;
    }

    public FancyMessage translationReplacements(FancyMessage... replacements) {
        byte b;
        int i;
        FancyMessage[] arrayOfFancyMessage;
        for (i = (arrayOfFancyMessage = replacements).length, b = 0; b < i; ) { FancyMessage str = arrayOfFancyMessage[b];
            (latest()).translationReplacements.add(str);
            b++; }
        
        this.dirty = true;
        
        return this;
    }

    public FancyMessage translationReplacements(Iterable<FancyMessage> replacements) {
        return translationReplacements((FancyMessage[])ArrayWrapper.toArray(replacements, FancyMessage.class));
    }

    public FancyMessage then(String text) {
        return then(TextualComponent.rawText(text));
    }

    public FancyMessage then(TextualComponent text) {
        if (!latest().hasText()) {
            throw new IllegalStateException("previous message part has no text");
        }
        this.messageParts.add(new MessagePart(text));
        this.dirty = true;
        return this;
    }

    public FancyMessage then() {
        if (!latest().hasText()) {
            throw new IllegalStateException("previous message part has no text");
        }
        this.messageParts.add(new MessagePart());
        this.dirty = true;
        return this;
    }
    
    public void writeJson(JsonWriter writer) throws IOException {
        if (this.messageParts.size() == 1) {
            latest().writeJson(writer);
        } else {
            writer.beginObject().name("text").value("").name("extra").beginArray();
            for (MessagePart part : this) {
                part.writeJson(writer);
            }
            writer.endArray().endObject();
        } 
    }

    public String toJSONString() {
        if (!this.dirty && this.jsonString != null) {
            return this.jsonString;
        }
        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter(string);
        try {
            writeJson(json);
            json.close();
        } catch (IOException e) {
            throw new RuntimeException("invalid message");
        } 
        this.jsonString = string.toString();
        this.dirty = false;
        return this.jsonString;
    }

    public void send(Player player) {
        send((CommandSender)player, toJSONString());
    }
    
    private void send(CommandSender sender, String jsonString) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(toOldMessageFormat());
            return;
        } 
        Player player = (Player)sender;
        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + jsonString);
    }

    public void send(CommandSender sender) {
        send(sender, toJSONString());
    }

    public void send(Iterable<? extends CommandSender> senders) {
        String string = toJSONString();
        for (CommandSender sender : senders) {
            send(sender, string);
        }
    }

    public String toOldMessageFormat() {
        StringBuilder result = new StringBuilder();
        for (MessagePart part : this) {
            result.append((part.color == null) ? "" : part.color);
            for (ChatColor formatSpecifier : part.styles) {
                result.append(formatSpecifier);
            }
            result.append(part.text);
        } 
        return result.toString();
    }
    
    private MessagePart latest() {
        return this.messageParts.get(this.messageParts.size() - 1);
    }
    
    private void onClick(String name, String data) {
        MessagePart latest = latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
        this.dirty = true;
    }
    
    private void onHover(String name, JsonRepresentedObject data) {
        MessagePart latest = latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
        this.dirty = true;
    }

    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("messageParts", this.messageParts);
        
        return map;
    }

    public static FancyMessage deserialize(Map<String, Object> serialized) {
        FancyMessage msg = new FancyMessage();
        msg.messageParts = (List<MessagePart>)serialized.get("messageParts");
        msg.jsonString = serialized.containsKey("JSON") ? serialized.get("JSON").toString() : null;
        msg.dirty = !serialized.containsKey("JSON");
        return msg;
    }

    public Iterator<MessagePart> iterator() {
        return this.messageParts.iterator();
    }
    
    private static JsonParser _stringParser = new JsonParser();
    
    public static FancyMessage deserialize(String json) {
        JsonObject serialized = _stringParser.parse(json).getAsJsonObject();
        JsonArray extra = serialized.getAsJsonArray("extra");
        FancyMessage returnVal = new FancyMessage();
        returnVal.messageParts.clear();
        for (JsonElement mPrt : extra) {
            MessagePart component = new MessagePart();
            JsonObject messagePart = mPrt.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)messagePart.entrySet()) {
                
                if (TextualComponent.isTextKey(entry.getKey())) {
                    
                    Map<String, Object> serializedMapForm = new HashMap<>();
                    serializedMapForm.put("key", entry.getKey());
                    if (((JsonElement)entry.getValue()).isJsonPrimitive()) {
                        
                        serializedMapForm.put("value", ((JsonElement)entry.getValue()).getAsString());
                    } else {
                        
                        for (Map.Entry<String, JsonElement> compositeNestedElement : (Iterable<Map.Entry<String, JsonElement>>)((JsonElement)entry.getValue()).getAsJsonObject().entrySet()) {
                            serializedMapForm.put("value." + (String)compositeNestedElement.getKey(), ((JsonElement)compositeNestedElement.getValue()).getAsString());
                        }
                    } 
                    component.text = TextualComponent.deserialize(serializedMapForm); continue;
                }    if (MessagePart.stylesToNames.inverse().containsKey(entry.getKey())) {
                    if (((JsonElement)entry.getValue()).getAsBoolean())
                        component.styles.add((ChatColor)MessagePart.stylesToNames.inverse().get(entry.getKey()));    continue;
                } 
                if (((String)entry.getKey()).equals("color")) {
                    component.color = ChatColor.valueOf(((JsonElement)entry.getValue()).getAsString().toUpperCase()); continue;
                }    if (((String)entry.getKey()).equals("clickEvent")) {
                    JsonObject object = ((JsonElement)entry.getValue()).getAsJsonObject();
                    component.clickActionName = object.get("action").getAsString();
                    component.clickActionData = object.get("value").getAsString(); continue;
                }    if (((String)entry.getKey()).equals("hoverEvent")) {
                    JsonObject object = ((JsonElement)entry.getValue()).getAsJsonObject();
                    component.hoverActionName = object.get("action").getAsString();
                    if (object.get("value").isJsonPrimitive()) {
                        
                        component.hoverActionData = new JsonString(object.get("value").getAsString());
                        
                        continue;
                    } 
                    
                    component.hoverActionData = deserialize(object.get("value").toString()); continue;
                } 
                if (((String)entry.getKey()).equals("insertion")) {
                    component.insertionData = ((JsonElement)entry.getValue()).getAsString(); continue;
                }    if (((String)entry.getKey()).equals("with")) {
                    for (JsonElement object : ((JsonElement)entry.getValue()).getAsJsonArray()) {
                        if (object.isJsonPrimitive()) {
                            component.translationReplacements.add(new JsonString(object.getAsString()));
                            
                            continue;
                        } 
                        component.translationReplacements.add(deserialize(object.toString()));
                    } 
                }
            } 
            
            returnVal.messageParts.add(component);
        } 
        return returnVal;
    }
}
