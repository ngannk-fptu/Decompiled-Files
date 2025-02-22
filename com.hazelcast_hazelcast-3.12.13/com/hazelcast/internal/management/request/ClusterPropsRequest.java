/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.internal.partition.InternalPartitionService;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class ClusterPropsRequest
implements ConsoleRequest {
    @Override
    public int getType() {
        return 9;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject root) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        InternalPartitionService partitionService = mcs.getHazelcastInstance().node.getPartitionService();
        JsonObject properties = new JsonObject();
        properties.add("hazelcast.cl_version", mcs.getHazelcastInstance().node.getBuildInfo().getVersion());
        properties.add("date.cl_startTime", Long.toString(runtimeMxBean.getStartTime()));
        properties.add("seconds.cl_upTime", Long.toString(runtimeMxBean.getUptime()));
        properties.add("memory.cl_freeMemory", Long.toString(runtime.freeMemory()));
        properties.add("memory.cl_totalMemory", Long.toString(runtime.totalMemory()));
        properties.add("memory.cl_maxMemory", Long.toString(runtime.maxMemory()));
        properties.add("return.hasOngoingMigration", Boolean.toString(partitionService.hasOnGoingMigration()));
        properties.add("data.cl_migrationTasksCount", Long.toString(partitionService.getMigrationQueueSize()));
        root.add("result", properties);
    }

    @Override
    public void fromJson(JsonObject json) {
    }
}

