/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool;

import org.apache.commons.pool.KeyedObjectPool;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface KeyedObjectPoolFactory<K, V> {
    public KeyedObjectPool<K, V> createPool() throws IllegalStateException;
}

