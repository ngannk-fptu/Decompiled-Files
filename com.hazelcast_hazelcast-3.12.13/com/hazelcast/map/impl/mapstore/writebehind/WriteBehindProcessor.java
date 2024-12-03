/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.impl.mapstore.writebehind.StoreListener;
import com.hazelcast.map.impl.mapstore.writebehind.WriteBehindQueue;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface WriteBehindProcessor<E> {
    public Map<Integer, List<E>> process(List<E> var1);

    public void callAfterStoreListeners(Collection<E> var1);

    public void callBeforeStoreListeners(Collection<E> var1);

    public void addStoreListener(StoreListener var1);

    public void flush(WriteBehindQueue var1);

    public void flush(E var1);
}

