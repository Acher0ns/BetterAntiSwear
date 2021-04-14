package mkremins.fanciful;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;




final class MessagePart
    implements JsonRepresentedObject, ConfigurationSerializable, Cloneable
{
    ChatColor color = ChatColor.WHITE;
    ArrayList<ChatColor> styles = new ArrayList<>();
    String clickActionName = null; String clickActionData = null; String hoverActionName = null;
    JsonRepresentedObject hoverActionData = null;
    TextualComponent text = null;
    String insertionData = null;
    ArrayList<JsonRepresentedObject> translationReplacements = new ArrayList<>(); static final BiMap<ChatColor, String> stylesToNames;
    
    MessagePart(TextualComponent text) {
        this.text = text;
    }
    
    MessagePart() {
        this.text = null;
    }
    
    boolean hasText() {
        return (this.text != null);
    }


    
    public MessagePart clone() throws CloneNotSupportedException {
        MessagePart obj = (MessagePart)super.clone();
        obj.styles = (ArrayList<ChatColor>)this.styles.clone();
        if (this.hoverActionData instanceof JsonString) {
            obj.hoverActionData = new JsonString(((JsonString)this.hoverActionData).getValue());
        } else if (this.hoverActionData instanceof FancyMessage) {
            obj.hoverActionData = ((FancyMessage)this.hoverActionData).clone();
        } 
        obj.translationReplacements = (ArrayList<JsonRepresentedObject>)this.translationReplacements.clone();
        return obj;
    }



    
    static {
        ImmutableBiMap.Builder<ChatColor, String> builder = ImmutableBiMap.builder(); byte b; int i; ChatColor[] arrayOfChatColor;
        for (i = (arrayOfChatColor = ChatColor.values()).length, b = 0; b < i; ) { ChatColor style = arrayOfChatColor[b];
            if (style.isFormat()) {
                String styleName;


                
                switch (style) {
                    case MAGIC:
                        styleName = "obfuscated";
                        break;
                    case UNDERLINE:
                        styleName = "underlined";
                        break;
                    default:
                        styleName = style.name().toLowerCase();
                        break;
                } 
                
                builder.put(style, styleName);
            }    b++; }
         stylesToNames = (BiMap<ChatColor, String>)builder.build();





































































        
        ConfigurationSerialization.registerClass(MessagePart.class);
    }
    
    public void writeJson(JsonWriter json) {
        try {
            json.beginObject();
            this.text.writeJson(json);
            json.name("color").value(this.color.name().toLowerCase());
            for (ChatColor style : this.styles)
                json.name((String)stylesToNames.get(style)).value(true); 
            if (this.clickActionName != null && this.clickActionData != null)
                json.name("clickEvent").beginObject().name("action").value(this.clickActionName).name("value").value(this.clickActionData).endObject(); 
            if (this.hoverActionName != null && this.hoverActionData != null) {
                json.name("hoverEvent").beginObject().name("action").value(this.hoverActionName).name("value");
                this.hoverActionData.writeJson(json);
                json.endObject();
            } 
            if (this.insertionData != null)
                json.name("insertion").value(this.insertionData); 
            if (this.translationReplacements.size() > 0 && this.text != null && TextualComponent.isTranslatableText(this.text)) {
                json.name("with").beginArray();
                for (JsonRepresentedObject obj : this.translationReplacements)
                    obj.writeJson(json); 
                json.endArray();
            } 
            json.endObject();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "A problem occured during writing of JSON string", e);
        } 
    }
    
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("text", this.text);
        map.put("styles", this.styles);
        map.put("color", Character.valueOf(this.color.getChar()));
        map.put("hoverActionName", this.hoverActionName);
        map.put("hoverActionData", this.hoverActionData);
        map.put("clickActionName", this.clickActionName);
        map.put("clickActionData", this.clickActionData);
        map.put("insertion", this.insertionData);
        map.put("translationReplacements", this.translationReplacements);
        return map;
    }
    
    public static MessagePart deserialize(Map<String, Object> serialized) {
        MessagePart part = new MessagePart((TextualComponent)serialized.get("text"));
        part.styles = (ArrayList<ChatColor>)serialized.get("styles");
        part.color = ChatColor.getByChar(serialized.get("color").toString());
        part.hoverActionName = (String)serialized.get("hoverActionName");
        part.hoverActionData = (JsonRepresentedObject)serialized.get("hoverActionData");
        part.clickActionName = (String)serialized.get("clickActionName");
        part.clickActionData = (String)serialized.get("clickActionData");
        part.insertionData = (String)serialized.get("insertion");
        part.translationReplacements = (ArrayList<JsonRepresentedObject>)serialized.get("translationReplacements");
        return part;
    }
}
