/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.event.events.admin.MacroMetadataChangedEvent
 *  com.atlassian.confluence.macro.browser.MacroMetadataProvider
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  com.atlassian.confluence.macro.browser.beans.MacroSummary
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.GadgetSpecProvider
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore
 *  com.atlassian.gadgets.feed.GadgetFeedReaderFactory
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.plugin.event.events.PluginUninstalledEvent
 *  com.atlassian.plugin.event.events.PluginUpgradedEvent
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.gadgets.metadata;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.event.events.admin.MacroMetadataChangedEvent;
import com.atlassian.confluence.macro.browser.MacroMetadataProvider;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import com.atlassian.confluence.plugins.gadgets.events.GadgetInstalledEvent;
import com.atlassian.confluence.plugins.gadgets.events.GadgetUninstalledEvent;
import com.atlassian.confluence.plugins.gadgets.metadata.CachedMetadata;
import com.atlassian.confluence.plugins.gadgets.metadata.GadgetCacheFlusher;
import com.atlassian.confluence.plugins.gadgets.metadata.GadgetUsageTracker;
import com.atlassian.confluence.plugins.gadgets.metadata.GadgetsMacroMetadataProvider;
import com.atlassian.confluence.plugins.gadgets.requestcontext.RequestContextBuilder;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.GadgetSpecProvider;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore;
import com.atlassian.gadgets.feed.GadgetFeedReaderFactory;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.plugin.event.events.PluginUninstalledEvent;
import com.atlassian.plugin.event.events.PluginUpgradedEvent;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class CachingGadgetsMacroMetadataProvider
implements MacroMetadataProvider,
InitializingBean,
DisposableBean {
    @VisibleForTesting
    static final String CACHE_NAME = CachingGadgetsMacroMetadataProvider.class.getName();
    @VisibleForTesting
    static final String CACHE_KEY = "Key";
    private static final Logger log = LoggerFactory.getLogger(CachingGadgetsMacroMetadataProvider.class);
    private final EventPublisher eventPublisher;
    private final GadgetCacheFlusher gadgetCacheFlusher;
    private final GadgetsMacroMetadataProvider delegateProvider;
    private final Supplier<Cache<String, CachedMetadata>> cache;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean invalidationRequested = new AtomicBoolean();

    public CachingGadgetsMacroMetadataProvider(CacheManager cacheManager, GadgetSpecProvider gadgetSpecProvider, ExternalGadgetSpecStore gadgetStore, SubscribedGadgetFeedStore feedStore, GadgetFeedReaderFactory gadgetFeedReaderFactory, GadgetSpecFactory gadgetSpecFactory, RequestContextBuilder requestContextBuilder, I18nResolver resolver, EventPublisher eventPublisher, GadgetUsageTracker gadgetUsageTracker) {
        this((Supplier<Cache<String, CachedMetadata>>)Lazy.supplier(() -> cacheManager.getCache(CACHE_NAME, null, new CacheSettingsBuilder().local().maxEntries(1).build())), new GadgetCacheFlusher(cacheManager, gadgetUsageTracker, eventPublisher), new GadgetsMacroMetadataProvider(gadgetSpecProvider, gadgetStore, feedStore, gadgetFeedReaderFactory, gadgetSpecFactory, requestContextBuilder, resolver, eventPublisher), eventPublisher);
    }

    @VisibleForTesting
    CachingGadgetsMacroMetadataProvider(Supplier<Cache<String, CachedMetadata>> cache, GadgetCacheFlusher gadgetCacheFlusher, GadgetsMacroMetadataProvider delegateProvider, EventPublisher eventPublisher) {
        this.delegateProvider = Objects.requireNonNull(delegateProvider);
        this.gadgetCacheFlusher = Objects.requireNonNull(gadgetCacheFlusher);
        this.cache = Objects.requireNonNull(cache);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void afterPropertiesSet() {
        this.cache.get();
    }

    public void destroy() {
        this.clearMacroMetadataCache();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Collection<MacroMetadata> getData() {
        if (!this.lock.tryLock()) {
            return this.getMacroMetadataImmediately();
        }
        try {
            if (this.invalidationRequested.get()) {
                this.invalidationRequested.set(false);
                Collection<MacroMetadata> collection = this.retrieveMetadataAndUpdateCache().getMacroMetadata();
                return collection;
            }
            CachedMetadata cachedMetadata = (CachedMetadata)((Cache)this.cache.get()).get((Object)CACHE_KEY);
            if (cachedMetadata == null) {
                Collection<MacroMetadata> collection = this.retrieveMetadataAndUpdateCache().getMacroMetadata();
                return collection;
            }
            Set<URI> currentGadgetUris = this.getGadgetUris();
            if (cachedMetadata.matchesGadgetUris(currentGadgetUris)) {
                log.debug("Cached macro metadata matches current set of {} gadget URIs, returning cached macro metadata", (Object)currentGadgetUris.size());
                Collection<MacroMetadata> collection = cachedMetadata.getMacroMetadata();
                return collection;
            }
            log.debug("Cached macro metadata set of {} gadget URIs does not match current set of {} gadget URIs", (Object)cachedMetadata.getGadgetUris().size(), (Object)currentGadgetUris.size());
            Collection<MacroMetadata> collection = this.retrieveMetadataAndUpdateCache().getMacroMetadata();
            return collection;
        }
        finally {
            this.lock.unlock();
        }
    }

    private Collection<MacroMetadata> getMacroMetadataImmediately() {
        CachedMetadata cachedMetadata = (CachedMetadata)((Cache)this.cache.get()).get((Object)CACHE_KEY);
        if (cachedMetadata != null) {
            return cachedMetadata.getMacroMetadata();
        }
        return Collections.emptyList();
    }

    public Collection<MacroSummary> getSummaries() {
        return GadgetsMacroMetadataProvider.getSummaries(this.getData());
    }

    public MacroMetadata getByMacroName(String macroName) {
        return this.getByMacroNameAndId(macroName, null);
    }

    public MacroMetadata getByMacroNameAndId(String macroName, String alternateId) {
        return GadgetsMacroMetadataProvider.getByMacroNameAndId(macroName, alternateId, this::getData);
    }

    private CachedMetadata retrieveMetadataAndUpdateCache() {
        Set<URI> gadgetUris = this.delegateProvider.getGadgetUris();
        log.debug("Starting fetch and assembly of gadgets macro metadata for {} gadget URIs", (Object)gadgetUris.size());
        long start = System.currentTimeMillis();
        Collection<MacroMetadata> macroMetadata = this.delegateProvider.getMacroMetadata(gadgetUris);
        long end = System.currentTimeMillis();
        CachedMetadata cachedMetadata = new CachedMetadata((Set<URI>)ImmutableSet.copyOf(gadgetUris), (Collection<MacroMetadata>)ImmutableList.copyOf(macroMetadata));
        ((Cache)this.cache.get()).put((Object)CACHE_KEY, (Object)cachedMetadata);
        log.debug("Gadgets macro metadata assembly for {} gadget URIs took {} ms.", (Object)gadgetUris.size(), (Object)(end - start));
        this.gadgetCacheFlusher.requestGadgetsCacheFlush();
        this.eventPublisher.publish((Object)new MacroMetadataChangedEvent((Object)this));
        return cachedMetadata;
    }

    private Set<URI> getGadgetUris() {
        log.trace("Fetching gadget URIs first");
        long start = System.currentTimeMillis();
        Set<URI> gadgetUris = this.delegateProvider.getGadgetUris();
        long end = System.currentTimeMillis();
        log.trace("Fetched {} gadget URIs in {}ms", (Object)gadgetUris.size(), (Object)(end - start));
        return gadgetUris;
    }

    private void clearMacroMetadataCache() {
        log.debug("Clearing gadgets macro metadata cache");
        ((Cache)this.cache.get()).removeAll();
    }

    private void invalidateCache() {
        log.debug("Invalidating the cache");
        this.invalidationRequested.set(true);
    }

    @EventListener
    public void gadgetInstalled(GadgetInstalledEvent event) {
        log.debug("Gadget installed, uri={}", (Object)event.getGadgetUri());
        this.invalidateCache();
    }

    @EventListener
    public void gadgetUninstalled(GadgetUninstalledEvent event) {
        log.debug("Gadget uninstalled, uri={}", (Object)event.getGadgetUri());
        this.invalidateCache();
    }

    @EventListener
    public void pluginUninstalled(PluginUninstalledEvent event) {
        log.debug("Plugin {} uninstalled, clearing gadgets metadata cache since we assume it's now stale", (Object)event.getPlugin());
        this.invalidateCache();
    }

    @EventListener
    public void pluginUpgraded(PluginUpgradedEvent event) {
        log.debug("Plugin {} upgraded, clearing gadgets metadata cache since we assume it's now stale", (Object)event.getPlugin());
        this.invalidateCache();
    }
}

