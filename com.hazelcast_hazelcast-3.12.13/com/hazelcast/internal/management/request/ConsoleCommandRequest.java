/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ConsoleCommandHandler;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.util.JsonUtil;

public class ConsoleCommandRequest
implements ConsoleRequest {
    private String command;

    @Override
    public int getType() {
        return 5;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject root) throws Exception {
        ConsoleCommandHandler handler = mcs.getCommandHandler();
        JsonObject result = new JsonObject();
        try {
            String output = handler.handleCommand(this.command);
            result.add("output", output);
        }
        catch (Throwable e) {
            result.add("output", "Error: " + e.getClass().getSimpleName() + "[" + e.getMessage() + "]");
        }
        root.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
        this.command = JsonUtil.getString(json, "command", "");
    }
}

