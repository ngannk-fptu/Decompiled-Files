/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.LifecycleMapper;
import com.hazelcast.nio.serialization.BinaryInterface;

@Deprecated
@BinaryInterface
public abstract class LifecycleMapperAdapter<KeyIn, ValueIn, KeyOut, ValueOut>
implements LifecycleMapper<KeyIn, ValueIn, KeyOut, ValueOut> {
    @Override
    public void initialize(Context<KeyOut, ValueOut> context) {
    }

    @Override
    public abstract void map(KeyIn var1, ValueIn var2, Context<KeyOut, ValueOut> var3);

    @Override
    public void finalized(Context<KeyOut, ValueOut> context) {
    }
}

