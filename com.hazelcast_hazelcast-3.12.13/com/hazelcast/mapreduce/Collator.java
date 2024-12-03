/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

@Deprecated
public interface Collator<ValueIn, ValueOut> {
    public ValueOut collate(Iterable<ValueIn> var1);
}

