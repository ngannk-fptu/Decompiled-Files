/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import java.util.Collection;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public interface JsonBuilderFactory {
    public JsonObjectBuilder createObjectBuilder();

    default public JsonObjectBuilder createObjectBuilder(JsonObject object) {
        throw new UnsupportedOperationException();
    }

    default public JsonObjectBuilder createObjectBuilder(Map<String, Object> object) {
        throw new UnsupportedOperationException();
    }

    public JsonArrayBuilder createArrayBuilder();

    default public JsonArrayBuilder createArrayBuilder(JsonArray array) {
        throw new UnsupportedOperationException();
    }

    default public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    public Map<String, ?> getConfigInUse();
}

