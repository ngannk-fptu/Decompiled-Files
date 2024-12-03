/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gadgets.metadata;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.plugins.gadgets.events.GadgetCacheFlushBlockedEvent;
import com.atlassian.confluence.plugins.gadgets.events.GadgetCacheFlushEvent;
import com.atlassian.confluence.plugins.gadgets.metadata.GadgetUsageTracker;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GadgetCacheFlusher {
    private static final Logger log = LoggerFactory.getLogger(GadgetCacheFlusher.class);
    static final String CACHE_NAME_PREFIX = "com.atlassian.gadgets.renderer.internal.cache.";
    private final CacheManager cacheManager;
    private final GadgetUsageTracker gadgetUsageTracker;
    private final EventPublisher eventPublisher;

    GadgetCacheFlusher(CacheManager cacheManager, GadgetUsageTracker gadgetUsageTracker, EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.cacheManager = (CacheManager)Preconditions.checkNotNull((Object)cacheManager);
        this.gadgetUsageTracker = (GadgetUsageTracker)Preconditions.checkNotNull((Object)gadgetUsageTracker);
    }

    void requestGadgetsCacheFlush() {
        if (!this.gadgetUsageTracker.shouldGadgetsCacheFlushingBePrevented()) {
            int keysFlushedCount = this.performSelectiveGadgetsCacheFlush();
            this.eventPublisher.publish((Object)new GadgetCacheFlushEvent(keysFlushedCount));
        } else {
            log.warn("Skipping flushing of gadgets cache due to prior cross-gadgets activity. This may significantly increase memory usage.");
            this.eventPublisher.publish((Object)new GadgetCacheFlushBlockedEvent());
        }
    }

    private int performSelectiveGadgetsCacheFlush() {
        int keysFlushedCount = 0;
        log.debug("Flushing gadgets caches");
        for (ManagedCache managedCache : this.getGadgetsCaches()) {
            String cacheName = managedCache.getName();
            Cache cache = this.cacheManager.getCache(cacheName);
            ArrayList allKeys = Lists.newArrayList((Iterable)cache.getKeys());
            ArrayList keysToDelete = Lists.newArrayList((Iterable)allKeys);
            keysToDelete.removeAll(this.getKeysToRetain(allKeys));
            log.debug("Deleting {} entries out of {} from cache [{}]", new Object[]{keysToDelete.size(), allKeys.size(), cache.getName()});
            for (Object keyToDelete : keysToDelete) {
                cache.remove(keyToDelete);
                ++keysFlushedCount;
            }
        }
        return keysFlushedCount;
    }

    private Collection getKeysToRetain(Collection keys) {
        return Collections2.filter((Collection)keys, (Predicate)new Predicate(){

            public boolean apply(Object input) {
                return GadgetCacheFlusher.this.matchesAnyGadgetInUse(input.toString().toLowerCase());
            }
        });
    }

    private boolean matchesAnyGadgetInUse(String keyString) {
        for (URI uri : this.gadgetUsageTracker.getGadgetUrisInUse()) {
            if (!GadgetCacheFlusher.keyStringMatchesGadgetUri(keyString, uri)) continue;
            return true;
        }
        return false;
    }

    private static boolean keyStringMatchesGadgetUri(String keyString, URI uri) {
        return keyString.contains(uri.toString().toLowerCase());
    }

    private Set<ManagedCache> getGadgetsCaches() {
        HashSet caches = Sets.newHashSet();
        for (ManagedCache cache : this.cacheManager.getManagedCaches()) {
            if (!cache.getName().startsWith(CACHE_NAME_PREFIX)) continue;
            caches.add(cache);
        }
        return caches;
    }
}

