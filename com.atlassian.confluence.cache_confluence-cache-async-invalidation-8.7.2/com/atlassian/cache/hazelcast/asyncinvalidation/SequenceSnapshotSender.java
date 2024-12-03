/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceNumber;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceSnapshot;
import com.atlassian.cache.hazelcast.asyncinvalidation.Topic;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

final class SequenceSnapshotSender<K>
implements AutoCloseable {
    private final Topic<SequenceSnapshot<K>> topic;
    private final ConcurrentMap<K, SequenceNumberSource> sequenceNumberSources = new ConcurrentHashMap<K, SequenceNumberSource>();

    SequenceSnapshotSender(Topic<SequenceSnapshot<K>> topic) {
        this.topic = topic;
    }

    private Optional<Map<K, SequenceNumber>> buildSnapshotModel() {
        if (this.sequenceNumberSources.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Maps.transformValues(this.sequenceNumberSources, Supplier::get));
    }

    public void publishSequenceSnapshot() {
        this.buildSequenceSnapshot().ifPresent(this.topic::publish);
    }

    private Optional<SequenceSnapshot<K>> buildSequenceSnapshot() {
        return this.buildSnapshotModel().map(SequenceSnapshot::new);
    }

    @Override
    public void close() {
        this.sequenceNumberSources.clear();
    }

    public void registerSequenceNumberSource(K key, SequenceNumberSource source) {
        this.sequenceNumberSources.put(key, source);
    }

    @FunctionalInterface
    static interface SequenceNumberSource
    extends Supplier<SequenceNumber> {
    }
}

