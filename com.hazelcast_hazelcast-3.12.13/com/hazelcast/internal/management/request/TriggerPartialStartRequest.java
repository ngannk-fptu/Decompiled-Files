/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.hotrestart.InternalHotRestartService;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.ConsoleRequest;

public class TriggerPartialStartRequest
implements ConsoleRequest {
    public static final String SUCCESS_RESULT = "SUCCESS";
    public static final String FAILED_RESULT = "FAILED";

    @Override
    public int getType() {
        return 39;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject out) throws Exception {
        Node node = mcs.getHazelcastInstance().node;
        InternalHotRestartService hotRestartService = node.getNodeExtension().getInternalHotRestartService();
        boolean done = hotRestartService.triggerPartialStart();
        String result = done ? SUCCESS_RESULT : FAILED_RESULT;
        out.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
    }
}

