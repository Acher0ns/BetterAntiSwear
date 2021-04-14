/*     */ package mkremins.fanciful;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.bukkit.configuration.serialization.ConfigurationSerializable;
/*     */ import org.bukkit.configuration.serialization.ConfigurationSerialization;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TextualComponent
/*     */   implements Cloneable
/*     */ {
/*     */   static {
/*  22 */     ConfigurationSerialization.registerClass(ArbitraryTextTypeComponent.class);
/*  23 */     ConfigurationSerialization.registerClass(ComplexTextTypeComponent.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  28 */     return getReadableString();
/*     */   }
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
/*     */   static TextualComponent deserialize(Map<String, Object> map) {
/*  58 */     if (map.containsKey("key") && map.size() == 2 && map.containsKey("value"))
/*     */     {
/*  60 */       return ArbitraryTextTypeComponent.deserialize(map); } 
/*  61 */     if (map.size() >= 2 && map.containsKey("key") && !map.containsKey("value"))
/*     */     {
/*  63 */       return ComplexTextTypeComponent.deserialize(map);
/*     */     }
/*     */     
/*  66 */     return null;
/*     */   }
/*     */   
/*     */   static boolean isTextKey(String key) {
/*  70 */     return !(!key.equals("translate") && !key.equals("text") && !key.equals("score") && !key.equals("selector"));
/*     */   }
/*     */   
/*     */   static boolean isTranslatableText(TextualComponent component) {
/*  74 */     return (component instanceof ComplexTextTypeComponent && ((ComplexTextTypeComponent)component).getKey().equals("translate"));
/*     */   }
/*     */   
/*     */   private static final class ArbitraryTextTypeComponent
/*     */     extends TextualComponent
/*     */     implements ConfigurationSerializable {
/*     */     private String _key;
/*     */     private String _value;
/*     */     
/*     */     public ArbitraryTextTypeComponent(String key, String value) {
/*  84 */       setKey(key);
/*  85 */       setValue(value);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/*  90 */       return this._key;
/*     */     }
/*     */     
/*     */     public void setKey(String key) {
/*  94 */       Preconditions.checkArgument((key != null && !key.isEmpty()), "The key must be specified.");
/*  95 */       this._key = key;
/*     */     }
/*     */     
/*     */     public String getValue() {
/*  99 */       return this._value;
/*     */     }
/*     */     
/*     */     public void setValue(String value) {
/* 103 */       Preconditions.checkArgument((value != null), "The value must be specified.");
/* 104 */       this._value = value;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TextualComponent clone() throws CloneNotSupportedException {
/* 113 */       return new ArbitraryTextTypeComponent(getKey(), getValue());
/*     */     }
/*     */ 
/*     */     
/*     */     public void writeJson(JsonWriter writer) throws IOException {
/* 118 */       writer.name(getKey()).value(getValue());
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Object> serialize() {
/* 123 */       return new HashMap<String, Object>()
/*     */         {
/*     */         
/*     */         };
/*     */     }
/*     */     
/*     */     public static ArbitraryTextTypeComponent deserialize(Map<String, Object> map) {
/* 130 */       return new ArbitraryTextTypeComponent(map.get("key").toString(), map.get("value").toString());
/*     */     }
/*     */ 
/*     */     
/*     */     public String getReadableString() {
/* 135 */       return getValue();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ComplexTextTypeComponent
/*     */     extends TextualComponent
/*     */     implements ConfigurationSerializable {
/*     */     private String _key;
/*     */     private Map<String, String> _value;
/*     */     
/*     */     public ComplexTextTypeComponent(String key, Map<String, String> values) {
/* 146 */       setKey(key);
/* 147 */       setValue(values);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getKey() {
/* 152 */       return this._key;
/*     */     }
/*     */     
/*     */     public void setKey(String key) {
/* 156 */       Preconditions.checkArgument((key != null && !key.isEmpty()), "The key must be specified.");
/* 157 */       this._key = key;
/*     */     }
/*     */     
/*     */     public Map<String, String> getValue() {
/* 161 */       return this._value;
/*     */     }
/*     */     
/*     */     public void setValue(Map<String, String> value) {
/* 165 */       Preconditions.checkArgument((value != null), "The value must be specified.");
/* 166 */       this._value = value;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TextualComponent clone() throws CloneNotSupportedException {
/* 175 */       return new ComplexTextTypeComponent(getKey(), getValue());
/*     */     }
/*     */ 
/*     */     
/*     */     public void writeJson(JsonWriter writer) throws IOException {
/* 180 */       writer.name(getKey());
/* 181 */       writer.beginObject();
/* 182 */       for (Map.Entry<String, String> jsonPair : this._value.entrySet()) {
/* 183 */         writer.name(jsonPair.getKey()).value(jsonPair.getValue());
/*     */       }
/* 185 */       writer.endObject();
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Object> serialize() {
/* 190 */       return new HashMap<String, Object>()
/*     */         {
/*     */         
/*     */         };
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public static ComplexTextTypeComponent deserialize(Map<String, Object> map) {
/* 199 */       String key = null;
/* 200 */       Map<String, String> value = new HashMap<>();
/* 201 */       for (Map.Entry<String, Object> valEntry : map.entrySet()) {
/* 202 */         if (((String)valEntry.getKey()).equals("key")) {
/* 203 */           key = (String)valEntry.getValue(); continue;
/* 204 */         }  if (((String)valEntry.getKey()).startsWith("value.")) {
/* 205 */           value.put(((String)valEntry.getKey()).substring(6), valEntry.getValue().toString());
/*     */         }
/*     */       } 
/* 208 */       return new ComplexTextTypeComponent(key, value);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getReadableString() {
/* 213 */       return getKey();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TextualComponent rawText(String textValue) {
/* 225 */     return new ArbitraryTextTypeComponent("text", textValue);
/*     */   }
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
/*     */   public static TextualComponent localizedText(String translateKey) {
/* 240 */     return new ArbitraryTextTypeComponent("translate", translateKey);
/*     */   }
/*     */   
/*     */   private static void throwUnsupportedSnapshot() {
/* 244 */     throw new UnsupportedOperationException("This feature is only supported in snapshot releases.");
/*     */   }
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
/*     */   public static TextualComponent objectiveScore(String scoreboardObjective) {
/* 258 */     return objectiveScore("*", scoreboardObjective);
/*     */   }
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
/*     */   public static TextualComponent objectiveScore(String playerName, String scoreboardObjective) {
/* 274 */     throwUnsupportedSnapshot();
/*     */     
/* 276 */     return null;
/*     */   }
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
/*     */   public static TextualComponent selector(String selector) {
/* 293 */     throwUnsupportedSnapshot();
/*     */     
/* 295 */     return new ArbitraryTextTypeComponent("selector", selector);
/*     */   }
/*     */   
/*     */   public abstract String getKey();
/*     */   
/*     */   public abstract String getReadableString();
/*     */   
/*     */   public abstract TextualComponent clone() throws CloneNotSupportedException;
/*     */   
/*     */   public abstract void writeJson(JsonWriter paramJsonWriter) throws IOException;
/*     */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\mkremins\fanciful\TextualComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */