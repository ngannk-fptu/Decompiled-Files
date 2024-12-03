/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.internal.json.JsonObject;

public interface JsonSerializable {
    public JsonObject toJson();

    public void fromJson(JsonObject var1);
}

