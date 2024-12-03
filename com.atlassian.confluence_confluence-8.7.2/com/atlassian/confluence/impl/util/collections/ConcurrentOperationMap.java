/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.confluence.impl.util.collections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
@Deprecated
public class ConcurrentOperationMap<K, T> {
    private final ConcurrentMap<K, T> map = new ConcurrentHashMap<K, T>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T computeIfAbsent(K key, Supplier<T> supplier) {
        try {
            Object object = this.map.computeIfAbsent(key, (? super K k) -> supplier.get());
            return (T)object;
        }
        finally {
            this.map.remove(key);
        }
    }
}

