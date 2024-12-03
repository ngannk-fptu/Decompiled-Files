/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;

@Deprecated
@BinaryInterface
public interface Mapper<KeyIn, ValueIn, KeyOut, ValueOut>
extends Serializable {
    public void map(KeyIn var1, ValueIn var2, Context<KeyOut, ValueOut> var3);
}

