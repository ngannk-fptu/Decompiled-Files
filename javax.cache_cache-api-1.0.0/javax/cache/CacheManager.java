/*
 * Decompiled with CFR 0.152.
 */
package javax.cache;

import java.io.Closeable;
import java.net.URI;
import java.util.Properties;
import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;

public interface CacheManager
extends Closeable {
    public CachingProvider getCachingProvider();

    public URI getURI();

    public ClassLoader getClassLoader();

    public Properties getProperties();

    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String var1, C var2) throws IllegalArgumentException;

    public <K, V> Cache<K, V> getCache(String var1, Class<K> var2, Class<V> var3);

    public <K, V> Cache<K, V> getCache(String var1);

    public Iterable<String> getCacheNames();

    public void destroyCache(String var1);

    public void enableManagement(String var1, boolean var2);

    public void enableStatistics(String var1, boolean var2);

    @Override
    public void close();

    public boolean isClosed();

    public <T> T unwrap(Class<T> var1);
}

