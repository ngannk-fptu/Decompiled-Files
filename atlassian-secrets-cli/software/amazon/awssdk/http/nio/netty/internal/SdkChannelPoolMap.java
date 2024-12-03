/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolMap;
import java.io.Closeable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public abstract class SdkChannelPoolMap<K, P extends ChannelPool>
implements ChannelPoolMap<K, P>,
Iterable<Map.Entry<K, P>>,
Closeable {
    private final ConcurrentMap<K, P> map = new ConcurrentHashMap<K, P>();

    @Override
    public final P get(K key) {
        return (P)this.map.computeIfAbsent(key, this::newPool);
    }

    public final boolean remove(K key) {
        ChannelPool pool = (ChannelPool)this.map.remove(Validate.paramNotNull(key, "key"));
        if (pool != null) {
            pool.close();
            return true;
        }
        return false;
    }

    @Override
    public final Iterator<Map.Entry<K, P>> iterator() {
        return new ReadOnlyIterator<Map.Entry<K, P>>(this.map.entrySet().iterator());
    }

    public final int size() {
        return this.map.size();
    }

    public final boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public final boolean contains(K key) {
        return this.map.containsKey(Validate.paramNotNull(key, "key"));
    }

    protected abstract P newPool(K var1);

    @Override
    public void close() {
        this.map.keySet().forEach(this::remove);
    }

    public final Map<K, P> pools() {
        return Collections.unmodifiableMap(new HashMap<K, P>(this.map));
    }

    private final class ReadOnlyIterator<T>
    implements Iterator<T> {
        private final Iterator<? extends T> iterator;

        private ReadOnlyIterator(Iterator<? extends T> iterator) {
            this.iterator = Validate.paramNotNull(iterator, "iterator");
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public T next() {
            return this.iterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Read-only iterator doesn't support removal.");
        }
    }
}

