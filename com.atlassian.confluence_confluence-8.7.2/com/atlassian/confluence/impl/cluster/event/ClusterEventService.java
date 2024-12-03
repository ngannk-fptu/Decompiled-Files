/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListenableFuture
 *  javax.annotation.CheckReturnValue
 */
package com.atlassian.confluence.impl.cluster.event;

import com.google.common.util.concurrent.ListenableFuture;
import javax.annotation.CheckReturnValue;

public interface ClusterEventService {
    @CheckReturnValue
    public ListenableFuture<?> publishEventToCluster(Object var1);

    @Deprecated(forRemoval=true)
    default public boolean isAvailable() {
        return true;
    }
}

