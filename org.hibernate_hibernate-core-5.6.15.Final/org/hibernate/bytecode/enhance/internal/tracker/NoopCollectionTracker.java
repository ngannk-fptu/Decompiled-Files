/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.internal.tracker;

import org.hibernate.bytecode.enhance.spi.CollectionTracker;

public final class NoopCollectionTracker
implements CollectionTracker {
    public static final CollectionTracker INSTANCE = new NoopCollectionTracker();

    @Override
    public void add(String name, int size) {
    }

    @Override
    public int getSize(String name) {
        return -1;
    }
}

