/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.ManagedCache
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.hazelcast.asyncinvalidation.ClusterNode;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceNumber;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class CacheInvalidationSequenceSnapshotVerifier {
    private static final Logger log = LoggerFactory.getLogger(CacheInvalidationSequenceSnapshotVerifier.class);
    private final ManagedCache cache;
    private final Runnable onFlush;
    private final SequenceTracker<ClusterNode> sequenceTracker;

    public CacheInvalidationSequenceSnapshotVerifier(ManagedCache cache, Runnable onFlush, SequenceTracker<ClusterNode> sequenceTracker) {
        this.cache = cache;
        this.onFlush = onFlush;
        this.sequenceTracker = sequenceTracker;
    }

    public void verifyMinimumSequenceNumber(ClusterNode clusterNode, SequenceNumber minimumSequenceNumber) {
        if (this.sequenceTracker.verifyMinimumSequenceNumber(clusterNode, minimumSequenceNumber)) {
            log.debug("Minimum sequence number {} for {} was verified for cache '{}'", new Object[]{minimumSequenceNumber, clusterNode, this.cache.getName()});
        } else {
            log.warn("Minimum sequence number {} for {} was not met, flushing cache '{}'", new Object[]{minimumSequenceNumber, clusterNode, this.cache.getName()});
            this.cache.clear();
            this.onFlush.run();
        }
    }
}

