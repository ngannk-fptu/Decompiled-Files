/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

@Deprecated
public abstract class Reducer<ValueIn, ValueOut> {
    public void beginReduce() {
    }

    public abstract void reduce(ValueIn var1);

    public abstract ValueOut finalizeReduce();
}

