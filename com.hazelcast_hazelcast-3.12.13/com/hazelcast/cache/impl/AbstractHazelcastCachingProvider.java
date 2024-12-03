/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 *  javax.cache.CacheManager
 *  javax.cache.configuration.OptionalFeature
 *  javax.cache.spi.CachingProvider
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.HazelcastCachingProvider;
import com.hazelcast.cache.impl.AbstractHazelcastCacheManager;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.StringUtil;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;

public abstract class AbstractHazelcastCachingProvider
implements CachingProvider {
    public static final String SHARED_JCACHE_INSTANCE_NAME = "_hzinstance_jcache_shared";
    public static final String NAMED_JCACHE_HZ_INSTANCE = "hazelcast.named.jcache.instance";
    protected static final ILogger LOGGER = Logger.getLogger(HazelcastCachingProvider.class);
    private static final String INVALID_HZ_INSTANCE_SPECIFICATION_MESSAGE = "No available Hazelcast instance. Please specify your Hazelcast configuration file path via \"HazelcastCachingProvider.HAZELCAST_CONFIG_LOCATION\" property or specify Hazelcast instance name via \"HazelcastCachingProvider.HAZELCAST_INSTANCE_NAME\" property in the \"properties\" parameter.";
    private static final Set<String> SUPPORTED_SCHEMES;
    protected final boolean namedDefaultHzInstance = Boolean.parseBoolean(System.getProperty("hazelcast.named.jcache.instance", "true"));
    protected volatile HazelcastInstance hazelcastInstance;
    private final ClassLoader defaultClassLoader;
    private final URI defaultURI;
    private final Map<ClassLoader, Map<URI, AbstractHazelcastCacheManager>> cacheManagers = new WeakHashMap<ClassLoader, Map<URI, AbstractHazelcastCacheManager>>();

    protected AbstractHazelcastCachingProvider() {
        this.defaultClassLoader = this.getClass().getClassLoader();
        try {
            this.defaultURI = new URI("hazelcast");
        }
        catch (URISyntaxException e) {
            throw new CacheException("Cannot create default URI", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        URI managerURI = this.getManagerUri(uri);
        ClassLoader managerClassLoader = this.getManagerClassLoader(classLoader);
        Properties managerProperties = properties == null ? new Properties() : properties;
        Map<ClassLoader, Map<URI, AbstractHazelcastCacheManager>> map = this.cacheManagers;
        synchronized (map) {
            AbstractHazelcastCacheManager cacheManager;
            Map<URI, AbstractHazelcastCacheManager> cacheManagersByURI = this.cacheManagers.get(managerClassLoader);
            if (cacheManagersByURI == null) {
                cacheManagersByURI = new HashMap<URI, AbstractHazelcastCacheManager>();
                this.cacheManagers.put(managerClassLoader, cacheManagersByURI);
            }
            if ((cacheManager = cacheManagersByURI.get(managerURI)) == null || cacheManager.isClosed()) {
                try {
                    cacheManager = this.createHazelcastCacheManager(uri, classLoader, managerProperties);
                    cacheManagersByURI.put(managerURI, cacheManager);
                }
                catch (Exception e) {
                    throw new CacheException("Error opening URI [" + managerURI.toString() + ']', (Throwable)e);
                }
            }
            return cacheManager;
        }
    }

    public ClassLoader getDefaultClassLoader() {
        return this.defaultClassLoader;
    }

    public URI getDefaultURI() {
        return this.defaultURI;
    }

    public Properties getDefaultProperties() {
        return null;
    }

    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
        return this.getCacheManager(uri, classLoader, null);
    }

    public CacheManager getCacheManager() {
        return this.getCacheManager(null, null, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        Map<ClassLoader, Map<URI, AbstractHazelcastCacheManager>> map = this.cacheManagers;
        synchronized (map) {
            for (Map<URI, AbstractHazelcastCacheManager> cacheManagersByURI : this.cacheManagers.values()) {
                for (AbstractHazelcastCacheManager cacheManager : cacheManagersByURI.values()) {
                    if (cacheManager.isDefaultClassLoader) {
                        cacheManager.close();
                        continue;
                    }
                    cacheManager.destroy();
                }
            }
        }
        this.cacheManagers.clear();
        this.shutdownHazelcastInstance();
    }

    private void shutdownHazelcastInstance() {
        HazelcastInstance localInstanceRef = this.hazelcastInstance;
        if (localInstanceRef != null) {
            localInstanceRef.shutdown();
        }
        this.hazelcastInstance = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close(ClassLoader classLoader) {
        ClassLoader managerClassLoader = this.getManagerClassLoader(classLoader);
        Map<ClassLoader, Map<URI, AbstractHazelcastCacheManager>> map = this.cacheManagers;
        synchronized (map) {
            Map<URI, AbstractHazelcastCacheManager> cacheManagersByURI = this.cacheManagers.get(managerClassLoader);
            if (cacheManagersByURI != null) {
                for (CacheManager cacheManager : cacheManagersByURI.values()) {
                    cacheManager.close();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close(URI uri, ClassLoader classLoader) {
        URI managerURI = this.getManagerUri(uri);
        ClassLoader managerClassLoader = this.getManagerClassLoader(classLoader);
        Map<ClassLoader, Map<URI, AbstractHazelcastCacheManager>> map = this.cacheManagers;
        synchronized (map) {
            Map<URI, AbstractHazelcastCacheManager> cacheManagersByURI = this.cacheManagers.get(managerClassLoader);
            if (cacheManagersByURI != null) {
                CacheManager cacheManager = cacheManagersByURI.remove(managerURI);
                if (cacheManager != null) {
                    cacheManager.close();
                }
                if (cacheManagersByURI.isEmpty()) {
                    this.cacheManagers.remove(classLoader);
                }
            }
        }
    }

    public boolean isSupported(OptionalFeature optionalFeature) {
        switch (optionalFeature) {
            case STORE_BY_REFERENCE: {
                return false;
            }
        }
        return false;
    }

    private URI getManagerUri(URI uri) {
        return uri == null ? this.defaultURI : uri;
    }

    private ClassLoader getManagerClassLoader(ClassLoader classLoader) {
        return classLoader == null ? this.defaultClassLoader : classLoader;
    }

    private <T extends AbstractHazelcastCacheManager> T createHazelcastCacheManager(URI uri, ClassLoader classLoader, Properties managerProperties) {
        HazelcastInstance instance;
        try {
            instance = this.getOrCreateInstance(uri, classLoader, managerProperties);
            if (instance == null) {
                throw new IllegalArgumentException(INVALID_HZ_INSTANCE_SPECIFICATION_MESSAGE);
            }
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        return this.createCacheManager(instance, uri, classLoader, managerProperties);
    }

    protected abstract HazelcastInstance getOrCreateInstance(URI var1, ClassLoader var2, Properties var3) throws URISyntaxException, IOException;

    protected abstract <T extends AbstractHazelcastCacheManager> T createCacheManager(HazelcastInstance var1, URI var2, ClassLoader var3, Properties var4);

    protected boolean isConfigLocation(URI location) {
        String scheme = location.getScheme();
        if (scheme == null) {
            try {
                String resolvedPlaceholder = System.getProperty(location.getRawSchemeSpecificPart());
                if (resolvedPlaceholder == null) {
                    return false;
                }
                location = new URI(resolvedPlaceholder);
                scheme = location.getScheme();
            }
            catch (URISyntaxException e) {
                return false;
            }
        }
        return scheme != null && SUPPORTED_SCHEMES.contains(scheme.toLowerCase(StringUtil.LOCALE_INTERNAL));
    }

    static {
        HashSet<String> supportedSchemes = new HashSet<String>();
        supportedSchemes.add("classpath");
        supportedSchemes.add("file");
        supportedSchemes.add("http");
        supportedSchemes.add("https");
        SUPPORTED_SCHEMES = supportedSchemes;
    }
}

