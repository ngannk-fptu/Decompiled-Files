/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.EvictingQueue
 */
package com.atlassian.diagnostics.internal.platform.monitor.gc;

import com.atlassian.diagnostics.internal.platform.monitor.gc.GCRead;
import com.google.common.collect.EvictingQueue;
import java.time.Instant;
import java.util.Optional;
import java.util.Queue;

public class GCReadsStore {
    private final Queue<GCRead> gcReads;

    public GCReadsStore(int maxQueueSize) {
        this.gcReads = EvictingQueue.create((int)maxQueueSize);
    }

    public void storeRead(GCRead gcRead) {
        this.gcReads.add(gcRead);
    }

    public Optional<GCRead> getReadIfHappenedBefore(Instant timestamp) {
        GCRead read = this.gcReads.peek();
        if (read != null && this.readHappenedBefore(read, timestamp)) {
            this.gcReads.remove();
            return Optional.of(read);
        }
        return Optional.empty();
    }

    private boolean readHappenedBefore(GCRead read, Instant timestamp) {
        return timestamp.compareTo(read.getTimestamp()) >= 0;
    }
}

