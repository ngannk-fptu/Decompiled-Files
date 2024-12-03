/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.integration;

import java.util.Collection;
import javax.cache.Cache;
import javax.cache.integration.CacheWriterException;

public interface CacheWriter<K, V> {
    public void write(Cache.Entry<? extends K, ? extends V> var1) throws CacheWriterException;

    public void writeAll(Collection<Cache.Entry<? extends K, ? extends V>> var1) throws CacheWriterException;

    public void delete(Object var1) throws CacheWriterException;

    public void deleteAll(Collection<?> var1) throws CacheWriterException;
}

