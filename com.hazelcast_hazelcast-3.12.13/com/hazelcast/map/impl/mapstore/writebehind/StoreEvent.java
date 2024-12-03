/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.mapstore.writebehind;

import java.util.EventObject;

public final class StoreEvent<E>
extends EventObject {
    private static final long serialVersionUID = -7071512331813330032L;

    private StoreEvent(E source) {
        super(source);
    }

    public static <E> StoreEvent<E> createStoreEvent(E source) {
        return new StoreEvent<E>(source);
    }

    public E getSource() {
        return (E)super.getSource();
    }
}

