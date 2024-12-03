/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.internal.tracker;

import org.hibernate.bytecode.enhance.internal.tracker.DirtyTracker;

public final class SortedFieldTracker
implements DirtyTracker {
    private String[] names = new String[0];
    private boolean suspended;

    @Override
    public void add(String name) {
        if (this.suspended) {
            return;
        }
        int insert = 0;
        int low = 0;
        int high = this.names.length - 1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            int compare = this.names[middle].compareTo(name);
            if (compare > 0) {
                high = middle - 1;
                insert = middle;
                continue;
            }
            if (compare < 0) {
                insert = low = middle + 1;
                continue;
            }
            return;
        }
        String[] newNames = new String[this.names.length + 1];
        System.arraycopy(this.names, 0, newNames, 0, insert);
        System.arraycopy(this.names, insert, newNames, insert + 1, this.names.length - insert);
        newNames[insert] = name;
        this.names = newNames;
    }

    @Override
    public boolean contains(String name) {
        int low = 0;
        int high = this.names.length - 1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            int compare = this.names[middle].compareTo(name);
            if (compare > 0) {
                high = middle - 1;
                continue;
            }
            if (compare < 0) {
                low = middle + 1;
                continue;
            }
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

