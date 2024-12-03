/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.memoize;

public interface MemoizeCache<K, V> {
    public V put(K var1, V var2);

    public V get(K var1);

    public void cleanUpNullReferences();
}

