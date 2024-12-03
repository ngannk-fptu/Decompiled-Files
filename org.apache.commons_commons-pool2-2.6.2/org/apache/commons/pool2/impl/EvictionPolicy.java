/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.EvictionConfig;

public interface EvictionPolicy<T> {
    public boolean evict(EvictionConfig var1, PooledObject<T> var2, int var3);
}

