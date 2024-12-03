/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.spi;

import java.io.Closeable;
import java.net.URI;
import java.util.Properties;
import javax.cache.CacheManager;
import javax.cache.configuration.OptionalFeature;

public interface CachingProvider
extends Closeable {
    public CacheManager getCacheManager(URI var1, ClassLoader var2, Properties var3);

    public ClassLoader getDefaultClassLoader();

    public URI getDefaultURI();

    public Properties getDefaultProperties();

    public CacheManager getCacheManager(URI var1, ClassLoader var2);

    public CacheManager getCacheManager();

    @Override
    public void close();

    public void close(ClassLoader var1);

    public void close(URI var1, ClassLoader var2);

    public boolean isSupported(OptionalFeature var1);
}

