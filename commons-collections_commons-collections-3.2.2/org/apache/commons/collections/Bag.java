/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public interface Bag
extends Collection {
    public int getCount(Object var1);

    public boolean add(Object var1);

    public boolean add(Object var1, int var2);

    public boolean remove(Object var1);

    public boolean remove(Object var1, int var2);

    public Set uniqueSet();

    public int size();

    public boolean containsAll(Collection var1);

    public boolean removeAll(Collection var1);

    public boolean retainAll(Collection var1);

    public Iterator iterator();
}

