/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.NotNull;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
final class WeakMemoizer<K, V>
implements Function<K, V> {
    private final ConcurrentMap<K, MappedReference<K, V>> map;
    private final ReferenceQueue<V> queue = new ReferenceQueue();
    private final Function<K, V> delegate;

    static <K, V> WeakMemoizer<K, V> weakMemoizer(Function<K, V> delegate) {
        return new WeakMemoizer<K, V>(delegate);
    }

    WeakMemoizer(@NotNull Function<K, V> delegate) {
        this.map = new ConcurrentHashMap<K, MappedReference<K, V>>();
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override
    public V apply(K descriptor) {
        this.expungeStaleEntries();
        Objects.requireNonNull(descriptor, "descriptor");
        while (true) {
            MappedReference reference;
            if ((reference = (MappedReference)this.map.get(descriptor)) != null) {
                Object value = reference.get();
                if (value != null) {
                    return (V)value;
                }
                this.map.remove(descriptor, reference);
            }
            this.map.putIfAbsent(descriptor, new MappedReference<K, V>(descriptor, this.delegate.apply(descriptor), this.queue));
        }
    }

    private void expungeStaleEntries() {
        MappedReference ref;
        while ((ref = (MappedReference)this.queue.poll()) != null) {
            Object key = ref.getDescriptor();
            if (key == null) continue;
            this.map.remove(key, ref);
        }
    }

    static final class MappedReference<K, V>
    extends WeakReference<V> {
        private final K key;

        public MappedReference(K key, V value, ReferenceQueue<? super V> q) {
            super(Objects.requireNonNull(value, "value"), q);
            this.key = Objects.requireNonNull(key, "key");
        }

        final K getDescriptor() {
            return this.key;
        }
    }
}

