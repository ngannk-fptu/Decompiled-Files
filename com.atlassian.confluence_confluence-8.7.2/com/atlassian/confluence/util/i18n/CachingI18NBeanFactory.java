/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginUpgradedEvent
 *  com.atlassian.vcache.JvmCacheSettingsBuilder
 *  com.atlassian.vcache.VCacheFactory
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.event.events.admin.TranslationTransformStateChangedEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.plugin.PluginEvent;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.impl.vcache.UnblockingRemovalJvmCache;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.event.I18NCacheInitEvent;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginUpgradedEvent;
import com.atlassian.vcache.JvmCacheSettingsBuilder;
import com.atlassian.vcache.VCacheFactory;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingI18NBeanFactory
implements I18NBeanFactory {
    private static final Logger log = LoggerFactory.getLogger(CachingI18NBeanFactory.class);
    private final VCacheFactory cacheFactory;
    private final I18NBeanFactory defaultI18NBeanFactory;
    private final EventPublisher eventPublisher;
    private final boolean disableCache = Boolean.getBoolean("confluence.i18n.reloadbundles");
    private UnblockingRemovalJvmCache<Locale, I18NBean> cache;
    private ResettableLazyReference<String> cachedI18nStateHash;

    public CachingI18NBeanFactory(VCacheFactory cacheFactory, I18NBeanFactory defaultI18NBeanFactory, EventPublisher eventPublisher) {
        this.cacheFactory = Objects.requireNonNull(cacheFactory);
        this.defaultI18NBeanFactory = Objects.requireNonNull(defaultI18NBeanFactory);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @PostConstruct
    public void initialiseCacheAndRegisterEventListener() {
        this.cache = CoreCache.I18N_BY_LOCALE.resolve(cacheName -> new UnblockingRemovalJvmCache(this.cacheFactory, (String)cacheName, new JvmCacheSettingsBuilder().defaultTtl(Duration.ofDays(1000L)).build()));
        this.cachedI18nStateHash = new ResettableLazyReference<String>(){

            protected String create() {
                return CachingI18NBeanFactory.this.defaultI18NBeanFactory.getStateHash();
            }
        };
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void unregisterEventListener() {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public @NonNull I18NBean getI18NBean(@NonNull Locale locale) {
        return this.disableCache ? this.defaultI18NBeanFactory.getI18NBean(locale) : this.cache.get(locale, () -> {
            this.eventPublisher.publish((Object)new I18NCacheInitEvent(locale));
            log.debug("Caching i18nBean for locale [{}]", (Object)locale);
            return this.defaultI18NBeanFactory.getI18NBean(locale);
        });
    }

    @Override
    public @NonNull I18NBean getI18NBean() {
        return this.getI18NBean(LocaleManager.DEFAULT_LOCALE);
    }

    @Override
    public @NonNull String getStateHash() {
        return this.disableCache ? this.defaultI18NBeanFactory.getStateHash() : (String)this.cachedI18nStateHash.get();
    }

    @EventListener
    public void onGlobalSettingsChangedEvent(GlobalSettingsChangedEvent event) {
        this.clearCache("received GlobalSettingsChangedEvent");
    }

    @EventListener
    public void onPluginFrameworkStartedEvent(PluginFrameworkStartedEvent event) {
        this.clearCache("received PluginFrameworkStartedEvent");
    }

    @EventListener
    public void onPluginEvent(PluginEvent event) {
        this.clearCache("received " + event.getClass());
    }

    @EventListener
    public void onPluginSystemPluginEnabledEvent(PluginEnabledEvent pluginEnabledEvent) {
        this.clearCache("received PluginEnabledEvent");
    }

    @EventListener
    public void onPluginSystemPluginDisabledEvent(PluginDisabledEvent pluginDisabledEvent) {
        this.clearCache("received PluginDisabledEvent");
    }

    @EventListener
    public void onPluginSystemPluginUpgradedEvent(PluginUpgradedEvent pluginUpgradedEvent) {
        this.clearCache("received PluginUpgradedEvent");
    }

    @EventListener
    public void onRemoteEvent(ClusterEventWrapper wrapper) {
        Event event = wrapper.getEvent();
        if (event instanceof GlobalSettingsChangedEvent) {
            this.onGlobalSettingsChangedEvent((GlobalSettingsChangedEvent)event);
        } else if (event instanceof PluginEvent) {
            this.onPluginEvent((PluginEvent)event);
        }
    }

    @EventListener
    public void onTranslationTransformStateChangedEvent(TranslationTransformStateChangedEvent translationTransformStateChangedEvent) {
        this.clearCache("received TranslationTransformStateChangedEvent");
    }

    private void clearCache(String reason) {
        log.debug("Clearing i18n cache - {}", (Object)reason);
        this.cache.removeAll();
        this.cachedI18nStateHash.reset();
    }
}

