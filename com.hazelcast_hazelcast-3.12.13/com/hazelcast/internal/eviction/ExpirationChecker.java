/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.eviction;

import com.hazelcast.internal.eviction.Expirable;

public interface ExpirationChecker<E extends Expirable> {
    public boolean isExpired(E var1);
}

