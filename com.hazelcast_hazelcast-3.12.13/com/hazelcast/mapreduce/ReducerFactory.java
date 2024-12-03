/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;

@Deprecated
@BinaryInterface
public interface ReducerFactory<KeyIn, ValueIn, ValueOut>
extends Serializable {
    public Reducer<ValueIn, ValueOut> newReducer(KeyIn var1);
}

