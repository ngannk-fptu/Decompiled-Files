/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidation;
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidator;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceNumber;
import com.atlassian.cache.hazelcast.asyncinvalidation.Topic;
import java.io.Serializable;
import java.util.function.Function;

final class InvalidationTopicSender {
    private final SequenceNumber.Generator sequenceGenerator = SequenceNumber.newSequenceNumberGenerator();

    InvalidationTopicSender() {
    }

    public SequenceNumber getCurrentSequenceNumber() {
        return this.sequenceGenerator.getCurrent();
    }

    public <K extends Serializable> CacheInvalidator<K> createInvalidator(final Topic<CacheInvalidation<K>> topic) {
        return new CacheInvalidator<K>(){

            @Override
            public void invalidateEntry(K key) {
                this.publish(seq -> CacheInvalidation.entryInvalidation(key, seq));
            }

            @Override
            public void invalidateAllEntries() {
                this.publish(CacheInvalidation::allEntriesInvalidation);
            }

            private void publish(Function<SequenceNumber, CacheInvalidation<K>> f) {
                topic.publish(f.apply(InvalidationTopicSender.this.sequenceGenerator.getNext()));
            }
        };
    }
}

