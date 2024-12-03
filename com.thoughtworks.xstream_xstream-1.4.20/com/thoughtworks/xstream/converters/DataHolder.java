/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters;

import java.util.Iterator;

public interface DataHolder {
    public Object get(Object var1);

    public void put(Object var1, Object var2);

    public Iterator keys();
}

