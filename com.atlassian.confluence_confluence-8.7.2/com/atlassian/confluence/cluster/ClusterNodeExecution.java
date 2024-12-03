/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterNodeInformation;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClusterNodeExecution<T> {
    private final ClusterNodeInformation clusterNode;
    private final CompletionStage<T> completionStage;

    public ClusterNodeExecution(@Nullable ClusterNodeInformation clusterNode, @Nonnull CompletionStage<T> completionStage) {
        this.clusterNode = clusterNode;
        this.completionStage = Objects.requireNonNull(completionStage);
    }

    @Nullable
    public ClusterNodeInformation getClusterNode() {
        return this.clusterNode;
    }

    public CompletionStage<T> getCompletionStage() {
        return this.completionStage;
    }
}

