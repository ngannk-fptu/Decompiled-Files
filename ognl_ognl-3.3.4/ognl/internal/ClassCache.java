/*
 * Decompiled with CFR 0.152.
 */
package ognl.internal;

import ognl.ClassCacheInspector;

public interface ClassCache {
    public void setClassInspector(ClassCacheInspector var1);

    public void clear();

    public int getSize();

    public Object get(Class var1);

    public Object put(Class var1, Object var2);
}

