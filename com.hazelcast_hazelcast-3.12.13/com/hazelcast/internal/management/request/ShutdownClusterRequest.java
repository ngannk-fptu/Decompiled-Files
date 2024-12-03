/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.AsyncConsoleRequest;
import com.hazelcast.logging.ILogger;

public class ShutdownClusterRequest
implements AsyncConsoleRequest {
    @Override
    public int getType() {
        return 36;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject out) throws Exception {
        String resultString = "SUCCESS";
        try {
            mcs.getHazelcastInstance().getCluster().shutdown();
        }
        catch (Exception e) {
            ILogger logger = mcs.getHazelcastInstance().node.nodeEngine.getLogger(this.getClass());
            logger.warning("Cluster can not be shutdown: ", e);
            resultString = e.getMessage();
        }
        JsonObject result = new JsonObject().add("result", resultString);
        out.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
    }
}

