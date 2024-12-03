/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;

@Deprecated
@BinaryInterface
public interface CombinerFactory<KeyIn, ValueIn, ValueOut>
extends Serializable {
    public Combiner<ValueIn, ValueOut> newCombiner(KeyIn var1);
}

