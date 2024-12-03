/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.plugin.manager.PluginPersistentState
 *  com.atlassian.plugin.manager.PluginPersistentStateStore
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.plugin;

import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CachedReference;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.plugin.manager.PluginPersistentState;
import com.atlassian.plugin.manager.PluginPersistentStateStore;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingPluginPersistentStateStore
implements PluginPersistentStateStore {
    private static final Logger log = LoggerFactory.getLogger(CachingPluginPersistentStateStore.class);
    private final PluginPersistentStateStore delegate;
    private final CachedReference<PluginPersistentState> cache;

    public CachingPluginPersistentStateStore(PluginPersistentStateStore delegate, CacheFactory cacheFactory) {
        this.delegate = Objects.requireNonNull(delegate);
        this.cache = CoreCache.PLUGIN_PERSISTENT_STATE.resolve(cacheName -> cacheFactory.getCachedReference(cacheName, this::loadFromDelegate));
    }

    public void save(PluginPersistentState state) {
        log.debug("Saving plugin state {}", (Object)Objects.requireNonNull(state));
        this.delegate.save(state);
        this.cache.reset();
    }

    public PluginPersistentState load() {
        return (PluginPersistentState)this.cache.get();
    }

    private PluginPersistentState loadFromDelegate() {
        PluginPersistentState state = this.delegate.load();
        log.debug("Loaded plugin state from delegate: {}", (Object)state);
        return state;
    }
}

