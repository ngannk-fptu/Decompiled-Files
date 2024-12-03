/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Slot;

public interface SlotMap
extends Iterable<Slot> {
    public int size();

    public boolean isEmpty();

    public Slot modify(Object var1, int var2, int var3);

    public Slot query(Object var1, int var2);

    public void replace(Slot var1, Slot var2);

    public void add(Slot var1);

    public void remove(Object var1, int var2);
}

