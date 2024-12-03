/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.lucene.search.ReferenceManager;

public abstract class LiveFieldValues<S, T>
implements ReferenceManager.RefreshListener,
Closeable {
    private volatile Map<String, T> current = new ConcurrentHashMap<String, T>();
    private volatile Map<String, T> old = new ConcurrentHashMap<String, T>();
    private final ReferenceManager<S> mgr;
    private final T missingValue;

    public LiveFieldValues(ReferenceManager<S> mgr, T missingValue) {
        this.missingValue = missingValue;
        this.mgr = mgr;
        mgr.addListener(this);
    }

    @Override
    public void close() {
        this.mgr.removeListener(this);
    }

    @Override
    public void beforeRefresh() throws IOException {
        this.old = this.current;
        this.current = new ConcurrentHashMap<String, T>();
    }

    @Override
    public void afterRefresh(boolean didRefresh) throws IOException {
        this.old = new ConcurrentHashMap<String, T>();
    }

    public void add(String id, T value) {
        this.current.put(id, value);
    }

    public void delete(String id) {
        this.current.put(id, this.missingValue);
    }

    public int size() {
        return this.current.size() + this.old.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T get(String id) throws IOException {
        T value = this.current.get(id);
        if (value == this.missingValue) {
            return null;
        }
        if (value != null) {
            return value;
        }
        value = this.old.get(id);
        if (value == this.missingValue) {
            return null;
        }
        if (value != null) {
            return value;
        }
        S s = this.mgr.acquire();
        try {
            T t = this.lookupFromSearcher(s, id);
            return t;
        }
        finally {
            this.mgr.release(s);
        }
    }

    protected abstract T lookupFromSearcher(S var1, String var2) throws IOException;
}

