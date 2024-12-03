/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cache.config;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.cache.CacheSettingsManager;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertiesFileCacheSettingsManager
implements CacheSettingsManager {
    private static final Logger log = LoggerFactory.getLogger(PropertiesFileCacheSettingsManager.class);
    private static final String CACHE_MAX_ENTRIES_LOCAL_HEAP_CONFIG_KEY_PREFIX = "cache.maxEntriesLocalHeap.";
    private static final String CACHE_REPLICATE_VIA_INVALIDATION_CONFIG_KEY_PREFIX = "cache.replicateViaInvalidation.";
    private static final String PROPERTIES_FILE_COMMENT = "Saved by " + PropertiesFileCacheSettingsManager.class.getName();
    private final ResettableLazyReference<Properties> propertiesRef;
    private final FileStore.Path settingsFile;
    private final ClusterLockService clusterLockService;

    public PropertiesFileCacheSettingsManager(FileStore.Path settingsFile, ClusterLockService clusterLockService) {
        this.clusterLockService = Objects.requireNonNull(clusterLockService);
        this.settingsFile = Objects.requireNonNull(settingsFile);
        this.propertiesRef = new ResettableLazyReference<Properties>(){

            protected Properties create() throws Exception {
                return PropertiesFileCacheSettingsManager.this.loadCustomSettings();
            }
        };
    }

    private Properties getProperties() {
        return (Properties)this.propertiesRef.get();
    }

    @Override
    public Optional<Integer> changeMaxEntries(@Nonnull String name, int newValue) {
        Objects.requireNonNull(name, "Cache Name can't be null");
        Preconditions.checkArgument((newValue >= 0 ? 1 : 0) != 0, (Object)"Cache Size can't be negative");
        String newStringValue = String.valueOf(newValue);
        Object previousValue = this.getProperties().put(CACHE_MAX_ENTRIES_LOCAL_HEAP_CONFIG_KEY_PREFIX + name, newStringValue);
        Integer previousIntegerValue = previousValue == null ? null : Integer.valueOf(previousValue.toString());
        return Optional.ofNullable(previousIntegerValue);
    }

    @Override
    public boolean saveSettings() {
        return this.saveSettings(this.getProperties());
    }

    @Override
    public void reloadSettings() {
        this.propertiesRef.reset();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean saveSettings(Properties properties) {
        ClusterLock writeLock = this.clusterLockService.getLockForName(this.settingsFile.getPathName());
        try {
            if (writeLock.tryLock()) {
                boolean bl = this.storeConfiguration(properties);
                return bl;
            }
            log.error("Failure obtaining [{}] cluster lock for persisting changes", (Object)writeLock);
            boolean bl = false;
            return bl;
        }
        finally {
            writeLock.unlock();
        }
    }

    private boolean storeConfiguration(Properties properties) {
        log.info("Writing new config override to [{}]", (Object)this.settingsFile);
        try {
            this.settingsFile.fileWriter().write(buffer -> properties.store(buffer, PROPERTIES_FILE_COMMENT));
        }
        catch (IOException e) {
            log.error("Error saving settings", (Throwable)e);
            return false;
        }
        return true;
    }

    @Nonnull
    public CacheSettings getDefaults(@Nonnull String cacheName) {
        CacheSettingsBuilder builder = new CacheSettingsBuilder();
        this.populateMaxEntries(cacheName, builder);
        this.populateReplicationMode(cacheName, builder);
        return builder.build();
    }

    private void populateMaxEntries(@Nonnull String cacheName, CacheSettingsBuilder builder) {
        this.getOverridePropertyKey(cacheName, CACHE_MAX_ENTRIES_LOCAL_HEAP_CONFIG_KEY_PREFIX).map(propertyKey -> Integer.valueOf(this.getProperties().get(propertyKey).toString())).ifPresent(arg_0 -> ((CacheSettingsBuilder)builder).maxEntries(arg_0));
    }

    private void populateReplicationMode(@Nonnull String cacheName, CacheSettingsBuilder builder) {
        this.getOverridePropertyKey(cacheName, CACHE_REPLICATE_VIA_INVALIDATION_CONFIG_KEY_PREFIX).map(propertyKey -> Boolean.parseBoolean(this.getProperties().get(propertyKey).toString())).ifPresent(enabled -> {
            log.warn("Overriding replicate-via-invalidation={} will change caching behaviour and may introduce problems (cache '{}')", enabled, (Object)cacheName);
            if (enabled.booleanValue()) {
                builder.replicateViaInvalidation();
            } else {
                builder.replicateViaCopy();
            }
        });
    }

    private Optional<String> getOverridePropertyKey(String name, String prefix) {
        if (this.getProperties().containsKey(prefix + name)) {
            return Optional.of(prefix + name);
        }
        if (this.getProperties().containsKey(prefix + name + "_v5")) {
            return Optional.of(prefix + name + "_v5");
        }
        return Optional.empty();
    }

    private Properties loadCustomSettings() throws IOException {
        Properties properties = new Properties();
        if (!this.settingsFile.fileExists()) {
            return properties;
        }
        Properties overridesProperties = new Properties();
        this.settingsFile.fileReader().consume(overridesProperties::load);
        properties.putAll((Map<?, ?>)overridesProperties);
        PropertiesFileCacheSettingsManager.removeV5SuffixFromCacheKeys(overridesProperties, updated -> {
            properties.clear();
            properties.putAll((Map<?, ?>)updated);
            this.saveSettings(properties);
        });
        return properties;
    }

    private static void removeV5SuffixFromCacheKeys(Properties customCacheProperties, Consumer<Properties> maybeUpdatedCallback) {
        Properties updatedCustomCacheProperties = new Properties();
        Properties v5SuffixedCustomCacheProperties = new Properties();
        int processedCount = 0;
        int updatedCount = 0;
        for (String key : customCacheProperties.stringPropertyNames()) {
            if (key.endsWith("_v5")) {
                v5SuffixedCustomCacheProperties.setProperty(key.substring(0, key.length() - 3), customCacheProperties.getProperty(key));
                ++updatedCount;
            } else {
                updatedCustomCacheProperties.setProperty(key, customCacheProperties.getProperty(key));
            }
            ++processedCount;
        }
        log.info("Removed _v5 suffix from all Hibernate cache keys. {}/{} entries updated", (Object)updatedCount, (Object)processedCount);
        if (updatedCount > 0) {
            v5SuffixedCustomCacheProperties.stringPropertyNames().forEach(v5SuffixedCustomCacheKey -> updatedCustomCacheProperties.putIfAbsent(v5SuffixedCustomCacheKey, v5SuffixedCustomCacheProperties.get(v5SuffixedCustomCacheKey)));
            maybeUpdatedCallback.accept(updatedCustomCacheProperties);
        }
    }

    private static LazyReference<Properties> memoize(final Callable<Properties> c) {
        return new LazyReference<Properties>(){

            protected Properties create() throws Exception {
                return (Properties)c.call();
            }
        };
    }
}

