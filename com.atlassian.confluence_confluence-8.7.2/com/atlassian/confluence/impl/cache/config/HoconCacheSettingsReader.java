/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheException
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CacheSettingsDefaultsProvider
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.dc.filestore.api.FileStore$Reader
 *  com.google.common.base.Preconditions
 *  com.typesafe.config.Config
 *  com.typesafe.config.ConfigFactory
 *  io.atlassian.util.concurrent.LazyReference
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.InputStreamSource
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.impl.cache.config;

import com.atlassian.cache.CacheException;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CacheSettingsDefaultsProvider;
import com.atlassian.dc.filestore.api.FileStore;
import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.atlassian.util.concurrent.LazyReference;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;

public final class HoconCacheSettingsReader
implements CacheSettingsDefaultsProvider {
    private static final Logger log = LoggerFactory.getLogger(HoconCacheSettingsReader.class);
    private static final int MIN_CACHE_SIZE = 50;
    private final Supplier<Config> configRef;

    public HoconCacheSettingsReader(FileStore.Path configOverridesPath, Resource defaultConfigResource) {
        this.configRef = HoconCacheSettingsReader.createLazyConfigRef(configOverridesPath, defaultConfigResource);
    }

    private static Supplier<Config> createLazyConfigRef(final FileStore.Path configOverridesPath, final Resource defaultConfigResource) {
        return new LazyReference<Config>(){

            protected Config create() throws IOException {
                try (InputStream reader = HoconCacheSettingsReader.getConfigReader(configOverridesPath, defaultConfigResource).getInputStream();){
                    Config config = ConfigFactory.parseReader((Reader)new InputStreamReader(reader, StandardCharsets.UTF_8)).resolve();
                    return config;
                }
            }
        };
    }

    public CacheSettings getDefaults(@NonNull String name) {
        return HoconCacheSettingsReader.buildCacheSettings(this.getBestMatchingConfig(name));
    }

    private Config getBestMatchingConfig(String cacheName) {
        List cacheConfigs = this.configRef.get().getConfigList("caches");
        return cacheConfigs.stream().filter(cacheConfig -> cacheName.matches(HoconCacheSettingsReader.getCacheNameRegex(cacheConfig))).findFirst().map(cacheConfig -> this.validate(cacheName, (Config)cacheConfig)).orElseThrow(() -> {
            log.error("Unable to find matching definition for cache '{}'", (Object)cacheName);
            return new CacheException("Unable to find definition for " + cacheName);
        });
    }

    private static String getCacheNameRegex(Config cacheConfig) {
        String candidate = cacheConfig.getString("name");
        return candidate.endsWith("*") ? "^" + Pattern.quote(candidate.substring(0, candidate.length() - 1)) + ".*$" : "^" + Pattern.quote(candidate) + "$";
    }

    private Config validate(String cacheName, Config cacheConfig) {
        Config result = Objects.requireNonNull(cacheConfig.getConfig("config"));
        log.debug("Best match for cache {} is config {}", (Object)cacheName, (Object)cacheConfig.getString("name"));
        Preconditions.checkArgument((boolean)HoconCacheSettingsReader.isValidConfig(cacheConfig), (Object)"Cache config file is incorrect");
        return result;
    }

    private static @NonNull CacheSettings buildCacheSettings(Config cfg) {
        CacheSettingsBuilder builder = new CacheSettingsBuilder();
        if (cfg.hasPath(ConfigKeys.EXPIRE_AFTER_ACCESS_SECS.getKey())) {
            builder.expireAfterAccess(cfg.getLong(ConfigKeys.EXPIRE_AFTER_ACCESS_SECS.getKey()), TimeUnit.SECONDS);
        }
        if (cfg.hasPath(ConfigKeys.EXPIRE_AFTER_WRITE_SECS.getKey())) {
            builder.expireAfterWrite(cfg.getLong(ConfigKeys.EXPIRE_AFTER_WRITE_SECS.getKey()), TimeUnit.SECONDS);
        }
        if (cfg.hasPath(ConfigKeys.FLUSHABLE.getKey())) {
            if (cfg.getBoolean(ConfigKeys.FLUSHABLE.getKey())) {
                builder.flushable();
            } else {
                builder.unflushable();
            }
        }
        if (cfg.hasPath(ConfigKeys.LOCAL.getKey())) {
            if (cfg.getBoolean(ConfigKeys.LOCAL.getKey())) {
                builder.local();
            } else {
                builder.remote();
            }
        }
        if (cfg.hasPath(ConfigKeys.MAX_ENTRIES.getKey())) {
            builder.maxEntries(Math.max(50, cfg.getInt(ConfigKeys.MAX_ENTRIES.getKey())));
        }
        if (cfg.hasPath(ConfigKeys.REPLICATE_ASYNC.getKey())) {
            if (cfg.getBoolean(ConfigKeys.REPLICATE_ASYNC.getKey())) {
                builder.replicateAsynchronously();
            } else {
                builder.replicateSynchronously();
            }
        }
        if (cfg.hasPath(ConfigKeys.REPLICATE_VIA_COPY.getKey())) {
            if (cfg.getBoolean(ConfigKeys.REPLICATE_VIA_COPY.getKey())) {
                builder.replicateViaCopy();
            } else {
                builder.replicateViaInvalidation();
            }
        }
        return builder.build();
    }

    private static boolean isValidConfig(Config cacheConfig) {
        return cacheConfig.entrySet().stream().map(Map.Entry::getKey).allMatch(entry -> entry.equals("name") || entry.startsWith("config"));
    }

    private static InputStreamSource getConfigReader(FileStore.Path configOverridesPath, Resource defaultConfigResource) throws IOException {
        if (configOverridesPath.fileExists()) {
            log.info("Reading {}", (Object)configOverridesPath);
            return () -> ((FileStore.Reader)configOverridesPath.fileReader()).openInputStream();
        }
        return defaultConfigResource;
    }

    private static enum ConfigKeys {
        MAX_ENTRIES("max-entries"),
        REPLICATE_ASYNC("replicate-async"),
        REPLICATE_VIA_COPY("replicate-via-copy"),
        EXPIRE_AFTER_ACCESS_SECS("expire-after-access-secs"),
        EXPIRE_AFTER_WRITE_SECS("expire-after-write-secs"),
        FLUSHABLE("flushable"),
        LOCAL("local");

        private final String key;

        private ConfigKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}

