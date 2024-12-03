/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

public interface JsonArray
extends JsonStructure,
List<JsonValue> {
    public JsonObject getJsonObject(int var1);

    public JsonArray getJsonArray(int var1);

    public JsonNumber getJsonNumber(int var1);

    public JsonString getJsonString(int var1);

    public <T extends JsonValue> List<T> getValuesAs(Class<T> var1);

    default public <T, K extends JsonValue> List<T> getValuesAs(Function<K, T> func) {
        Stream stream = this.stream();
        return stream.map(func).collect(Collectors.toList());
    }

    public String getString(int var1);

    public String getString(int var1, String var2);

    public int getInt(int var1);

    public int getInt(int var1, int var2);

    public boolean getBoolean(int var1);

    public boolean getBoolean(int var1, boolean var2);

    public boolean isNull(int var1);
}

