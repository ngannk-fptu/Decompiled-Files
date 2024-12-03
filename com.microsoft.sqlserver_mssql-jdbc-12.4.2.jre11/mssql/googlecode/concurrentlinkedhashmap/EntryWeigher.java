/*
 * Decompiled with CFR 0.152.
 */
package mssql.googlecode.concurrentlinkedhashmap;

public interface EntryWeigher<K, V> {
    public int weightOf(K var1, V var2);
}

