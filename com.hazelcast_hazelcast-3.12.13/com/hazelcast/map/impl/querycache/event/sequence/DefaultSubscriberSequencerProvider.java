/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event.sequence;

import com.hazelcast.map.impl.querycache.event.sequence.DefaultPartitionSequencer;
import com.hazelcast.map.impl.querycache.event.sequence.PartitionSequencer;
import com.hazelcast.map.impl.querycache.event.sequence.SubscriberSequencerProvider;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultSubscriberSequencerProvider
implements SubscriberSequencerProvider {
    private static final ConstructorFunction<Integer, PartitionSequencer> PARTITION_SEQUENCER_CONSTRUCTOR = new ConstructorFunction<Integer, PartitionSequencer>(){

        @Override
        public PartitionSequencer createNew(Integer arg) {
            return new DefaultPartitionSequencer();
        }
    };
    private final ConcurrentMap<Integer, PartitionSequencer> partitionSequences = new ConcurrentHashMap<Integer, PartitionSequencer>();

    @Override
    public boolean compareAndSetSequence(long expect, long update, int partitionId) {
        PartitionSequencer sequence = this.getOrCreateSequence(partitionId);
        return sequence.compareAndSetSequence(expect, update);
    }

    @Override
    public long getSequence(int partitionId) {
        PartitionSequencer sequence = this.getOrCreateSequence(partitionId);
        return sequence.getSequence();
    }

    @Override
    public void reset(int partitionId) {
        PartitionSequencer sequence = this.getOrCreateSequence(partitionId);
        sequence.reset();
    }

    @Override
    public void resetAll() {
        for (PartitionSequencer partitionSequencer : this.partitionSequences.values()) {
            partitionSequencer.reset();
        }
    }

    private PartitionSequencer getOrCreateSequence(int partitionId) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.partitionSequences, partitionId, PARTITION_SEQUENCER_CONSTRUCTOR);
    }
}

