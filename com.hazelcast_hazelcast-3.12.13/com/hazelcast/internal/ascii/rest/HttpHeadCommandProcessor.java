/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeState;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.rest.HttpCommandProcessor;
import com.hazelcast.internal.ascii.rest.HttpHeadCommand;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.partition.InternalPartitionService;
import java.util.LinkedHashMap;

public class HttpHeadCommandProcessor
extends HttpCommandProcessor<HttpHeadCommand> {
    public HttpHeadCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
    }

    @Override
    public void handle(HttpHeadCommand command) {
        String uri = command.getURI();
        if (uri.startsWith("/hazelcast/rest/maps/")) {
            command.send200();
        } else if (uri.startsWith("/hazelcast/rest/queues/")) {
            command.send200();
        } else if (uri.startsWith("/hazelcast/rest/cluster")) {
            command.send200();
        } else if (uri.equals("/hazelcast/health")) {
            this.handleHealthcheck(command);
        } else if (uri.startsWith("/hazelcast/rest/management/cluster/version")) {
            command.send200();
        } else {
            command.send404();
        }
        this.textCommandService.sendResponse(command);
    }

    private void handleHealthcheck(HttpHeadCommand command) {
        Node node = this.textCommandService.getNode();
        NodeState nodeState = node.getState();
        ClusterServiceImpl clusterService = node.getClusterService();
        ClusterState clusterState = clusterService.getClusterState();
        int clusterSize = clusterService.getMembers().size();
        InternalPartitionService partitionService = node.getPartitionService();
        long migrationQueueSize = partitionService.getMigrationQueueSize();
        LinkedHashMap<String, Object> headervals = new LinkedHashMap<String, Object>();
        headervals.put("NodeState", (Object)nodeState);
        headervals.put("ClusterState", (Object)clusterState);
        headervals.put("MigrationQueueSize", migrationQueueSize);
        headervals.put("ClusterSize", clusterSize);
        command.setResponse(headervals);
    }

    @Override
    public void handleRejection(HttpHeadCommand command) {
        this.handle(command);
    }
}

