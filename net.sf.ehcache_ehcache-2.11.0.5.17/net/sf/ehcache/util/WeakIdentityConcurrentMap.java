/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class WeakIdentityConcurrentMap<K, V> {
    private final ConcurrentMap<WeakReference<K>, V> map = new ConcurrentHashMap<WeakReference<K>, V>();
    private final ReferenceQueue<K> queue = new ReferenceQueue();
    private final CleanUpTask<V> cleanUpTask;

    public WeakIdentityConcurrentMap() {
        this(null);
    }

    public WeakIdentityConcurrentMap(CleanUpTask<V> cleanUpTask) {
        this.cleanUpTask = cleanUpTask;
    }

    public V put(K key, V value) {
        this.cleanUp();
        return this.map.put(new IdentityWeakReference<K>(key, this.queue), value);
    }

    public V remove(K key) {
        this.cleanUp();
        return this.map.remove(new IdentityWeakReference<K>(key, this.queue));
    }

    public String toString() {
        this.cleanUp();
        return this.map.toString();
    }

    public V putIfAbsent(K key, V value) {
        this.cleanUp();
        return this.map.putIfAbsent(new IdentityWeakReference<K>(key, this.queue), value);
    }

    public V get(K key) {
        this.cleanUp();
        return this.map.get(new IdentityWeakReference<K>(key));
    }

    public void cleanUp() {
        Reference<K> reference;
        while ((reference = this.queue.poll()) != null) {
            Object value = this.map.remove(reference);
            if (this.cleanUpTask == null || value == null) continue;
            this.cleanUpTask.cleanUp(value);
        }
    }

    public Set<K> keySet() {
        this.cleanUp();
        HashSet ks = new HashSet();
        for (WeakReference weakReference : this.map.keySet()) {
            Object k = weakReference.get();
            if (k == null) continue;
            ks.add(k);
        }
        return ks;
    }

    public static interface CleanUpTask<T> {
        public void cleanUp(T var1);
    }

    private static final class IdentityWeakReference<T>
    extends WeakReference<T> {
        private final int hashCode;

        IdentityWeakReference(T reference) {
            this(reference, (ReferenceQueue<T>)null);
        }

        IdentityWeakReference(T reference, ReferenceQueue<T> referenceQueue) {
            super(reference, referenceQueue);
            this.hashCode = reference == null ? 0 : System.identityHashCode(reference);
        }

        public String toString() {
            return String.valueOf(this.get());
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IdentityWeakReference)) {
                return false;
            }
            IdentityWeakReference wr = (IdentityWeakReference)o;
            Object got = this.get();
            return got != null && got == wr.get();
        }

        public int hashCode() {
            return this.hashCode;
        }
    }
}

