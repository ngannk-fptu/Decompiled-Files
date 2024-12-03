/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.util.BundleClassLoaderAccessor
 *  com.atlassian.plugin.util.resource.AlternativeClassLoaderResourceLoader
 *  com.atlassian.plugin.util.resource.AlternativeResourceLoader
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.PreDestroy
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.FrameworkUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.hazelcast.serialization;

import com.atlassian.hazelcast.serialization.BundleKey;
import com.atlassian.plugin.osgi.util.BundleClassLoaderAccessor;
import com.atlassian.plugin.util.resource.AlternativeClassLoaderResourceLoader;
import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgiClassLoaderRegistry {
    private static final Logger log = LoggerFactory.getLogger(OsgiClassLoaderRegistry.class);
    private final ConcurrentMap<BundleKey, Integer> bundleToId = new ConcurrentHashMap<BundleKey, Integer>();
    private final ConcurrentMap<Integer, BundleKey> idToBundle = new ConcurrentHashMap<Integer, BundleKey>();
    private final ConcurrentMap<BundleKey, ClassLoader> bundleToClassLoader = new ConcurrentHashMap<BundleKey, ClassLoader>();
    private final ConcurrentMap<BundleKey, Optional<ClassLoader>> bundleToCompatibleClassLoader = new ConcurrentHashMap<BundleKey, Optional<ClassLoader>>();
    private final ClassLoader fallbackClassLoader;

    public OsgiClassLoaderRegistry() {
        this(OsgiClassLoaderRegistry.class.getClassLoader());
    }

    public OsgiClassLoaderRegistry(ClassLoader fallbackClassLoader) {
        this.fallbackClassLoader = (ClassLoader)Preconditions.checkNotNull((Object)fallbackClassLoader, (Object)"fallbackClassLoader");
    }

    @PreDestroy
    public void destroy() {
        this.bundleToClassLoader.clear();
        this.bundleToCompatibleClassLoader.clear();
    }

    public int getBundleId(Object value) {
        if (value == null) {
            return -1;
        }
        Bundle bundle = this.getBundle(value.getClass());
        if (bundle == null) {
            return -1;
        }
        Integer id = (Integer)this.bundleToId.get(new BundleKey(bundle));
        return id == null ? -1 : id;
    }

    @Nonnull
    public String getBundleName(int bundleId) {
        if (bundleId < 0) {
            return "system";
        }
        BundleKey key = (BundleKey)this.idToBundle.get(bundleId);
        return key == null ? "unknown" : key.toString();
    }

    public ClassLoader getClassLoader(int bundleId) {
        BundleKey key = (BundleKey)this.idToBundle.get(bundleId);
        if (key == null) {
            log.debug("Unknown bundle with id {}. Using fallbackClassLoader.", (Object)bundleId);
            return this.fallbackClassLoader;
        }
        ClassLoader result = (ClassLoader)this.bundleToClassLoader.get(key);
        if (result != null) {
            return result;
        }
        return this.getCompatibleClassLoader(key).orElse(this.fallbackClassLoader);
    }

    void register(Integer id, Bundle bundle) {
        BundleKey key = new BundleKey(bundle);
        this.bundleToId.put(key, id);
        this.idToBundle.put(id, key);
        this.bundleToClassLoader.put(key, BundleClassLoaderAccessor.getClassLoader((Bundle)bundle, (AlternativeResourceLoader)new AlternativeClassLoaderResourceLoader(this.getClass())));
        log.debug("Registered bundle {} under id {}", (Object)key, (Object)id);
        this.clearCompatibleClassLoaders(key);
    }

    void registerMapping(Integer id, BundleKey bundleKey, boolean override) {
        this.idToBundle.put(id, bundleKey);
        if (override) {
            this.bundleToId.put(bundleKey, id);
        } else {
            this.bundleToId.putIfAbsent(bundleKey, id);
        }
    }

    void unregister(Bundle bundle) {
        BundleKey key = new BundleKey((Bundle)Preconditions.checkNotNull((Object)bundle, (Object)"bundle"));
        Integer bundleId = (Integer)this.bundleToId.get(key);
        if (bundleId != null && this.bundleToClassLoader.remove(key) != null) {
            this.clearCompatibleClassLoaders(key);
        }
    }

    @VisibleForTesting
    protected Bundle getBundle(Class<?> clazz) {
        return FrameworkUtil.getBundle(clazz);
    }

    private void clearCompatibleClassLoaders(BundleKey key) {
        this.bundleToCompatibleClassLoader.keySet().removeIf(bundleKey -> this.isCompatible((BundleKey)bundleKey, key));
    }

    private SortedSet<BundleKey> findCompatibleBundles(BundleKey key) {
        TreeSet<BundleKey> mappings = new TreeSet<BundleKey>();
        for (BundleKey bundleKey : this.bundleToId.keySet()) {
            if (!this.isCompatible(bundleKey, key)) continue;
            mappings.add(bundleKey);
        }
        for (BundleKey bundleKey : this.idToBundle.values()) {
            if (!this.isCompatible(bundleKey, key)) continue;
            mappings.add(bundleKey);
        }
        return mappings;
    }

    private Optional<ClassLoader> getCompatibleClassLoader(BundleKey bundleKey) {
        ClassLoader classLoader;
        Optional<ClassLoader> result = (Optional<ClassLoader>)this.bundleToCompatibleClassLoader.get(bundleKey);
        if (result != null) {
            return result;
        }
        SortedSet<BundleKey> compatibleBundles = this.findCompatibleBundles(bundleKey);
        for (BundleKey key : compatibleBundles.tailSet(bundleKey)) {
            classLoader = (ClassLoader)this.bundleToClassLoader.get(key);
            if (classLoader == null) continue;
            result = Optional.of(classLoader);
            break;
        }
        if (result == null) {
            for (BundleKey key : compatibleBundles.headSet(bundleKey)) {
                classLoader = (ClassLoader)this.bundleToClassLoader.get(key);
                if (classLoader == null) continue;
                result = Optional.of(classLoader);
            }
        }
        if (result == null) {
            result = Optional.empty();
        }
        this.bundleToCompatibleClassLoader.putIfAbsent(bundleKey, result);
        return result;
    }

    private boolean isCompatible(BundleKey value, BundleKey bundleKey) {
        return bundleKey.getSymbolicName().equals(value.getSymbolicName());
    }
}

