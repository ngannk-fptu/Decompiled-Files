/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.flakeidgen.impl;

import com.hazelcast.flakeidgen.impl.IdBatch;
import com.hazelcast.util.Clock;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AutoBatcher {
    private final int batchSize;
    private final long validity;
    private volatile Block block = new Block(new IdBatch(0L, 0L, 0), 0L);
    private final IdBatchSupplier batchIdSupplier;

    public AutoBatcher(int batchSize, long validity, IdBatchSupplier idGenerator) {
        this.batchSize = batchSize;
        this.validity = validity;
        this.batchIdSupplier = idGenerator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long newId() {
        Block block;
        long res;
        while ((res = (block = this.block).next()) == Long.MIN_VALUE) {
            AutoBatcher autoBatcher = this;
            synchronized (autoBatcher) {
                if (block != this.block) {
                    continue;
                }
                this.block = new Block(this.batchIdSupplier.newIdBatch(this.batchSize), this.validity);
            }
        }
        return res;
    }

    public static interface IdBatchSupplier {
        public IdBatch newIdBatch(int var1);
    }

    private static final class Block {
        private static final AtomicIntegerFieldUpdater<Block> NUM_RETURNED = AtomicIntegerFieldUpdater.newUpdater(Block.class, "numReturned");
        private final IdBatch idBatch;
        private final long invalidSince;
        private volatile int numReturned;

        private Block(IdBatch idBatch, long validity) {
            this.idBatch = idBatch;
            this.invalidSince = validity > 0L ? Clock.currentTimeMillis() + validity : Long.MAX_VALUE;
        }

        long next() {
            int index;
            if (this.invalidSince <= Clock.currentTimeMillis()) {
                return Long.MIN_VALUE;
            }
            do {
                if ((index = this.numReturned) != this.idBatch.batchSize()) continue;
                return Long.MIN_VALUE;
            } while (!NUM_RETURNED.compareAndSet(this, index, index + 1));
            return this.idBatch.base() + (long)index * this.idBatch.increment();
        }
    }
}

