package mkremins.fanciful;

import com.google.common.base.Preconditions;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public abstract class TextualComponent implements Cloneable {
    static {
        ConfigurationSerialization.registerClass(ArbitraryTextTypeComponent.class);
        ConfigurationSerialization.registerClass(ComplexTextTypeComponent.class);
    }
    
    public String toString() {
        return getReadableString();
    }

    static TextualComponent deserialize(Map<String, Object> map) {
        if (map.containsKey("key") && map.size() == 2 && map.containsKey("value"))
        {
            return ArbitraryTextTypeComponent.deserialize(map); } 
        if (map.size() >= 2 && map.containsKey("key") && !map.containsKey("value"))
        {
            return ComplexTextTypeComponent.deserialize(map);
        }
        
        return null;
    }
    
    static boolean isTextKey(String key) {
        return !(!key.equals("translate") && !key.equals("text") && !key.equals("score") && !key.equals("selector"));
    }
    
    static boolean isTranslatableText(TextualComponent component) {
        return (component instanceof ComplexTextTypeComponent && ((ComplexTextTypeComponent)component).getKey().equals("translate"));
    }
    
    private static final class ArbitraryTextTypeComponent extends TextualComponent implements ConfigurationSerializable {
        private String _key;
        private String _value;
        
        public ArbitraryTextTypeComponent(String key, String value) {
            setKey(key);
            setValue(value);
        }

        public String getKey() {
            return this._key;
        }
        
        public void setKey(String key) {
            Preconditions.checkArgument((key != null && !key.isEmpty()), "The key must be specified.");
            this._key = key;
        }
        
        public String getValue() {
            return this._value;
        }
        
        public void setValue(String value) {
            Preconditions.checkArgument((value != null), "The value must be specified.");
            this._value = value;
        }

        public TextualComponent clone() throws CloneNotSupportedException {
            return new ArbitraryTextTypeComponent(getKey(), getValue());
        }
        
        public void writeJson(JsonWriter writer) throws IOException {
            writer.name(getKey()).value(getValue());
        }
        
        public Map<String, Object> serialize() {
            return new HashMap<String, Object>()
                {
                
                };
        }
        
        public static ArbitraryTextTypeComponent deserialize(Map<String, Object> map) {
            return new ArbitraryTextTypeComponent(map.get("key").toString(), map.get("value").toString());
        }
        
        public String getReadableString() {
            return getValue();
        }
    }
    
    private static final class ComplexTextTypeComponent
        extends TextualComponent
        implements ConfigurationSerializable {
        private String _key;
        private Map<String, String> _value;
        
        public ComplexTextTypeComponent(String key, Map<String, String> values) {
            setKey(key);
            setValue(values);
        }
        
        public String getKey() {
            return this._key;
        }
        
        public void setKey(String key) {
            Preconditions.checkArgument((key != null && !key.isEmpty()), "The key must be specified.");
            this._key = key;
        }
        
        public Map<String, String> getValue() {
            return this._value;
        }
        
        public void setValue(Map<String, String> value) {
            Preconditions.checkArgument((value != null), "The value must be specified.");
            this._value = value;
        }
        
        public TextualComponent clone() throws CloneNotSupportedException {
            return new ComplexTextTypeComponent(getKey(), getValue());
        }
        
        public void writeJson(JsonWriter writer) throws IOException {
            writer.name(getKey());
            writer.beginObject();
            for (Map.Entry<String, String> jsonPair : this._value.entrySet()) {
                writer.name(jsonPair.getKey()).value(jsonPair.getValue());
            }
            writer.endObject();
        }
        
        public Map<String, Object> serialize() {
            return new HashMap<String, Object>()
                {
                
                };
        }
        
        public static ComplexTextTypeComponent deserialize(Map<String, Object> map) {
            String key = null;
            Map<String, String> value = new HashMap<>();
            for (Map.Entry<String, Object> valEntry : map.entrySet()) {
                if (((String)valEntry.getKey()).equals("key")) {
                    key = (String)valEntry.getValue(); continue;
                }    if (((String)valEntry.getKey()).startsWith("value.")) {
                    value.put(((String)valEntry.getKey()).substring(6), valEntry.getValue().toString());
                }
            } 
            return new ComplexTextTypeComponent(key, value);
        }

        public String getReadableString() {
            return getKey();
        }
    }

    public static TextualComponent rawText(String textValue) {
        return new ArbitraryTextTypeComponent("text", textValue);
    }

    public static TextualComponent localizedText(String translateKey) {
        return new ArbitraryTextTypeComponent("translate", translateKey);
    }
    
    private static void throwUnsupportedSnapshot() {
        throw new UnsupportedOperationException("This feature is only supported in snapshot releases.");
    }

    public static TextualComponent objectiveScore(String scoreboardObjective) {
        return objectiveScore("*", scoreboardObjective);
    }

    public static TextualComponent objectiveScore(String playerName, String scoreboardObjective) {
        throwUnsupportedSnapshot();
        
        return null;
    }

    public static TextualComponent selector(String selector) {
        throwUnsupportedSnapshot();
        return new ArbitraryTextTypeComponent("selector", selector);
    }
    
    public abstract String getKey();
    public abstract String getReadableString();
    public abstract TextualComponent clone() throws CloneNotSupportedException;
    public abstract void writeJson(JsonWriter paramJsonWriter) throws IOException;
}
