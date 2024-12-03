/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.gc;

import com.atlassian.diagnostics.internal.platform.monitor.gc.GCRead;
import java.lang.management.GarbageCollectorMXBean;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

class GCMXBeanPoller {
    private final GarbageCollectorMXBean garbageCollectorMXBean;
    private final Clock clock;

    public GCMXBeanPoller(GarbageCollectorMXBean garbageCollectorMXBean, Clock clock) {
        this.garbageCollectorMXBean = garbageCollectorMXBean;
        this.clock = clock;
    }

    public GCRead doRead() {
        return GCRead.builder().timestamp(Instant.now(this.clock)).collectionTime(Duration.ofMillis(this.garbageCollectorMXBean.getCollectionTime())).collectionCount(this.garbageCollectorMXBean.getCollectionCount()).build();
    }

    public String getGarbageCollectorName() {
        return this.garbageCollectorMXBean.getName();
    }
}

