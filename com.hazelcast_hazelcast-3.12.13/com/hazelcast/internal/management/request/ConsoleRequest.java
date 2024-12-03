/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;

public interface ConsoleRequest {
    public int getType();

    public void writeResponse(ManagementCenterService var1, JsonObject var2) throws Exception;

    public void fromJson(JsonObject var1);
}

