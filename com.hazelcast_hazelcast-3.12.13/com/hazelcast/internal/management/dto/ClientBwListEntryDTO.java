/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.util.JsonUtil;

public class ClientBwListEntryDTO
implements JsonSerializable {
    public Type type;
    public String value;

    public ClientBwListEntryDTO() {
    }

    public ClientBwListEntryDTO(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("type", this.type.toString());
        root.add("value", this.value);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        String typeStr = JsonUtil.getString(json, "type");
        this.type = Type.valueOf(typeStr);
        this.value = JsonUtil.getString(json, "value");
    }

    public static enum Type {
        IP_ADDRESS,
        INSTANCE_NAME,
        LABEL;

    }
}

