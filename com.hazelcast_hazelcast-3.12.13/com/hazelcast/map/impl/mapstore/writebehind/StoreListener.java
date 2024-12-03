/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import com.hazelcast.map.impl.mapstore.writebehind.StoreEvent;
import java.util.EventListener;

interface StoreListener<E>
extends EventListener {
    public void beforeStore(StoreEvent<E> var1);

    public void afterStore(StoreEvent<E> var1);
}

