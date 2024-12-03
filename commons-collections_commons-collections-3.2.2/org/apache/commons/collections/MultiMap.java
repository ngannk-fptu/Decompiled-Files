/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Map;

public interface MultiMap
extends Map {
    public Object remove(Object var1, Object var2);

    public int size();

    public Object get(Object var1);

    public boolean containsValue(Object var1);

    public Object put(Object var1, Object var2);

    public Object remove(Object var1);

    public Collection values();
}

