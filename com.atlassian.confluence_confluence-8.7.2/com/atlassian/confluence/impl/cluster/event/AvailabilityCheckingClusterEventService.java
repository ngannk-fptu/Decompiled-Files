/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListenableFuture
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cluster.event;

import com.atlassian.confluence.impl.cluster.event.ClusterEventService;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public final class AvailabilityCheckingClusterEventService
implements ClusterEventService {
    private static final Logger log = LoggerFactory.getLogger(AvailabilityCheckingClusterEventService.class);
    private static final boolean FAIL_IF_PRIMARY_NOT_AVAILABLE = Boolean.getBoolean("confluence.AvailabilityCheckingClusterEventService.failIfPrimaryNotAvailable");
    private final ClusterEventService primary;
    private final ClusterEventService secondary;

    public AvailabilityCheckingClusterEventService(ClusterEventService primary, ClusterEventService secondary) {
        this.primary = Objects.requireNonNull(primary);
        this.secondary = Objects.requireNonNull(secondary);
    }

    private ClusterEventService selectDelegate() {
        if (this.primary.isAvailable()) {
            log.debug("Using primary HazelcastClusterEventService");
            return this.primary;
        }
        if (this.secondary.isAvailable()) {
            if (FAIL_IF_PRIMARY_NOT_AVAILABLE) {
                throw new IllegalStateException("Primary ClusterEventService is not available");
            }
            log.warn("Primary HazelcastClusterEventService is not available, using secondary");
            return this.secondary;
        }
        throw new IllegalStateException("Neither primary nor secondary HazelcastClusterEventService are available");
    }

    @Override
    public ListenableFuture<?> publishEventToCluster(Object event) {
        return this.selectDelegate().publishEventToCluster(event);
    }

    @Override
    public boolean isAvailable() {
        return this.primary.isAvailable() || this.secondary.isAvailable();
    }
}

