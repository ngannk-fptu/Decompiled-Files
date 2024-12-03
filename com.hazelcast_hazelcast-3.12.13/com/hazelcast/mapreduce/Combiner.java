/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

@Deprecated
public abstract class Combiner<ValueIn, ValueOut> {
    public void beginCombine() {
    }

    public abstract void combine(ValueIn var1);

    public abstract ValueOut finalizeChunk();

    public void reset() {
    }

    public void finalizeCombine() {
    }
}

