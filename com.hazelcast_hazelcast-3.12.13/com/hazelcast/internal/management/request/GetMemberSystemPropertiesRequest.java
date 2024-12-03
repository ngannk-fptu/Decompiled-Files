/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.ConsoleRequest;
import java.util.Map;
import java.util.Properties;

public class GetMemberSystemPropertiesRequest
implements ConsoleRequest {
    @Override
    public int getType() {
        return 13;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject root) {
        Properties properties = System.getProperties();
        JsonObject result = new JsonObject();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            result.add(entry.getKey().toString(), entry.getValue().toString());
        }
        root.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
    }
}

