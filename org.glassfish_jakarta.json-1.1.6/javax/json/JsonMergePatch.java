/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import javax.json.JsonValue;

public interface JsonMergePatch {
    public JsonValue apply(JsonValue var1);

    public JsonValue toJsonValue();
}

