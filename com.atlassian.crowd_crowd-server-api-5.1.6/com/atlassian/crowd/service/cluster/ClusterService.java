/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.service.cluster;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.service.cluster.ClusterInformation;
import com.atlassian.crowd.service.cluster.ClusterNode;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface ClusterService {
    public boolean isAvailable();

    @Nonnull
    public String getNodeId();

    @ExperimentalApi
    public Optional<ClusterNode> getClusterNode();

    @Nonnull
    public ClusterInformation getInformation();
}

