/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.util.IntEnumeration;

public interface IntObjectMap {
    public Object get(int var1);

    public void put(int var1, Object var2);

    public boolean putNoReplace(int var1, Object var2);

    public Object remove(int var1);

    public boolean containsInt(int var1);

    public int getSize();

    public void clear();

    public IntEnumeration ints();
}

