/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.NodeStatus
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.cluster.NodeStatus;
import java.util.Map;

public class RemoteNodeStatus
implements NodeStatus {
    private int nodeId;
    private Map<String, String> jvmStats;
    private Map<String, String> props;
    private Map<String, String> buildStats;
    public static final String __PARANAMER_DATA = "<init> int,com.atlassian.confluence.cluster.NodeStatus id,ns \n";

    public RemoteNodeStatus() {
    }

    public RemoteNodeStatus(int id, NodeStatus ns) {
        this.nodeId = id;
        this.jvmStats = ns.getJVMstats();
        this.props = ns.getProps();
        this.buildStats = ns.getBuildStats();
    }

    public int getNodeId() {
        return this.nodeId;
    }

    public Map<String, String> getJVMstats() {
        return this.jvmStats;
    }

    public Map<String, String> getProps() {
        return this.props;
    }

    public Map<String, String> getBuildStats() {
        return this.buildStats;
    }
}

