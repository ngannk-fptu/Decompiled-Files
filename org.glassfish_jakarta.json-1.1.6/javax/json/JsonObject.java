/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

public interface JsonObject
extends JsonStructure,
Map<String, JsonValue> {
    public JsonArray getJsonArray(String var1);

    public JsonObject getJsonObject(String var1);

    public JsonNumber getJsonNumber(String var1);

    public JsonString getJsonString(String var1);

    public String getString(String var1);

    public String getString(String var1, String var2);

    public int getInt(String var1);

    public int getInt(String var1, int var2);

    public boolean getBoolean(String var1);

    public boolean getBoolean(String var1, boolean var2);

    public boolean isNull(String var1);
}

