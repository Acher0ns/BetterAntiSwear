/*    */ package mkremins.fanciful;
/*    */ 
/*    */ import com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.bukkit.configuration.serialization.ConfigurationSerializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class JsonString
/*    */   implements JsonRepresentedObject, ConfigurationSerializable
/*    */ {
/*    */   private String _value;
/*    */   
/*    */   public JsonString(CharSequence value) {
/* 20 */     this._value = (value == null) ? null : value.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeJson(JsonWriter writer) throws IOException {
/* 25 */     writer.value(getValue());
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 29 */     return this._value;
/*    */   }
/*    */   
/*    */   public Map<String, Object> serialize() {
/* 33 */     HashMap<String, Object> theSingleValue = new HashMap<>();
/* 34 */     theSingleValue.put("stringValue", this._value);
/* 35 */     return theSingleValue;
/*    */   }
/*    */   
/*    */   public static JsonString deserialize(Map<String, Object> map) {
/* 39 */     return new JsonString(map.get("stringValue").toString());
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 44 */     return this._value;
/*    */   }
/*    */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\mkremins\fanciful\JsonString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */