/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import java.io.Serializable;

public interface PartitioningStrategy<K>
extends Serializable {
    public Object getPartitionKey(K var1);
}

