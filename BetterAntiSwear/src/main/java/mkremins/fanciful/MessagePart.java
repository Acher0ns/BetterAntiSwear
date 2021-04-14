/*     */ package mkremins.fanciful;
/*     */ 
/*     */ import com.google.common.collect.BiMap;
/*     */ import com.google.common.collect.ImmutableBiMap;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.configuration.serialization.ConfigurationSerializable;
/*     */ import org.bukkit.configuration.serialization.ConfigurationSerialization;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class MessagePart
/*     */   implements JsonRepresentedObject, ConfigurationSerializable, Cloneable
/*     */ {
/*  22 */   ChatColor color = ChatColor.WHITE;
/*  23 */   ArrayList<ChatColor> styles = new ArrayList<>();
/*  24 */   String clickActionName = null; String clickActionData = null; String hoverActionName = null;
/*  25 */   JsonRepresentedObject hoverActionData = null;
/*  26 */   TextualComponent text = null;
/*  27 */   String insertionData = null;
/*  28 */   ArrayList<JsonRepresentedObject> translationReplacements = new ArrayList<>(); static final BiMap<ChatColor, String> stylesToNames;
/*     */   
/*     */   MessagePart(TextualComponent text) {
/*  31 */     this.text = text;
/*     */   }
/*     */   
/*     */   MessagePart() {
/*  35 */     this.text = null;
/*     */   }
/*     */   
/*     */   boolean hasText() {
/*  39 */     return (this.text != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MessagePart clone() throws CloneNotSupportedException {
/*  45 */     MessagePart obj = (MessagePart)super.clone();
/*  46 */     obj.styles = (ArrayList<ChatColor>)this.styles.clone();
/*  47 */     if (this.hoverActionData instanceof JsonString) {
/*  48 */       obj.hoverActionData = new JsonString(((JsonString)this.hoverActionData).getValue());
/*  49 */     } else if (this.hoverActionData instanceof FancyMessage) {
/*  50 */       obj.hoverActionData = ((FancyMessage)this.hoverActionData).clone();
/*     */     } 
/*  52 */     obj.translationReplacements = (ArrayList<JsonRepresentedObject>)this.translationReplacements.clone();
/*  53 */     return obj;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  60 */     ImmutableBiMap.Builder<ChatColor, String> builder = ImmutableBiMap.builder(); byte b; int i; ChatColor[] arrayOfChatColor;
/*  61 */     for (i = (arrayOfChatColor = ChatColor.values()).length, b = 0; b < i; ) { ChatColor style = arrayOfChatColor[b];
/*  62 */       if (style.isFormat()) {
/*     */         String styleName;
/*     */ 
/*     */ 
/*     */         
/*  67 */         switch (style) {
/*     */           case MAGIC:
/*  69 */             styleName = "obfuscated";
/*     */             break;
/*     */           case UNDERLINE:
/*  72 */             styleName = "underlined";
/*     */             break;
/*     */           default:
/*  75 */             styleName = style.name().toLowerCase();
/*     */             break;
/*     */         } 
/*     */         
/*  79 */         builder.put(style, styleName);
/*     */       }  b++; }
/*  81 */      stylesToNames = (BiMap<ChatColor, String>)builder.build();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     ConfigurationSerialization.registerClass(MessagePart.class);
/*     */   }
/*     */   
/*     */   public void writeJson(JsonWriter json) {
/*     */     try {
/*     */       json.beginObject();
/*     */       this.text.writeJson(json);
/*     */       json.name("color").value(this.color.name().toLowerCase());
/*     */       for (ChatColor style : this.styles)
/*     */         json.name((String)stylesToNames.get(style)).value(true); 
/*     */       if (this.clickActionName != null && this.clickActionData != null)
/*     */         json.name("clickEvent").beginObject().name("action").value(this.clickActionName).name("value").value(this.clickActionData).endObject(); 
/*     */       if (this.hoverActionName != null && this.hoverActionData != null) {
/*     */         json.name("hoverEvent").beginObject().name("action").value(this.hoverActionName).name("value");
/*     */         this.hoverActionData.writeJson(json);
/*     */         json.endObject();
/*     */       } 
/*     */       if (this.insertionData != null)
/*     */         json.name("insertion").value(this.insertionData); 
/*     */       if (this.translationReplacements.size() > 0 && this.text != null && TextualComponent.isTranslatableText(this.text)) {
/*     */         json.name("with").beginArray();
/*     */         for (JsonRepresentedObject obj : this.translationReplacements)
/*     */           obj.writeJson(json); 
/*     */         json.endArray();
/*     */       } 
/*     */       json.endObject();
/*     */     } catch (IOException e) {
/*     */       Bukkit.getLogger().log(Level.WARNING, "A problem occured during writing of JSON string", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Map<String, Object> serialize() {
/*     */     HashMap<String, Object> map = new HashMap<>();
/*     */     map.put("text", this.text);
/*     */     map.put("styles", this.styles);
/*     */     map.put("color", Character.valueOf(this.color.getChar()));
/*     */     map.put("hoverActionName", this.hoverActionName);
/*     */     map.put("hoverActionData", this.hoverActionData);
/*     */     map.put("clickActionName", this.clickActionName);
/*     */     map.put("clickActionData", this.clickActionData);
/*     */     map.put("insertion", this.insertionData);
/*     */     map.put("translationReplacements", this.translationReplacements);
/*     */     return map;
/*     */   }
/*     */   
/*     */   public static MessagePart deserialize(Map<String, Object> serialized) {
/*     */     MessagePart part = new MessagePart((TextualComponent)serialized.get("text"));
/*     */     part.styles = (ArrayList<ChatColor>)serialized.get("styles");
/*     */     part.color = ChatColor.getByChar(serialized.get("color").toString());
/*     */     part.hoverActionName = (String)serialized.get("hoverActionName");
/*     */     part.hoverActionData = (JsonRepresentedObject)serialized.get("hoverActionData");
/*     */     part.clickActionName = (String)serialized.get("clickActionName");
/*     */     part.clickActionData = (String)serialized.get("clickActionData");
/*     */     part.insertionData = (String)serialized.get("insertion");
/*     */     part.translationReplacements = (ArrayList<JsonRepresentedObject>)serialized.get("translationReplacements");
/*     */     return part;
/*     */   }
/*     */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\mkremins\fanciful\MessagePart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */