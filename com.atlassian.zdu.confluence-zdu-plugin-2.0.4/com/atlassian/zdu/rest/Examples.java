/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.rest;

import com.atlassian.zdu.rest.dto.Cluster;
import com.atlassian.zdu.rest.dto.ClusterState;
import com.atlassian.zdu.rest.dto.ClusterStateResponse;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import com.atlassian.zdu.rest.dto.NodeState;
import java.util.Arrays;

public class Examples {
    public static final NodeInfoDTO NODE = NodeInfoDTO.builder().id("7a52c813").name("node-1").ipAddress("10.0.0.1").buildNumber("1234").version("1.0").state(NodeState.ACTIVE).tasksTotal(5).activeUserCount(12).local(true).build();
    public static Cluster CLUSTER_STABLE = new Cluster(false, true, ClusterState.STABLE, null, Arrays.asList(NODE));
    public static Cluster CLUSTER_READY_TO_UPGRADE = new Cluster(true, true, ClusterState.READY_TO_UPGRADE, "1.0", Arrays.asList(NODE));
    public static ClusterStateResponse CLUSTER_STATE_STABLE = new ClusterStateResponse(ClusterState.STABLE, NODE);
}

