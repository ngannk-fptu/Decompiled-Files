/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.Cluster;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.AsyncConsoleRequest;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.JsonUtil;

public class ChangeClusterStateRequest
implements AsyncConsoleRequest {
    private static final String FAILURE = "FAILURE: ";
    private String state;

    public ChangeClusterStateRequest() {
    }

    public ChangeClusterStateRequest(String state) {
        this.state = state;
    }

    @Override
    public int getType() {
        return 35;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject out) throws Exception {
        String resultString = "SUCCESS";
        try {
            Cluster cluster = mcs.getHazelcastInstance().getCluster();
            cluster.changeClusterState(ChangeClusterStateRequest.getClusterState(this.state));
        }
        catch (Exception e) {
            ILogger logger = mcs.getHazelcastInstance().node.nodeEngine.getLogger(this.getClass());
            logger.warning("Cluster state can not be changed: ", e);
            resultString = FAILURE + e.getMessage();
        }
        JsonObject result = new JsonObject().add("result", resultString);
        out.add("result", result);
    }

    private static ClusterState getClusterState(String state) {
        return ClusterState.valueOf(state);
    }

    @Override
    public void fromJson(JsonObject json) {
        this.state = JsonUtil.getString(json, "state");
    }
}

