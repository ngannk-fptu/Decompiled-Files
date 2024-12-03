/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.crowd.service.cluster;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.Internal;
import com.atlassian.crowd.service.cluster.ClusterNodeDetails;
import java.time.Instant;
import java.util.Optional;

public interface ClusterNode {
    public String getNodeId();

    public String getNodeName();

    @Internal
    public Instant getLastHeartbeat();

    public boolean isLocal();

    @ExperimentalApi
    public Optional<ClusterNodeDetails> getDetails();
}

