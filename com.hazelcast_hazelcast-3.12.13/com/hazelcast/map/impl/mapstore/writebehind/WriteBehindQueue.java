/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.impl.mapstore.writebehind.IPredicate;
import java.util.Collection;
import java.util.List;

public interface WriteBehindQueue<E> {
    public void addFirst(Collection<E> var1);

    public void addLast(E var1);

    public E peek();

    public boolean removeFirstOccurrence(E var1);

    public int drainTo(Collection<E> var1);

    public boolean contains(E var1);

    public int size();

    public void clear();

    public List<E> asList();

    public void filter(IPredicate<E> var1, Collection<E> var2);
}

