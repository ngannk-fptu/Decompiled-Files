/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.nio.Address;
import java.util.Map;

class ReducerChunk<Key, Chunk> {
    final Map<Key, Chunk> chunk;
    final int partitionId;
    final Address sender;

    ReducerChunk(Map<Key, Chunk> chunk, int partitionId, Address sender) {
        this.chunk = chunk;
        this.sender = sender;
        this.partitionId = partitionId;
    }
}

