/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.io.IOStringEnumeration;
import com.mchange.io.IOStringObjectMap;
import com.mchange.util.StringEnumeration;

public interface StringObjectMap
extends IOStringObjectMap {
    @Override
    public Object get(String var1);

    @Override
    public void put(String var1, Object var2);

    @Override
    public boolean putNoReplace(String var1, Object var2);

    @Override
    public boolean remove(String var1);

    @Override
    public boolean containsKey(String var1);

    @Override
    public IOStringEnumeration keys();

    public StringEnumeration mkeys();
}

