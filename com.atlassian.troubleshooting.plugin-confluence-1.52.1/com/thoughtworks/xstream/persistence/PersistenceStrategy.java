/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.persistence;

import java.util.Iterator;

public interface PersistenceStrategy {
    public Iterator iterator();

    public int size();

    public Object get(Object var1);

    public Object put(Object var1, Object var2);

    public Object remove(Object var1);
}

