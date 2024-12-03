/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.algorithm;

import com.opensymphony.oscache.base.algorithm.AbstractConcurrentReadCache;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class FIFOCache
extends AbstractConcurrentReadCache {
    private Collection list;
    private boolean isSet = false;

    public FIFOCache() {
        try {
            Class.forName("java.util.LinkedHashSet");
            this.list = new LinkedHashSet();
            this.isSet = true;
        }
        catch (ClassNotFoundException e) {
            this.list = new LinkedList();
        }
    }

    public FIFOCache(int capacity) {
        this();
        this.maxEntries = capacity;
    }

    protected void itemRetrieved(Object key) {
    }

    protected void itemPut(Object key) {
        if (!this.list.contains(key)) {
            this.list.add(key);
        }
    }

    protected Object removeItem() {
        Object toRemove;
        if (this.isSet) {
            Iterator it = this.list.iterator();
            toRemove = it.next();
            it.remove();
        } else {
            toRemove = ((List)this.list).remove(0);
        }
        return toRemove;
    }

    protected void itemRemoved(Object key) {
        this.list.remove(key);
    }
}

