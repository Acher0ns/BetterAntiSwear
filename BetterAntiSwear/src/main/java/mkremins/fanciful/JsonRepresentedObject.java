package mkremins.fanciful;

import com.google.gson.stream.JsonWriter;
import java.io.IOException;

interface JsonRepresentedObject {
    void writeJson(JsonWriter paramJsonWriter) throws IOException;
}
