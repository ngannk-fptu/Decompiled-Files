/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.internal.tracker;

public interface DirtyTracker {
    public void add(String var1);

    public boolean contains(String var1);

    public void clear();

    public boolean isEmpty();

    public String[] get();

    public void suspend(boolean var1);
}

