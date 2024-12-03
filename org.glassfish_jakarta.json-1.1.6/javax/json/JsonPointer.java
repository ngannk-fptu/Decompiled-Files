/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import javax.json.JsonStructure;
import javax.json.JsonValue;

public interface JsonPointer {
    public <T extends JsonStructure> T add(T var1, JsonValue var2);

    public <T extends JsonStructure> T remove(T var1);

    public <T extends JsonStructure> T replace(T var1, JsonValue var2);

    public boolean containsValue(JsonStructure var1);

    public JsonValue getValue(JsonStructure var1);
}

