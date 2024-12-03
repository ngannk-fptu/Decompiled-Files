/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.cache;

import java.util.List;

public interface Cache {
    public String getName();

    public Object get(Object var1);

    public List getKeys();

    public void put(Object var1, Object var2);

    public void remove(Object var1);

    public void removeAll();

    public int getStatus();
}

