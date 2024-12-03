/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.ognl;

public interface OgnlCache<Key, Value> {
    public Value get(Key var1);

    public void put(Key var1, Value var2);

    public void putIfAbsent(Key var1, Value var2);

    public int size();

    public void clear();

    public int getEvictionLimit();

    public void setEvictionLimit(int var1);
}

