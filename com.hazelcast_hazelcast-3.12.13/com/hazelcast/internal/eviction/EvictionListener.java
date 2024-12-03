/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.internal.eviction.Evictable;

public interface EvictionListener<A, E extends Evictable> {
    public static final EvictionListener NO_LISTENER = null;

    public void onEvict(A var1, E var2, boolean var3);
}

