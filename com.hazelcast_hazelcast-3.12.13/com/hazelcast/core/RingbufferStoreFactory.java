/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.RingbufferStore;
import java.util.Properties;

public interface RingbufferStoreFactory<T> {
    public RingbufferStore<T> newRingbufferStore(String var1, Properties var2);
}

