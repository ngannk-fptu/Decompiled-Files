/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.confluence.cluster.ClusterManager;
import java.util.Objects;
import java.util.function.Supplier;

public class ShouldCompressAttachmentTextExtraction
implements Supplier<Boolean> {
    private final ClusterManager clusterManager;

    public ShouldCompressAttachmentTextExtraction(ClusterManager clusterManager) {
        this.clusterManager = Objects.requireNonNull(clusterManager);
    }

    @Override
    public Boolean get() {
        return this.clusterManager.isClustered();
    }
}

