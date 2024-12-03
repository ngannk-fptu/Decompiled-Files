/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  io.atlassian.fugue.Option
 *  org.apache.commons.lang3.Validate
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.confluence.pluginsettings;

import com.atlassian.cache.Cache;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import io.atlassian.fugue.Option;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class CachingPluginSettings
implements PluginSettings {
    private static final Logger log = LoggerFactory.getLogger(CachingPluginSettings.class);
    private final Cache<CacheKey, Option<Object>> cache;
    private final PluginSettings delegate;
    private final Serializable contextKey;

    public CachingPluginSettings(Cache<CacheKey, Option<Object>> cache, PluginSettings delegate, Serializable contextKey) {
        this.cache = Objects.requireNonNull(cache);
        this.delegate = Objects.requireNonNull(delegate);
        this.contextKey = Objects.requireNonNull(contextKey);
    }

    public Object get(@Nullable String key) {
        this.assertKeyIsNotNull(key);
        Object result = ((Option)this.cache.get((Object)this.cacheKey(key), () -> Option.option((Object)this.delegate.get(key)))).getOrNull();
        log.debug("get {}:{} = {}", new Object[]{this.contextKey, key, result});
        return result;
    }

    public Object put(String key, Object value) {
        this.assertKeyIsNotNull(key);
        log.debug("put {}:{} = {}", new Object[]{this.contextKey, key, value});
        Object result = this.delegate.put(key, value);
        CacheKey cacheKey = this.cacheKey(key);
        this.cache.remove((Object)cacheKey);
        this.get(key);
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove(String key) {
        this.assertKeyIsNotNull(key);
        try {
            log.debug("remove {}:{}", (Object)this.contextKey, (Object)key);
            Object object = this.delegate.remove(key);
            return object;
        }
        finally {
            CacheKey cacheKey = this.cacheKey(key);
            this.cache.remove((Object)cacheKey);
            this.cache.get((Object)cacheKey, Option::none);
        }
    }

    private void assertKeyIsNotNull(@Nullable String key) {
        Validate.isTrue((key != null ? 1 : 0) != 0, (String)"The plugin settings key cannot be null", (Object[])new Object[0]);
    }

    private CacheKey cacheKey(String settingKey) {
        return new CacheKey(this.contextKey, settingKey);
    }

    static final class CacheKey
    implements Serializable {
        final Serializable contextKey;
        final String settingKey;

        CacheKey(Serializable contextKey, String settingKey) {
            this.contextKey = Objects.requireNonNull(contextKey);
            this.settingKey = Objects.requireNonNull(settingKey);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CacheKey cacheKey = (CacheKey)o;
            return this.contextKey.equals(cacheKey.contextKey) && this.settingKey.equals(cacheKey.settingKey);
        }

        public int hashCode() {
            return Objects.hash(this.contextKey, this.settingKey);
        }
    }
}

