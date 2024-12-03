/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import javax.json.Json;
import javax.json.JsonValue;

public interface JsonStructure
extends JsonValue {
    default public JsonValue getValue(String jsonPointer) {
        return Json.createPointer(jsonPointer).getValue(this);
    }
}

