/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public final class WeakIdentityMap<K, V> {
    private final ReferenceQueue<Object> queue = new ReferenceQueue();
    private final Map<IdentityWeakReference, V> backingStore;
    private final boolean reapOnRead;
    static final Object NULL = new Object();

    public static <K, V> WeakIdentityMap<K, V> newHashMap() {
        return WeakIdentityMap.newHashMap(true);
    }

    public static <K, V> WeakIdentityMap<K, V> newHashMap(boolean reapOnRead) {
        return new WeakIdentityMap(new HashMap(), reapOnRead);
    }

    public static <K, V> WeakIdentityMap<K, V> newConcurrentHashMap() {
        return WeakIdentityMap.newConcurrentHashMap(true);
    }

    public static <K, V> WeakIdentityMap<K, V> newConcurrentHashMap(boolean reapOnRead) {
        return new WeakIdentityMap(new ConcurrentHashMap(), reapOnRead);
    }

    private WeakIdentityMap(Map<IdentityWeakReference, V> backingStore, boolean reapOnRead) {
        this.backingStore = backingStore;
        this.reapOnRead = reapOnRead;
    }

    public void clear() {
        this.backingStore.clear();
        this.reap();
    }

    public boolean containsKey(Object key) {
        if (this.reapOnRead) {
            this.reap();
        }
        return this.backingStore.containsKey(new IdentityWeakReference(key, null));
    }

    public V get(Object key) {
        if (this.reapOnRead) {
            this.reap();
        }
        return this.backingStore.get(new IdentityWeakReference(key, null));
    }

    public V put(K key, V value) {
        this.reap();
        return this.backingStore.put(new IdentityWeakReference(key, this.queue), value);
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public V remove(Object key) {
        this.reap();
        return this.backingStore.remove(new IdentityWeakReference(key, null));
    }

    public int size() {
        if (this.backingStore.isEmpty()) {
            return 0;
        }
        if (this.reapOnRead) {
            this.reap();
        }
        return this.backingStore.size();
    }

    public Iterator<K> keyIterator() {
        this.reap();
        final Iterator<IdentityWeakReference> iterator = this.backingStore.keySet().iterator();
        return new Iterator<K>(){
            private Object next = null;
            private boolean nextIsSet = false;

            @Override
            public boolean hasNext() {
                return this.nextIsSet || this.setNext();
            }

            @Override
            public K next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                assert (this.nextIsSet);
                try {
                    Object object = this.next;
                    return object;
                }
                finally {
                    this.nextIsSet = false;
                    this.next = null;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private boolean setNext() {
                assert (!this.nextIsSet);
                while (iterator.hasNext()) {
                    this.next = ((IdentityWeakReference)iterator.next()).get();
                    if (this.next == null) {
                        iterator.remove();
                        continue;
                    }
                    if (this.next == NULL) {
                        this.next = null;
                    }
                    this.nextIsSet = true;
                    return true;
                }
                return false;
            }
        };
    }

    public Iterator<V> valueIterator() {
        if (this.reapOnRead) {
            this.reap();
        }
        return this.backingStore.values().iterator();
    }

    public void reap() {
        Reference<Object> zombie;
        while ((zombie = this.queue.poll()) != null) {
            this.backingStore.remove(zombie);
        }
    }

    private static final class IdentityWeakReference
    extends WeakReference<Object> {
        private final int hash;

        IdentityWeakReference(Object obj, ReferenceQueue<Object> queue) {
            super(obj == null ? NULL : obj, queue);
            this.hash = System.identityHashCode(obj);
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof IdentityWeakReference) {
                IdentityWeakReference ref = (IdentityWeakReference)o;
                if (this.get() == ref.get()) {
                    return true;
                }
            }
            return false;
        }
    }
}

