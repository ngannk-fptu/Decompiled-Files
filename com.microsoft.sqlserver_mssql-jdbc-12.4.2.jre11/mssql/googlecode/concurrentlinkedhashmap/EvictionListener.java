/*
 * Decompiled with CFR 0.152.
 */
package mssql.googlecode.concurrentlinkedhashmap;

public interface EvictionListener<K, V> {
    public void onEviction(K var1, V var2);
}

