/*
 * Decompiled with CFR 0.152.
 */
package javax.json;

import javax.json.JsonPatch;
import javax.json.JsonValue;

public interface JsonPatchBuilder {
    public JsonPatchBuilder add(String var1, JsonValue var2);

    public JsonPatchBuilder add(String var1, String var2);

    public JsonPatchBuilder add(String var1, int var2);

    public JsonPatchBuilder add(String var1, boolean var2);

    public JsonPatchBuilder remove(String var1);

    public JsonPatchBuilder replace(String var1, JsonValue var2);

    public JsonPatchBuilder replace(String var1, String var2);

    public JsonPatchBuilder replace(String var1, int var2);

    public JsonPatchBuilder replace(String var1, boolean var2);

    public JsonPatchBuilder move(String var1, String var2);

    public JsonPatchBuilder copy(String var1, String var2);

    public JsonPatchBuilder test(String var1, JsonValue var2);

    public JsonPatchBuilder test(String var1, String var2);

    public JsonPatchBuilder test(String var1, int var2);

    public JsonPatchBuilder test(String var1, boolean var2);

    public JsonPatch build();
}

