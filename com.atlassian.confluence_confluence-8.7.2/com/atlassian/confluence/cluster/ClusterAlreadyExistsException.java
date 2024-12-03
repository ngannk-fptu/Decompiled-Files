/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.cluster.NamedClusterException;

public class ClusterAlreadyExistsException
extends NamedClusterException {
    public ClusterAlreadyExistsException(String clusterName, ClusterJoinConfig joinConfig) {
        super(clusterName, joinConfig);
    }

    @Override
    protected String getMessageTemplate() {
        return "There already an active cluster with the name {0} ({1})";
    }
}

