/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.spi;

import org.glassfish.ha.store.spi.Storable;

public interface MutableStoreEntry
extends Storable {
    public void _markStoreEntryAsDirty();

    public void _markAsDirty(int var1);

    public void _markAsClean(int var1);

    public void _markStoreEntryAsClean();

    public void _setOwnerId(String var1);
}

