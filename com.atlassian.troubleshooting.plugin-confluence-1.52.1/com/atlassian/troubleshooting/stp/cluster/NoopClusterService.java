/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.cluster;

import com.atlassian.troubleshooting.api.ClusterNode;
import com.atlassian.troubleshooting.api.ClusterService;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import javax.annotation.Nonnull;

public class NoopClusterService
implements ClusterService {
    @Override
    public Optional<ClusterNode> getCurrentNode() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getCurrentNodeId() {
        return Optional.empty();
    }

    @Override
    @Nonnull
    public Collection<ClusterNode> getNodes() {
        return Collections.emptyList();
    }
}

