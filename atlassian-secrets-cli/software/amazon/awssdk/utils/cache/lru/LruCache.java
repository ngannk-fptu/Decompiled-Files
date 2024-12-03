/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.cache.lru;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
@ThreadSafe
public final class LruCache<K, V> {
    private static final Logger log = Logger.loggerFor(LruCache.class);
    private static final int DEFAULT_SIZE = 100;
    private final Map<K, CacheEntry<K, V>> cache;
    private final Function<K, V> valueSupplier;
    private final Object listLock = new Object();
    private final int maxCacheSize;
    private CacheEntry<K, V> leastRecentlyUsed = null;
    private CacheEntry<K, V> mostRecentlyUsed = null;

    private LruCache(Builder<K, V> b) {
        this.valueSupplier = ((Builder)b).supplier;
        Integer customSize = Validate.isPositiveOrNull(((Builder)b).maxSize, "size");
        this.maxCacheSize = customSize != null ? customSize : 100;
        this.cache = new ConcurrentHashMap<K, CacheEntry<K, V>>();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public V get(K key) {
        while (true) {
            CacheEntry cachedEntry = this.cache.computeIfAbsent(key, this::newEntry);
            Object object = this.listLock;
            synchronized (object) {
                if (!cachedEntry.evicted()) {
                    this.moveToBackOfQueue(cachedEntry);
                    return cachedEntry.value();
                }
            }
        }
    }

    private CacheEntry<K, V> newEntry(K key) {
        V value = this.valueSupplier.apply(key);
        return new CacheEntry(key, value);
    }

    private void moveToBackOfQueue(CacheEntry<K, V> entry) {
        if (entry.equals(this.mostRecentlyUsed)) {
            return;
        }
        this.removeFromQueue(entry);
        this.addToQueue(entry);
    }

    private void removeFromQueue(CacheEntry<K, V> entry) {
        CacheEntry<K, V> nextEntry;
        CacheEntry<K, V> previousEntry = entry.previous();
        if (previousEntry != null) {
            previousEntry.setNext(entry.next());
        }
        if ((nextEntry = entry.next()) != null) {
            nextEntry.setPrevious(entry.previous());
        }
        if (entry.equals(this.leastRecentlyUsed)) {
            this.leastRecentlyUsed = entry.previous();
        }
        if (entry.equals(this.mostRecentlyUsed)) {
            this.mostRecentlyUsed = entry.next();
        }
    }

    private void addToQueue(CacheEntry<K, V> entry) {
        if (this.mostRecentlyUsed != null) {
            this.mostRecentlyUsed.setPrevious(entry);
            entry.setNext(this.mostRecentlyUsed);
        }
        entry.setPrevious(null);
        this.mostRecentlyUsed = entry;
        if (this.leastRecentlyUsed == null) {
            this.leastRecentlyUsed = entry;
        }
        if (this.size() > this.maxCacheSize) {
            this.evict();
        }
    }

    private void evict() {
        this.leastRecentlyUsed.isEvicted(true);
        this.closeEvictedResourcesIfPossible(((CacheEntry)this.leastRecentlyUsed).value);
        this.cache.remove(this.leastRecentlyUsed.key());
        this.removeFromQueue(this.leastRecentlyUsed);
    }

    private void closeEvictedResourcesIfPossible(V value) {
        if (value instanceof AutoCloseable) {
            try {
                ((AutoCloseable)value).close();
            }
            catch (Exception e) {
                log.warn(() -> "Attempted to close instance that was evicted by cache, but got exception: " + e.getMessage());
            }
        }
    }

    public int size() {
        return this.cache.size();
    }

    public static <K, V> Builder<K, V> builder(Function<K, V> supplier) {
        return new Builder(supplier);
    }

    private static final class CacheEntry<K, V> {
        private final K key;
        private final V value;
        private boolean evicted = false;
        private CacheEntry<K, V> previous;
        private CacheEntry<K, V> next;

        private CacheEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K key() {
            return this.key;
        }

        V value() {
            return this.value;
        }

        boolean evicted() {
            return this.evicted;
        }

        void isEvicted(boolean evicted) {
            this.evicted = evicted;
        }

        CacheEntry<K, V> next() {
            return this.next;
        }

        void setNext(CacheEntry<K, V> next) {
            this.next = next;
        }

        CacheEntry<K, V> previous() {
            return this.previous;
        }

        void setPrevious(CacheEntry<K, V> previous) {
            this.previous = previous;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CacheEntry that = (CacheEntry)o;
            return Objects.equals(this.key, that.key) && Objects.equals(this.value, that.value);
        }

        public int hashCode() {
            int result = this.key != null ? this.key.hashCode() : 0;
            result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
            return result;
        }
    }

    public static final class Builder<K, V> {
        private final Function<K, V> supplier;
        private Integer maxSize;

        private Builder(Function<K, V> supplier) {
            this.supplier = supplier;
        }

        public Builder<K, V> maxSize(Integer maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public LruCache<K, V> build() {
            return new LruCache(this);
        }
    }
}

