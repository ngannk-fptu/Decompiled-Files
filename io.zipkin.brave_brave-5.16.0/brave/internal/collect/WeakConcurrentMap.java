/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.collect;

import brave.internal.Nullable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WeakConcurrentMap<K, V>
extends ReferenceQueue<K>
implements Iterable<Map.Entry<K, V>> {
    final ConcurrentMap<WeakKey<K>, V> target = new ConcurrentHashMap<WeakKey<K>, V>();

    @Nullable
    public V getIfPresent(K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        this.expungeStaleEntries();
        return this.target.get(key);
    }

    @Nullable
    public V putIfProbablyAbsent(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        this.expungeStaleEntries();
        return this.target.putIfAbsent(new WeakKey<K>(key, this), value);
    }

    @Nullable
    public V remove(K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        this.expungeStaleEntries();
        return this.target.remove(key);
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new EntryIterator(this.target.entrySet().iterator());
    }

    protected void expungeStaleEntries() {
        Reference reference;
        while ((reference = this.poll()) != null) {
            this.removeStaleEntry(reference);
        }
    }

    protected V removeStaleEntry(Reference<?> reference) {
        return this.target.remove(reference);
    }

    public String toString() {
        Class<?> thisClass = this.getClass();
        while (thisClass.getSimpleName().isEmpty()) {
            thisClass = thisClass.getSuperclass();
        }
        this.expungeStaleEntries();
        return thisClass.getSimpleName() + this.target.keySet();
    }

    static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == null ? b == null : a.equals(b);
    }

    class EntryIterator
    implements Iterator<Map.Entry<K, V>> {
        private final Iterator<Map.Entry<WeakKey<K>, V>> iterator;
        private Map.Entry<WeakKey<K>, V> nextEntry;
        private K nextKey;

        private EntryIterator(Iterator<Map.Entry<WeakKey<K>, V>> iterator) {
            this.iterator = iterator;
            this.findNext();
        }

        private void findNext() {
            while (this.iterator.hasNext()) {
                this.nextEntry = this.iterator.next();
                this.nextKey = this.nextEntry.getKey().get();
                if (this.nextKey == null) continue;
                return;
            }
            this.nextEntry = null;
            this.nextKey = null;
        }

        @Override
        public boolean hasNext() {
            return this.nextKey != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (this.nextKey == null) {
                throw new NoSuchElementException();
            }
            try {
                AbstractMap.SimpleImmutableEntry simpleImmutableEntry = new AbstractMap.SimpleImmutableEntry(this.nextKey, this.nextEntry.getValue());
                return simpleImmutableEntry;
            }
            finally {
                this.findNext();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static final class WeakKey<T>
    extends WeakReference<T> {
        final int hashCode;

        WeakKey(T key, ReferenceQueue<? super T> queue) {
            super(key, queue);
            this.hashCode = key.hashCode();
        }

        public int hashCode() {
            return this.hashCode;
        }

        public String toString() {
            Object value = this.get();
            return value != null ? value.toString() : "ClearedReference()";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            assert (o instanceof WeakReference) : "Bug: unexpected input to equals";
            return WeakConcurrentMap.equal(this.get(), ((WeakReference)o).get());
        }
    }
}

