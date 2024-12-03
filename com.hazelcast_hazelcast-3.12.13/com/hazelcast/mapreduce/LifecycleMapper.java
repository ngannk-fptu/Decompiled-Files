/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.nio.serialization.BinaryInterface;

@Deprecated
@BinaryInterface
public interface LifecycleMapper<KeyIn, ValueIn, KeyOut, ValueOut>
extends Mapper<KeyIn, ValueIn, KeyOut, ValueOut> {
    public void initialize(Context<KeyOut, ValueOut> var1);

    public void finalized(Context<KeyOut, ValueOut> var1);
}

