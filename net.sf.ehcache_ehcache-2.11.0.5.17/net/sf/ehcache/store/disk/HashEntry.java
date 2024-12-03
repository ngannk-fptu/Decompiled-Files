/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.disk;

import java.util.concurrent.atomic.AtomicBoolean;
import net.sf.ehcache.pool.sizeof.annotations.IgnoreSizeOf;
import net.sf.ehcache.store.disk.DiskStorageFactory;

final class HashEntry {
    @IgnoreSizeOf
    protected final Object key;
    protected final int hash;
    @IgnoreSizeOf
    protected final HashEntry next;
    protected volatile DiskStorageFactory.DiskSubstitute element;
    protected final AtomicBoolean faulted;

    HashEntry(Object key, int hash, HashEntry next, DiskStorageFactory.DiskSubstitute element, AtomicBoolean faulted) {
        this.key = key;
        this.hash = hash;
        this.next = next;
        this.element = element;
        this.faulted = faulted;
    }
}

