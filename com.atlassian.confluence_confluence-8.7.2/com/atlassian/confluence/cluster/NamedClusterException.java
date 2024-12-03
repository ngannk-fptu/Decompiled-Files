/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import java.text.MessageFormat;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class NamedClusterException
extends ClusterException {
    private final ClusterJoinConfig clusterJoinConfig;
    private final String clusterName;

    public NamedClusterException(String clusterName, @Nullable ClusterJoinConfig clusterJoinConfig) {
        this.clusterJoinConfig = clusterJoinConfig;
        this.clusterName = clusterName;
    }

    public ClusterJoinConfig getClusterJoinConfig() {
        return this.clusterJoinConfig;
    }

    public String getClusterName() {
        return this.clusterName;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format(this.getMessageTemplate(), this.clusterName, this.clusterJoinConfig);
    }

    protected abstract String getMessageTemplate();
}

