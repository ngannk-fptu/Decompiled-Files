/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

public interface JsonObjectBuilder {
    public JsonObjectBuilder add(String var1, JsonValue var2);

    public JsonObjectBuilder add(String var1, String var2);

    public JsonObjectBuilder add(String var1, BigInteger var2);

    public JsonObjectBuilder add(String var1, BigDecimal var2);

    public JsonObjectBuilder add(String var1, int var2);

    public JsonObjectBuilder add(String var1, long var2);

    public JsonObjectBuilder add(String var1, double var2);

    public JsonObjectBuilder add(String var1, boolean var2);

    public JsonObjectBuilder addNull(String var1);

    public JsonObjectBuilder add(String var1, JsonObjectBuilder var2);

    public JsonObjectBuilder add(String var1, JsonArrayBuilder var2);

    default public JsonObjectBuilder addAll(JsonObjectBuilder builder) {
        throw new UnsupportedOperationException();
    }

    default public JsonObjectBuilder remove(String name) {
        throw new UnsupportedOperationException();
    }

    public JsonObject build();
}

