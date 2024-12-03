/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.idgen;

import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IdGenerator;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class IdGeneratorImpl
implements IdGenerator {
    public static final int BLOCK_SIZE = 10000;
    private static final AtomicIntegerFieldUpdater<IdGeneratorImpl> RESIDUE = AtomicIntegerFieldUpdater.newUpdater(IdGeneratorImpl.class, "residue");
    private static final AtomicLongFieldUpdater<IdGeneratorImpl> LOCAL = AtomicLongFieldUpdater.newUpdater(IdGeneratorImpl.class, "local");
    private final IAtomicLong blockGenerator;
    private volatile int residue = 10000;
    private volatile long local = -1L;

    public IdGeneratorImpl(IAtomicLong blockGenerator) {
        this.blockGenerator = blockGenerator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean init(long id) {
        if (id < 0L) {
            return false;
        }
        long step = id / 10000L;
        IdGeneratorImpl idGeneratorImpl = this;
        synchronized (idGeneratorImpl) {
            boolean init = this.blockGenerator.compareAndSet(0L, step + 1L);
            if (init) {
                LOCAL.set(this, step);
                RESIDUE.set(this, (int)(id % 10000L) + 1);
            }
            return init;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long newId() {
        long block = this.local;
        int value = RESIDUE.getAndIncrement(this);
        if (this.local != block) {
            return this.newId();
        }
        if (value < 10000) {
            return block * 10000L + (long)value;
        }
        IdGeneratorImpl idGeneratorImpl = this;
        synchronized (idGeneratorImpl) {
            value = this.residue;
            if (value >= 10000) {
                LOCAL.set(this, this.blockGenerator.getAndIncrement());
                RESIDUE.set(this, 0);
            }
        }
        return this.newId();
    }

    @Override
    public String getPartitionKey() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void destroy() {
        IdGeneratorImpl idGeneratorImpl = this;
        synchronized (idGeneratorImpl) {
            this.blockGenerator.destroy();
            LOCAL.set(this, -1L);
            RESIDUE.set(this, 10000);
        }
    }
}

