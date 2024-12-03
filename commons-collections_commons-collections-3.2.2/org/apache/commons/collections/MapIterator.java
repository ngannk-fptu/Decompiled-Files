/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Iterator;

public interface MapIterator
extends Iterator {
    public boolean hasNext();

    public Object next();

    public Object getKey();

    public Object getValue();

    public void remove();

    public Object setValue(Object var1);
}

