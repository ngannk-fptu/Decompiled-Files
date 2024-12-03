/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 */
package com.atlassian.fugue;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class WeakMemoizer<A, B>
implements Function<A, B> {
    private final ConcurrentMap<A, MappedReference<A, B>> map;
    private final ReferenceQueue<B> queue = new ReferenceQueue();
    private final Function<A, B> delegate;

    static <A, B> WeakMemoizer<A, B> weakMemoizer(Function<A, B> delegate) {
        return new WeakMemoizer<A, B>(delegate);
    }

    WeakMemoizer(Function<A, B> delegate) {
        this.map = new ConcurrentHashMap<A, MappedReference<A, B>>();
        this.delegate = (Function)Preconditions.checkNotNull(delegate, (Object)"delegate");
    }

    public B apply(A descriptor) {
        this.expungeStaleEntries();
        Preconditions.checkNotNull(descriptor, (Object)"descriptor");
        while (true) {
            MappedReference reference;
            if ((reference = (MappedReference)this.map.get(descriptor)) != null) {
                Object value = reference.get();
                if (value != null) {
                    return (B)value;
                }
                this.map.remove(descriptor, reference);
            }
            this.map.putIfAbsent(descriptor, new MappedReference<A, Object>(descriptor, this.delegate.apply(descriptor), this.queue));
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
            super(Preconditions.checkNotNull(value, (Object)"value"), q);
            this.key = Preconditions.checkNotNull(key, (Object)"key");
        }

        final K getDescriptor() {
            return this.key;
        }
    }
}

