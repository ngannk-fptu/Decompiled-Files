/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;

@BinaryInterface
public interface CacheMergePolicy
extends Serializable {
    public Object merge(String var1, CacheEntryView var2, CacheEntryView var3);
}

