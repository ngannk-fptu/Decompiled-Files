/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.Element;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.store.BruteForceSource;
import net.sf.ehcache.store.MemoryStore;

class MemoryStoreBruteForceSource
implements BruteForceSource {
    private final MemoryStore memoryStore;
    private final Searchable searchable;

    MemoryStoreBruteForceSource(MemoryStore memoryStore, Searchable searchable) {
        this.memoryStore = memoryStore;
        this.searchable = searchable;
    }

    @Override
    public Iterable<Element> elements() {
        return this.memoryStore.elementSet();
    }

    @Override
    public Searchable getSearchable() {
        return this.searchable;
    }

    @Override
    public Element transformForIndexing(Element element) {
        return element;
    }
}

