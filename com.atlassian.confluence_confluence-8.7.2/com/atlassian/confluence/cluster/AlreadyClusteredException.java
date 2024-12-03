/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterInformation;

public class AlreadyClusteredException
extends ClusterException {
    private final ClusterInformation clusterInformation;

    public AlreadyClusteredException(ClusterInformation clusterInformation) {
        this.clusterInformation = clusterInformation;
    }

    @Override
    public String getMessage() {
        return "Already a member of an existing cluster (" + this.clusterInformation + ")";
    }
}

