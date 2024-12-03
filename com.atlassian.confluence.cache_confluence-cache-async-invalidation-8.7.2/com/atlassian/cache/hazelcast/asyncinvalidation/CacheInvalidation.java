/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.google.errorprone.annotations.Immutable
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.Cache;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceNumber;
import com.google.errorprone.annotations.Immutable;
import java.io.Serializable;
import java.util.function.Consumer;

interface CacheInvalidation<K>
extends Serializable,
Consumer<Cache<K, ?>> {
    public SequenceNumber getSequenceNumber();

    public static <K extends Serializable> CacheInvalidation<K> entryInvalidation(K key, SequenceNumber sequenceNumber) {
        return new InvalidateEntry<K>(key, sequenceNumber);
    }

    public static <K> CacheInvalidation<K> allEntriesInvalidation(SequenceNumber sequenceNumber) {
        return new InvalidateAllEntries(sequenceNumber);
    }

    @Immutable
    public static final class InvalidateAllEntries<K>
    implements CacheInvalidation<K> {
        private final SequenceNumber sequenceNumber;

        InvalidateAllEntries(SequenceNumber sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        @Override
        public SequenceNumber getSequenceNumber() {
            return this.sequenceNumber;
        }

        @Override
        public void accept(Cache<K, ?> cache) {
            cache.removeAll();
        }

        public String toString() {
            return "InvalidateAllEntries{sequenceNumber=" + this.getSequenceNumber() + "}";
        }
    }

    @Immutable(containerOf={"K"})
    public static final class InvalidateEntry<K extends Serializable>
    implements CacheInvalidation<K> {
        private final SequenceNumber sequenceNumber;
        private final K key;

        InvalidateEntry(K key, SequenceNumber sequenceNumber) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
        }

        @Override
        public SequenceNumber getSequenceNumber() {
            return this.sequenceNumber;
        }

        @Override
        public void accept(Cache<K, ?> cache) {
            cache.remove(this.key);
        }

        public String toString() {
            return "InvalidateEntry{sequenceNumber=" + this.getSequenceNumber() + ", key=" + this.key + "}";
        }
    }
}

