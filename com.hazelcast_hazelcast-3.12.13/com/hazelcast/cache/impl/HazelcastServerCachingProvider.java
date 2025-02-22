/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.AbstractHazelcastCacheManager;
import com.hazelcast.cache.impl.AbstractHazelcastCachingProvider;
import com.hazelcast.cache.impl.HazelcastServerCacheManager;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.StringUtil;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public final class HazelcastServerCachingProvider
extends AbstractHazelcastCachingProvider {
    public static HazelcastServerCachingProvider createCachingProvider(HazelcastInstance hazelcastInstance) {
        HazelcastServerCachingProvider cachingProvider = new HazelcastServerCachingProvider();
        cachingProvider.hazelcastInstance = hazelcastInstance;
        return cachingProvider;
    }

    @Override
    protected <T extends AbstractHazelcastCacheManager> T createCacheManager(HazelcastInstance instance, URI uri, ClassLoader classLoader, Properties properties) {
        return (T)new HazelcastServerCacheManager(this, instance, uri, classLoader, properties);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected HazelcastInstance getOrCreateInstance(URI uri, ClassLoader classLoader, Properties properties) throws URISyntaxException, IOException {
        boolean isDefaultURI;
        HazelcastInstance instanceItself = (HazelcastInstance)properties.get("hazelcast.instance.itself");
        if (instanceItself != null) {
            return instanceItself;
        }
        String location = properties.getProperty("hazelcast.config.location");
        String instanceName = properties.getProperty("hazelcast.instance.name");
        if (location != null) {
            Config config = this.getConfigFromLocation(location, classLoader, instanceName);
            return HazelcastInstanceFactory.getOrCreateHazelcastInstance(config);
        }
        if (instanceName != null) {
            return this.getOrCreateByInstanceName(instanceName);
        }
        boolean bl = isDefaultURI = uri == null || uri.equals(this.getDefaultURI());
        if (isDefaultURI) return this.getDefaultInstance();
        if (this.isConfigLocation(uri)) {
            try {
                Config config = this.getConfigFromLocation(uri, classLoader, null);
                return HazelcastInstanceFactory.getOrCreateHazelcastInstance(config);
            }
            catch (Exception e) {
                if (!LOGGER.isFinestEnabled()) return null;
                LOGGER.finest("Could not get or create Hazelcast instance from URI " + uri.toString(), e);
                return null;
            }
        } else {
            try {
                return this.getOrCreateByInstanceName(uri.toString());
            }
            catch (Exception e) {
                if (!LOGGER.isFinestEnabled()) return null;
                LOGGER.finest("Could not get Hazelcast instance from instance name " + uri.toString(), e);
            }
        }
        return null;
    }

    private HazelcastInstance getDefaultInstance() {
        if (this.hazelcastInstance == null) {
            Config config = this.getDefaultConfig();
            this.hazelcastInstance = StringUtil.isNullOrEmptyAfterTrim(config.getInstanceName()) ? Hazelcast.newHazelcastInstance() : Hazelcast.getOrCreateHazelcastInstance(config);
        }
        return this.hazelcastInstance;
    }

    private HazelcastInstance getOrCreateByInstanceName(String instanceName) {
        HazelcastInstance instance = Hazelcast.getHazelcastInstanceByName(instanceName);
        if (instance == null) {
            Config config = this.getDefaultConfig();
            config.setInstanceName(instanceName);
            instance = Hazelcast.getOrCreateHazelcastInstance(config);
        }
        return instance;
    }

    private Config getDefaultConfig() {
        Config config = new XmlConfigBuilder().build();
        if (this.namedDefaultHzInstance && StringUtil.isNullOrEmpty(config.getInstanceName())) {
            config.setInstanceName("_hzinstance_jcache_shared");
        }
        return config;
    }

    private Config getConfigFromLocation(String location, ClassLoader classLoader, String instanceName) throws URISyntaxException, IOException {
        URI uri = new URI(location);
        return this.getConfigFromLocation(uri, classLoader, instanceName);
    }

    private Config getConfigFromLocation(URI location, ClassLoader classLoader, String instanceName) throws URISyntaxException, IOException {
        URL configURL;
        ClassLoader theClassLoader;
        String scheme = location.getScheme();
        if (scheme == null) {
            location = new URI(System.getProperty(location.getRawSchemeSpecificPart()));
            scheme = location.getScheme();
        }
        ClassLoader classLoader2 = theClassLoader = classLoader == null ? this.getDefaultClassLoader() : classLoader;
        if ("classpath".equals(scheme)) {
            configURL = theClassLoader.getResource(location.getRawSchemeSpecificPart());
        } else if ("file".equals(scheme) || "http".equals(scheme) || "https".equals(scheme)) {
            configURL = location.toURL();
        } else {
            throw new URISyntaxException(location.toString(), "Unsupported protocol in configuration location URL");
        }
        try {
            return this.getConfig(configURL, theClassLoader, instanceName);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private Config getConfig(URL configURL, ClassLoader theClassLoader, String instanceName) throws IOException {
        Config config = new XmlConfigBuilder(configURL).build().setClassLoader(theClassLoader);
        if (instanceName != null) {
            config.setInstanceName(instanceName);
        } else if (config.getInstanceName() == null) {
            config.setInstanceName(configURL.toString());
        }
        return config;
    }

    public String toString() {
        return "HazelcastServerCachingProvider{hazelcastInstance=" + this.hazelcastInstance + '}';
    }
}

