/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import net.sourceforge.jtds.jdbc.ProcEntry;
import net.sourceforge.jtds.jdbc.cache.StatementCache;

public class ProcedureCache
implements StatementCache {
    private static final int MAX_INITIAL_SIZE = 50;
    private HashMap cache;
    int cacheSize;
    CacheEntry head;
    CacheEntry tail;
    ArrayList free;

    public ProcedureCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.cache = new HashMap(Math.min(50, cacheSize) + 1);
        this.head = new CacheEntry(null, null);
        this.head.next = this.tail = new CacheEntry(null, null);
        this.tail.prior = this.head;
        this.free = new ArrayList();
    }

    @Override
    public synchronized Object get(String key) {
        CacheEntry ce = (CacheEntry)this.cache.get(key);
        if (ce != null) {
            ce.unlink();
            ce.link(this.head);
            ce.value.addRef();
            return ce.value;
        }
        return null;
    }

    @Override
    public synchronized void put(String key, Object handle) {
        ((ProcEntry)handle).addRef();
        CacheEntry ce = new CacheEntry(key, (ProcEntry)handle);
        this.cache.put(key, ce);
        ce.link(this.head);
        this.scavengeCache();
    }

    @Override
    public synchronized void remove(String key) {
        CacheEntry ce = (CacheEntry)this.cache.get(key);
        if (ce != null) {
            ce.unlink();
            this.cache.remove(key);
        }
    }

    @Override
    public synchronized Collection getObsoleteHandles(Collection handles) {
        if (handles != null) {
            for (ProcEntry handle : handles) {
                handle.release();
            }
        }
        this.scavengeCache();
        if (this.free.size() > 0) {
            ArrayList list = this.free;
            this.free = new ArrayList();
            return list;
        }
        return null;
    }

    private void scavengeCache() {
        CacheEntry ce = this.tail.prior;
        while (ce != this.head && this.cache.size() > this.cacheSize) {
            if (ce.value.getRefCount() == 0) {
                ce.unlink();
                this.free.add(ce.value);
                this.cache.remove(ce.key);
            }
            ce = ce.prior;
        }
    }

    private static class CacheEntry {
        String key;
        ProcEntry value;
        CacheEntry next;
        CacheEntry prior;

        CacheEntry(String key, ProcEntry value) {
            this.key = key;
            this.value = value;
        }

        void unlink() {
            this.next.prior = this.prior;
            this.prior.next = this.next;
        }

        void link(CacheEntry ce) {
            this.next = ce.next;
            this.prior = ce;
            this.next.prior = this;
            ce.next = this;
        }
    }
}

