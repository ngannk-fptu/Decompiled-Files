/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

public interface LongObjectMap {
    public Object get(long var1);

    public void put(long var1, Object var3);

    public boolean putNoReplace(long var1, Object var3);

    public Object remove(long var1);

    public boolean containsLong(long var1);

    public long getSize();
}

