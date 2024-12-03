/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata;

import com.twelvemonkeys.imageio.metadata.Entry;

public interface Directory
extends Iterable<Entry> {
    public Entry getEntryById(Object var1);

    public Entry getEntryByFieldName(String var1);

    public boolean add(Entry var1);

    public boolean remove(Object var1);

    public int size();

    public boolean isReadOnly();
}

