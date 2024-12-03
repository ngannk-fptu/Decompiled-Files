/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.hazelcast.asyncinvalidation.ClusterNode;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceNumber;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceSnapshot;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SequenceSnapshotReceiver<K>
implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(SequenceSnapshotReceiver.class);
    private final ConcurrentMap<K, SequenceNumberConsumer> sequenceNumberConsumers = new ConcurrentHashMap<K, SequenceNumberConsumer>();

    SequenceSnapshotReceiver() {
    }

    public void processSequenceSnapshot(ClusterNode member, SequenceSnapshot<K> sequenceSnapshot) {
        sequenceSnapshot.getSequenceNumbers().forEach((key, expectedMinimumSequenceNumber) -> this.verifyMinimumCacheSequenceNumber((K)key, member, (SequenceNumber)expectedMinimumSequenceNumber));
    }

    private void verifyMinimumCacheSequenceNumber(K key, ClusterNode member, SequenceNumber expectedMinimumSequenceNumber) {
        SequenceNumberConsumer sequenceNumberConsumer = (SequenceNumberConsumer)this.sequenceNumberConsumers.get(key);
        if (sequenceNumberConsumer != null) {
            sequenceNumberConsumer.accept(member, expectedMinimumSequenceNumber);
        } else {
            log.debug("Nothing registered to receive sequence snapshot for '{}'", key);
        }
    }

    @Override
    public void close() {
        this.sequenceNumberConsumers.clear();
    }

    public void registerSequenceSnapshotConsumer(K key, SequenceNumberConsumer consumer) {
        this.sequenceNumberConsumers.put(key, consumer);
    }

    @FunctionalInterface
    static interface SequenceNumberConsumer
    extends BiConsumer<ClusterNode, SequenceNumber> {
    }
}

