/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.util;

import java.util.ArrayList;
import java.util.Collection;

public final class FinalArrayList<E>
extends ArrayList<E> {
    public FinalArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public FinalArrayList() {
    }

    public FinalArrayList(Collection<? extends E> collection) {
        super(collection);
    }
}

