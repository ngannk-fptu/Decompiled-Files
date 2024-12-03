/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.ConsoleRequest;

public class GetClusterStateRequest
implements ConsoleRequest {
    @Override
    public int getType() {
        return 34;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject out) throws Exception {
        ClusterState clusterState = mcs.getHazelcastInstance().getCluster().getClusterState();
        JsonObject result = new JsonObject();
        result.add("result", clusterState.toString());
        out.add("result", result);
    }

    @Override
    public void fromJson(JsonObject json) {
    }
}

