/*
 * Decompiled with CFR 0.152.
 */
package javax.json.stream;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;

public interface JsonParserFactory {
    public JsonParser createParser(Reader var1);

    public JsonParser createParser(InputStream var1);

    public JsonParser createParser(InputStream var1, Charset var2);

    public JsonParser createParser(JsonObject var1);

    public JsonParser createParser(JsonArray var1);

    public Map<String, ?> getConfigInUse();
}

