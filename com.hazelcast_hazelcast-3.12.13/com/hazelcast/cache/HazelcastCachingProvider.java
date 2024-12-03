/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 *  javax.cache.CacheManager
 *  javax.cache.configuration.OptionalFeature
 *  javax.cache.spi.CachingProvider
 */
package com.hazelcast.cache;

import com.hazelcast.cache.impl.HazelcastServerCachingProvider;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.spi.properties.GroupProperty;
import java.net.URI;
import java.util.Properties;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;

public final class HazelcastCachingProvider
implements CachingProvider {
    public static final String HAZELCAST_CONFIG_LOCATION = "hazelcast.config.location";
    public static final String HAZELCAST_INSTANCE_NAME = "hazelcast.instance.name";
    public static final String HAZELCAST_INSTANCE_ITSELF = "hazelcast.instance.itself";
    private static final String CLIENT_CACHING_PROVIDER_CLASS = "com.hazelcast.client.cache.impl.HazelcastClientCachingProvider";
    private static final ILogger LOGGER = Logger.getLogger(HazelcastCachingProvider.class);
    private final CachingProvider delegate;

    public HazelcastCachingProvider() {
        CachingProvider cp = null;
        String providerType = GroupProperty.JCACHE_PROVIDER_TYPE.getSystemProperty();
        if (providerType != null) {
            if ("client".equals(providerType)) {
                cp = this.createClientProvider();
            }
            if ("server".equals(providerType)) {
                cp = new HazelcastServerCachingProvider();
            }
            if (cp == null) {
                throw new CacheException("CacheProvider cannot be created with the provided type: " + providerType);
            }
        } else {
            cp = this.createClientProvider();
            if (cp == null) {
                cp = new HazelcastServerCachingProvider();
            }
        }
        this.delegate = cp;
    }

    private CachingProvider createClientProvider() {
        try {
            return (CachingProvider)ClassLoaderUtil.newInstance(this.getClass().getClassLoader(), CLIENT_CACHING_PROVIDER_CLASS);
        }
        catch (Exception e) {
            LOGGER.finest("Could not load client CachingProvider! Fallback to server one... " + e.toString());
            return null;
        }
    }

    public static Properties propertiesByLocation(String configFileLocation) {
        Properties properties = new Properties();
        properties.setProperty(HAZELCAST_CONFIG_LOCATION, configFileLocation);
        return properties;
    }

    public static Properties propertiesByInstanceName(String instanceName) {
        Properties properties = new Properties();
        properties.setProperty(HAZELCAST_INSTANCE_NAME, instanceName);
        return properties;
    }

    public static Properties propertiesByInstanceItself(HazelcastInstance instance) {
        Properties properties = new Properties();
        properties.put(HAZELCAST_INSTANCE_ITSELF, instance);
        return properties;
    }

    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        return this.delegate.getCacheManager(uri, classLoader, properties);
    }

    public ClassLoader getDefaultClassLoader() {
        return this.delegate.getDefaultClassLoader();
    }

    public URI getDefaultURI() {
        return this.delegate.getDefaultURI();
    }

    public Properties getDefaultProperties() {
        return this.delegate.getDefaultProperties();
    }

    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
        return this.delegate.getCacheManager(uri, classLoader);
    }

    public CacheManager getCacheManager() {
        return this.delegate.getCacheManager();
    }

    public void close() {
        this.delegate.close();
    }

    public void close(ClassLoader classLoader) {
        this.delegate.close(classLoader);
    }

    public void close(URI uri, ClassLoader classLoader) {
        this.delegate.close(uri, classLoader);
    }

    public boolean isSupported(OptionalFeature optionalFeature) {
        return this.delegate.isSupported(optionalFeature);
    }

    public String toString() {
        return "HazelcastCachingProvider{delegate=" + this.delegate + '}';
    }
}

