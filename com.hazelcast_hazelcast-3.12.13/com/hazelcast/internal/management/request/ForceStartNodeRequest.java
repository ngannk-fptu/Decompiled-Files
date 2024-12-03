/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.logging.ILogger;

public class ForceStartNodeRequest
implements ConsoleRequest {
    public static final String SUCCESS_RESULT = "SUCCESS";
    public static final String FAILED_RESULT = "FAILED";

    @Override
    public int getType() {
        return 37;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject out) throws Exception {
        String resultString;
        HazelcastInstanceImpl instance = mcs.getHazelcastInstance();
        try {
            resultString = instance.node.getNodeExtension().getInternalHotRestartService().triggerForceStart() ? SUCCESS_RESULT : FAILED_RESULT;
        }
        catch (Exception e) {
            ILogger logger = instance.node.getLogger(this.getClass());
            logger.warning("Problem on force start: ", e);
            resultString = e.getMessage();
        }
        JsonObject result = new JsonObject().add("result", resultString);
        out.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
    }
}

