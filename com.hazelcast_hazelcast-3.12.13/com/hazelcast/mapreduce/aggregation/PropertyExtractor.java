/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.aggregation;

import java.io.Serializable;

@Deprecated
public interface PropertyExtractor<ValueIn, ValueOut>
extends Serializable {
    public ValueOut extract(ValueIn var1);
}

