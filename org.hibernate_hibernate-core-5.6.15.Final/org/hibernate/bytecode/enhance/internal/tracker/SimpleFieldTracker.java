/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.internal.tracker;

import java.util.Arrays;
import org.hibernate.bytecode.enhance.internal.tracker.DirtyTracker;

public final class SimpleFieldTracker
implements DirtyTracker {
    private String[] names = new String[0];
    private boolean suspended;

    @Override
    public void add(String name) {
        if (this.suspended) {
            return;
        }
        if (!this.contains(name)) {
            this.names = Arrays.copyOf(this.names, this.names.length + 1);
            this.names[this.names.length - 1] = name;
        }
    }

    @Override
    public boolean contains(String name) {
        for (String existing : this.names) {
            if (!existing.equals(name)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if (!this.isEmpty()) {
            this.names = new String[0];
        }
    }

    @Override
    public boolean isEmpty() {
        return this.names.length == 0;
    }

    @Override
    public String[] get() {
        return this.names;
    }

    @Override
    public void suspend(boolean suspend) {
        this.suspended = suspend;
    }
}

