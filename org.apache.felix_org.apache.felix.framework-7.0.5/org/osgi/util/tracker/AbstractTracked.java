/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.util.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract class AbstractTracked<S, T, R> {
    static final boolean DEBUG = false;
    private final Map<S, T> tracked = new HashMap<S, T>();
    private int trackingCount = 0;
    private final List<S> adding = new ArrayList<S>(6);
    volatile boolean closed = false;
    private final LinkedList<S> initial = new LinkedList();

    AbstractTracked() {
    }

    void setInitial(S[] list) {
        if (list == null) {
            return;
        }
        for (S item : list) {
            if (item == null) continue;
            this.initial.add(item);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void trackInitial() {
        while (true) {
            S item;
            AbstractTracked abstractTracked = this;
            synchronized (abstractTracked) {
                if (this.closed || this.initial.size() == 0) {
                    return;
                }
                item = this.initial.removeFirst();
                if (this.tracked.get(item) != null) {
                    continue;
                }
                if (this.adding.contains(item)) {
                    continue;
                }
                this.adding.add(item);
            }
            this.trackAdding(item, null);
        }
    }

    void close() {
        this.closed = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void track(S item, R related) {
        T object;
        AbstractTracked abstractTracked = this;
        synchronized (abstractTracked) {
            if (this.closed) {
                return;
            }
            object = this.tracked.get(item);
            if (object == null) {
                if (this.adding.contains(item)) {
                    return;
                }
                this.adding.add(item);
            } else {
                this.modified();
            }
        }
        if (object == null) {
            this.trackAdding(item, related);
        } else {
            this.customizerModified(item, related, object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void trackAdding(S item, R related) {
        T object = null;
        boolean becameUntracked = false;
        try {
            object = this.customizerAdding(item, related);
        }
        finally {
            AbstractTracked abstractTracked = this;
            synchronized (abstractTracked) {
                if (this.adding.remove(item) && !this.closed) {
                    if (object != null) {
                        this.tracked.put(item, object);
                        this.modified();
                        this.notifyAll();
                    }
                } else {
                    becameUntracked = true;
                }
            }
        }
        if (becameUntracked && object != null) {
            this.customizerRemoved(item, related, object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void untrack(S item, R related) {
        T object;
        AbstractTracked abstractTracked = this;
        synchronized (abstractTracked) {
            if (this.initial.remove(item)) {
                return;
            }
            if (this.adding.remove(item)) {
                return;
            }
            object = this.tracked.remove(item);
            if (object == null) {
                return;
            }
            this.modified();
        }
        this.customizerRemoved(item, related, object);
    }

    int size() {
        return this.tracked.size();
    }

    boolean isEmpty() {
        return this.tracked.isEmpty();
    }

    T getCustomizedObject(S item) {
        return this.tracked.get(item);
    }

    S[] copyKeys(S[] list) {
        return this.tracked.keySet().toArray(list);
    }

    void modified() {
        ++this.trackingCount;
    }

    int getTrackingCount() {
        return this.trackingCount;
    }

    <M extends Map<? super S, ? super T>> M copyEntries(M map) {
        map.putAll(this.tracked);
        return map;
    }

    abstract T customizerAdding(S var1, R var2);

    abstract void customizerModified(S var1, R var2, T var3);

    abstract void customizerRemoved(S var1, R var2, T var3);
}

