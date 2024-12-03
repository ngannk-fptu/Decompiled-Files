/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.NodeStatus;
import java.io.Serializable;
import java.util.Map;

public class NodeStatusImpl
implements NodeStatus,
Serializable {
    private final Map<String, String> jvmStats;
    private final Map<String, String> props;
    private final Map<String, String> buildStats;

    public NodeStatusImpl(Map<String, String> jvmStats, Map<String, String> props, Map<String, String> buildStats) {
        this.jvmStats = jvmStats;
        this.props = props;
        this.buildStats = buildStats;
    }

    public NodeStatusImpl(NodeStatus status) {
        this(status.getJVMstats(), status.getProps(), status.getBuildStats());
    }

    @Override
    public Map<String, String> getJVMstats() {
        return this.jvmStats;
    }

    @Override
    public Map<String, String> getProps() {
        return this.props;
    }

    @Override
    public Map<String, String> getBuildStats() {
        return this.buildStats;
    }
}

