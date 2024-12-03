/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.api;

import com.atlassian.troubleshooting.api.ClusterNode;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public interface ClusterService {
    public Optional<ClusterNode> getCurrentNode();

    public Optional<String> getCurrentNodeId();

    default public boolean isClustered() {
        return this.getCurrentNodeId().isPresent();
    }

    @Nonnull
    default public Set<String> getNodeIds() {
        return this.getNodes().stream().map(ClusterNode::getId).collect(Collectors.toSet());
    }

    @Nonnull
    public Collection<ClusterNode> getNodes();

    default public Optional<Integer> getNodeCount() {
        return this.isClustered() ? Optional.of(this.getNodeIds().size()) : Optional.empty();
    }
}

