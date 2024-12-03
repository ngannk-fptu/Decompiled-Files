/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.UuidUtil;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLongArray;

public class MetaDataGenerator {
    private final int partitionCount;
    private final ConstructorFunction<String, AtomicLongArray> sequenceGeneratorConstructor = new ConstructorFunction<String, AtomicLongArray>(){

        @Override
        public AtomicLongArray createNew(String arg) {
            return new AtomicLongArray(MetaDataGenerator.this.partitionCount);
        }
    };
    private final ConcurrentMap<Integer, UUID> uuids = new ConcurrentHashMap<Integer, UUID>();
    private final ConcurrentMap<String, AtomicLongArray> sequenceGenerators = new ConcurrentHashMap<String, AtomicLongArray>();
    private final ConstructorFunction<Integer, UUID> uuidConstructor = new ConstructorFunction<Integer, UUID>(){

        @Override
        public UUID createNew(Integer partitionId) {
            return UuidUtil.newUnsecureUUID();
        }
    };

    public MetaDataGenerator(int partitionCount) {
        assert (partitionCount > 0);
        this.partitionCount = partitionCount;
    }

    public long currentSequence(String name, int partitionId) {
        AtomicLongArray sequences = (AtomicLongArray)this.sequenceGenerators.get(name);
        if (sequences == null) {
            return 0L;
        }
        return sequences.get(partitionId);
    }

    public long nextSequence(String name, int partitionId) {
        return this.sequenceGenerator(name).incrementAndGet(partitionId);
    }

    public void setCurrentSequence(String name, int partitionId, long sequence) {
        this.sequenceGenerator(name).set(partitionId, sequence);
    }

    private AtomicLongArray sequenceGenerator(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.sequenceGenerators, name, this.sequenceGeneratorConstructor);
    }

    public UUID getOrCreateUuid(int partitionId) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.uuids, partitionId, this.uuidConstructor);
    }

    public UUID getUuidOrNull(int partitionId) {
        return (UUID)this.uuids.get(partitionId);
    }

    public void setUuid(int partitionId, UUID uuid) {
        this.uuids.put(partitionId, uuid);
    }

    public void removeUuidAndSequence(int partitionId) {
        this.uuids.remove(partitionId);
        for (AtomicLongArray sequences : this.sequenceGenerators.values()) {
            sequences.set(partitionId, 0L);
        }
    }

    public void destroyMetaDataFor(String dataStructureName) {
        this.sequenceGenerators.remove(dataStructureName);
    }

    public void regenerateUuid(int partitionId) {
        this.uuids.put(partitionId, this.uuidConstructor.createNew(partitionId));
    }

    public void resetSequence(String name, int partitionId) {
        this.sequenceGenerator(name).set(partitionId, 0L);
    }

    public ConcurrentMap<String, AtomicLongArray> getSequenceGenerators() {
        return this.sequenceGenerators;
    }
}

