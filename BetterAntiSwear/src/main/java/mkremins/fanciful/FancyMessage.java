/*     */ package mkremins.fanciful;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonParser;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Level;
/*     */ import net.amoebaman.util.ArrayWrapper;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.configuration.serialization.ConfigurationSerializable;
/*     */ import org.bukkit.configuration.serialization.ConfigurationSerialization;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FancyMessage
/*     */   implements JsonRepresentedObject, Cloneable, Iterable<MessagePart>, ConfigurationSerializable
/*     */ {
/*     */   private List<MessagePart> messageParts;
/*     */   private String jsonString;
/*     */   private boolean dirty;
/*     */   
/*     */   static {
/*  40 */     ConfigurationSerialization.registerClass(FancyMessage.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage clone() throws CloneNotSupportedException {
/*  49 */     FancyMessage instance = (FancyMessage)super.clone();
/*  50 */     instance.messageParts = new ArrayList<>(this.messageParts.size());
/*  51 */     for (int i = 0; i < this.messageParts.size(); i++) {
/*  52 */       instance.messageParts.add(i, ((MessagePart)this.messageParts.get(i)).clone());
/*     */     }
/*  54 */     instance.dirty = false;
/*  55 */     instance.jsonString = null;
/*  56 */     return instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage(String firstPartText) {
/*  65 */     this(TextualComponent.rawText(firstPartText));
/*     */   }
/*     */   
/*     */   public FancyMessage(TextualComponent firstPartText) {
/*  69 */     this.messageParts = new ArrayList<>();
/*  70 */     this.messageParts.add(new MessagePart(firstPartText));
/*  71 */     this.jsonString = null;
/*  72 */     this.dirty = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage() {
/*  79 */     this((TextualComponent)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage text(String text) {
/*  89 */     MessagePart latest = latest();
/*  90 */     latest.text = TextualComponent.rawText(text);
/*  91 */     this.dirty = true;
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage text(TextualComponent text) {
/* 102 */     MessagePart latest = latest();
/* 103 */     latest.text = text;
/* 104 */     this.dirty = true;
/* 105 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage color(ChatColor color) {
/* 116 */     if (!color.isColor()) {
/* 117 */       throw new IllegalArgumentException(String.valueOf(color.name()) + " is not a color");
/*     */     }
/* 119 */     (latest()).color = color;
/* 120 */     this.dirty = true;
/* 121 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage style(ChatColor... styles) {
/*     */     byte b;
/*     */     int i;
/*     */     ChatColor[] arrayOfChatColor;
/* 132 */     for (i = (arrayOfChatColor = styles).length, b = 0; b < i; ) { ChatColor style = arrayOfChatColor[b];
/* 133 */       if (!style.isFormat())
/* 134 */         throw new IllegalArgumentException(String.valueOf(style.name()) + " is not a style"); 
/*     */       b++; }
/*     */     
/* 137 */     (latest()).styles.addAll(Arrays.asList(styles));
/* 138 */     this.dirty = true;
/* 139 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage file(String path) {
/* 149 */     onClick("open_file", path);
/* 150 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage link(String url) {
/* 160 */     onClick("open_url", url);
/* 161 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage suggest(String command) {
/* 172 */     onClick("suggest_command", command);
/* 173 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage insert(String command) {
/* 184 */     (latest()).insertionData = command;
/* 185 */     this.dirty = true;
/* 186 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage command(String command) {
/* 197 */     onClick("run_command", command);
/* 198 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage achievementTooltip(String name) {
/* 209 */     onHover("show_achievement", new JsonString("achievement." + name));
/* 210 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage tooltip(String text) {
/* 221 */     onHover("show_text", new JsonString(text));
/* 222 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage tooltip(Iterable<String> lines) {
/* 233 */     tooltip((String[])ArrayWrapper.toArray(lines, String.class));
/* 234 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage tooltip(String... lines) {
/* 245 */     StringBuilder builder = new StringBuilder();
/* 246 */     for (int i = 0; i < lines.length; i++) {
/* 247 */       builder.append(lines[i]);
/* 248 */       if (i != lines.length - 1) {
/* 249 */         builder.append('\n');
/*     */       }
/*     */     } 
/* 252 */     tooltip(builder.toString());
/* 253 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage formattedTooltip(FancyMessage text) {
/* 264 */     for (MessagePart component : text.messageParts) {
/* 265 */       if (component.clickActionData != null && component.clickActionName != null)
/* 266 */         throw new IllegalArgumentException("The tooltip text cannot have click data."); 
/* 267 */       if (component.hoverActionData != null && component.hoverActionName != null) {
/* 268 */         throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
/*     */       }
/*     */     } 
/* 271 */     onHover("show_text", text);
/* 272 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage formattedTooltip(FancyMessage... lines) {
/* 283 */     if (lines.length < 1) {
/* 284 */       onHover(null, null);
/* 285 */       return this;
/*     */     } 
/*     */     
/* 288 */     FancyMessage result = new FancyMessage();
/* 289 */     result.messageParts.clear();
/*     */     
/* 291 */     for (int i = 0; i < lines.length; i++) {
/*     */       try {
/* 293 */         for (MessagePart component : lines[i]) {
/* 294 */           if (component.clickActionData != null && component.clickActionName != null)
/* 295 */             throw new IllegalArgumentException("The tooltip text cannot have click data."); 
/* 296 */           if (component.hoverActionData != null && component.hoverActionName != null) {
/* 297 */             throw new IllegalArgumentException("The tooltip text cannot have a tooltip.");
/*     */           }
/* 299 */           if (component.hasText()) {
/* 300 */             result.messageParts.add(component.clone());
/*     */           }
/*     */         } 
/* 303 */         if (i != lines.length - 1) {
/* 304 */           result.messageParts.add(new MessagePart(TextualComponent.rawText("\n")));
/*     */         }
/* 306 */       } catch (CloneNotSupportedException e) {
/* 307 */         Bukkit.getLogger().log(Level.WARNING, "Failed to clone object", e);
/* 308 */         return this;
/*     */       } 
/*     */     } 
/* 311 */     return formattedTooltip(result.messageParts.isEmpty() ? null : result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage formattedTooltip(Iterable<FancyMessage> lines) {
/* 322 */     return formattedTooltip((FancyMessage[])ArrayWrapper.toArray(lines, FancyMessage.class));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage translationReplacements(String... replacements) {
/*     */     byte b;
/*     */     int i;
/*     */     String[] arrayOfString;
/* 332 */     for (i = (arrayOfString = replacements).length, b = 0; b < i; ) { String str = arrayOfString[b];
/* 333 */       (latest()).translationReplacements.add(new JsonString(str)); b++; }
/*     */     
/* 335 */     this.dirty = true;
/*     */     
/* 337 */     return this;
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
/*     */   public FancyMessage translationReplacements(FancyMessage... replacements) {
/*     */     byte b;
/*     */     int i;
/*     */     FancyMessage[] arrayOfFancyMessage;
/* 363 */     for (i = (arrayOfFancyMessage = replacements).length, b = 0; b < i; ) { FancyMessage str = arrayOfFancyMessage[b];
/* 364 */       (latest()).translationReplacements.add(str);
/*     */       b++; }
/*     */     
/* 367 */     this.dirty = true;
/*     */     
/* 369 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage translationReplacements(Iterable<FancyMessage> replacements) {
/* 379 */     return translationReplacements((FancyMessage[])ArrayWrapper.toArray(replacements, FancyMessage.class));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage then(String text) {
/* 390 */     return then(TextualComponent.rawText(text));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage then(TextualComponent text) {
/* 401 */     if (!latest().hasText()) {
/* 402 */       throw new IllegalStateException("previous message part has no text");
/*     */     }
/* 404 */     this.messageParts.add(new MessagePart(text));
/* 405 */     this.dirty = true;
/* 406 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FancyMessage then() {
/* 416 */     if (!latest().hasText()) {
/* 417 */       throw new IllegalStateException("previous message part has no text");
/*     */     }
/* 419 */     this.messageParts.add(new MessagePart());
/* 420 */     this.dirty = true;
/* 421 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeJson(JsonWriter writer) throws IOException {
/* 426 */     if (this.messageParts.size() == 1) {
/* 427 */       latest().writeJson(writer);
/*     */     } else {
/* 429 */       writer.beginObject().name("text").value("").name("extra").beginArray();
/* 430 */       for (MessagePart part : this) {
/* 431 */         part.writeJson(writer);
/*     */       }
/* 433 */       writer.endArray().endObject();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toJSONString() {
/* 444 */     if (!this.dirty && this.jsonString != null) {
/* 445 */       return this.jsonString;
/*     */     }
/* 447 */     StringWriter string = new StringWriter();
/* 448 */     JsonWriter json = new JsonWriter(string);
/*     */     try {
/* 450 */       writeJson(json);
/* 451 */       json.close();
/* 452 */     } catch (IOException e) {
/* 453 */       throw new RuntimeException("invalid message");
/*     */     } 
/* 455 */     this.jsonString = string.toString();
/* 456 */     this.dirty = false;
/* 457 */     return this.jsonString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(Player player) {
/* 466 */     send((CommandSender)player, toJSONString());
/*     */   }
/*     */   
/*     */   private void send(CommandSender sender, String jsonString) {
/* 470 */     if (!(sender instanceof Player)) {
/* 471 */       sender.sendMessage(toOldMessageFormat());
/*     */       return;
/*     */     } 
/* 474 */     Player player = (Player)sender;
/* 475 */     Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + jsonString);
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
/*     */   public void send(CommandSender sender) {
/* 487 */     send(sender, toJSONString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(Iterable<? extends CommandSender> senders) {
/* 497 */     String string = toJSONString();
/* 498 */     for (CommandSender sender : senders) {
/* 499 */       send(sender, string);
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
/*     */   public String toOldMessageFormat() {
/* 521 */     StringBuilder result = new StringBuilder();
/* 522 */     for (MessagePart part : this) {
/* 523 */       result.append((part.color == null) ? "" : part.color);
/* 524 */       for (ChatColor formatSpecifier : part.styles) {
/* 525 */         result.append(formatSpecifier);
/*     */       }
/* 527 */       result.append(part.text);
/*     */     } 
/* 529 */     return result.toString();
/*     */   }
/*     */   
/*     */   private MessagePart latest() {
/* 533 */     return this.messageParts.get(this.messageParts.size() - 1);
/*     */   }
/*     */   
/*     */   private void onClick(String name, String data) {
/* 537 */     MessagePart latest = latest();
/* 538 */     latest.clickActionName = name;
/* 539 */     latest.clickActionData = data;
/* 540 */     this.dirty = true;
/*     */   }
/*     */   
/*     */   private void onHover(String name, JsonRepresentedObject data) {
/* 544 */     MessagePart latest = latest();
/* 545 */     latest.hoverActionName = name;
/* 546 */     latest.hoverActionData = data;
/* 547 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, Object> serialize() {
/* 552 */     HashMap<String, Object> map = new HashMap<>();
/* 553 */     map.put("messageParts", this.messageParts);
/*     */     
/* 555 */     return map;
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
/*     */   public static FancyMessage deserialize(Map<String, Object> serialized) {
/* 567 */     FancyMessage msg = new FancyMessage();
/* 568 */     msg.messageParts = (List<MessagePart>)serialized.get("messageParts");
/* 569 */     msg.jsonString = serialized.containsKey("JSON") ? serialized.get("JSON").toString() : null;
/* 570 */     msg.dirty = !serialized.containsKey("JSON");
/* 571 */     return msg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<MessagePart> iterator() {
/* 578 */     return this.messageParts.iterator();
/*     */   }
/*     */   
/* 581 */   private static JsonParser _stringParser = new JsonParser();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FancyMessage deserialize(String json) {
/* 591 */     JsonObject serialized = _stringParser.parse(json).getAsJsonObject();
/* 592 */     JsonArray extra = serialized.getAsJsonArray("extra");
/* 593 */     FancyMessage returnVal = new FancyMessage();
/* 594 */     returnVal.messageParts.clear();
/* 595 */     for (JsonElement mPrt : extra) {
/* 596 */       MessagePart component = new MessagePart();
/* 597 */       JsonObject messagePart = mPrt.getAsJsonObject();
/* 598 */       for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)messagePart.entrySet()) {
/*     */         
/* 600 */         if (TextualComponent.isTextKey(entry.getKey())) {
/*     */           
/* 602 */           Map<String, Object> serializedMapForm = new HashMap<>();
/* 603 */           serializedMapForm.put("key", entry.getKey());
/* 604 */           if (((JsonElement)entry.getValue()).isJsonPrimitive()) {
/*     */             
/* 606 */             serializedMapForm.put("value", ((JsonElement)entry.getValue()).getAsString());
/*     */           } else {
/*     */             
/* 609 */             for (Map.Entry<String, JsonElement> compositeNestedElement : (Iterable<Map.Entry<String, JsonElement>>)((JsonElement)entry.getValue()).getAsJsonObject().entrySet()) {
/* 610 */               serializedMapForm.put("value." + (String)compositeNestedElement.getKey(), ((JsonElement)compositeNestedElement.getValue()).getAsString());
/*     */             }
/*     */           } 
/* 613 */           component.text = TextualComponent.deserialize(serializedMapForm); continue;
/* 614 */         }  if (MessagePart.stylesToNames.inverse().containsKey(entry.getKey())) {
/* 615 */           if (((JsonElement)entry.getValue()).getAsBoolean())
/* 616 */             component.styles.add((ChatColor)MessagePart.stylesToNames.inverse().get(entry.getKey()));  continue;
/*     */         } 
/* 618 */         if (((String)entry.getKey()).equals("color")) {
/* 619 */           component.color = ChatColor.valueOf(((JsonElement)entry.getValue()).getAsString().toUpperCase()); continue;
/* 620 */         }  if (((String)entry.getKey()).equals("clickEvent")) {
/* 621 */           JsonObject object = ((JsonElement)entry.getValue()).getAsJsonObject();
/* 622 */           component.clickActionName = object.get("action").getAsString();
/* 623 */           component.clickActionData = object.get("value").getAsString(); continue;
/* 624 */         }  if (((String)entry.getKey()).equals("hoverEvent")) {
/* 625 */           JsonObject object = ((JsonElement)entry.getValue()).getAsJsonObject();
/* 626 */           component.hoverActionName = object.get("action").getAsString();
/* 627 */           if (object.get("value").isJsonPrimitive()) {
/*     */             
/* 629 */             component.hoverActionData = new JsonString(object.get("value").getAsString());
/*     */             
/*     */             continue;
/*     */           } 
/*     */           
/* 634 */           component.hoverActionData = deserialize(object.get("value").toString()); continue;
/*     */         } 
/* 636 */         if (((String)entry.getKey()).equals("insertion")) {
/* 637 */           component.insertionData = ((JsonElement)entry.getValue()).getAsString(); continue;
/* 638 */         }  if (((String)entry.getKey()).equals("with")) {
/* 639 */           for (JsonElement object : ((JsonElement)entry.getValue()).getAsJsonArray()) {
/* 640 */             if (object.isJsonPrimitive()) {
/* 641 */               component.translationReplacements.add(new JsonString(object.getAsString()));
/*     */               
/*     */               continue;
/*     */             } 
/* 645 */             component.translationReplacements.add(deserialize(object.toString()));
/*     */           } 
/*     */         }
/*     */       } 
/*     */       
/* 650 */       returnVal.messageParts.add(component);
/*     */     } 
/* 652 */     return returnVal;
/*     */   }
/*     */ }


/* Location:              C:\Users\Kamron\Downloads\AntiSwear.jar!\mkremins\fanciful\FancyMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */