/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.Cache;
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidation;
import com.atlassian.cache.hazelcast.asyncinvalidation.ClusterNode;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class CacheInvalidationReceiver<K> {
    private static final Logger log = LoggerFactory.getLogger(CacheInvalidationReceiver.class);
    private final Cache<K, ?> cache;
    private final SequenceTracker<ClusterNode> sequenceTracker;
    private final Runnable onFlush;

    public CacheInvalidationReceiver(Cache<K, ?> cache, SequenceTracker<ClusterNode> sequenceTracker, Runnable onFlush) {
        this.cache = cache;
        this.sequenceTracker = sequenceTracker;
        this.onFlush = onFlush;
    }

    public void processInvalidation(ClusterNode sentBy, CacheInvalidation<K> invalidation) {
        if (this.sequenceTracker.verifyNextInSequence(sentBy, invalidation.getSequenceNumber())) {
            log.debug("Invalidation from {} with sequence number {} accepted, processing the invalidation for cache '{}'", new Object[]{sentBy, invalidation.getSequenceNumber(), this.cache.getName()});
            invalidation.accept(this.cache);
        } else {
            log.debug("Invalidation from {} had out-of-sequence number {} accepted, flushing cache '{}'", new Object[]{sentBy, invalidation.getSequenceNumber(), this.cache.getName()});
            this.onFlush.run();
        }
    }
}

