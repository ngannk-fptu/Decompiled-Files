/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.internal.tracker;

import java.util.Arrays;
import org.hibernate.bytecode.enhance.spi.CollectionTracker;

public final class SimpleCollectionTracker
implements CollectionTracker {
    private String[] names = new String[0];
    private int[] sizes = new int[0];

    @Override
    public void add(String name, int size) {
        for (int i = 0; i < this.names.length; ++i) {
            if (!this.names[i].equals(name)) continue;
            this.sizes[i] = size;
            return;
        }
        this.names = Arrays.copyOf(this.names, this.names.length + 1);
        this.names[this.names.length - 1] = name;
        this.sizes = Arrays.copyOf(this.sizes, this.sizes.length + 1);
        this.sizes[this.sizes.length - 1] = size;
    }

    @Override
    public int getSize(String name) {
        for (int i = 0; i < this.names.length; ++i) {
            if (!name.equals(this.names[i])) continue;
            return this.sizes[i];
        }
        return -1;
    }
}

