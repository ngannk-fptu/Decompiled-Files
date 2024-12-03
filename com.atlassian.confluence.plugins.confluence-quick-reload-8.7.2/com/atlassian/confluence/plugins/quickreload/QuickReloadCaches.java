/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.cluster.ClusterAccessModeEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.confluence.event.events.cluster.ClusterMaintenanceBannerEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentEvent
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.quickreload;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.cluster.ClusterAccessModeEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.cluster.ClusterMaintenanceBannerEvent;
import com.atlassian.confluence.event.events.content.comment.CommentEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import java.util.Objects;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ParametersAreNonnullByDefault
@Component
public class QuickReloadCaches
implements InitializingBean,
DisposableBean {
    private static final String CACHE_NAME = QuickReloadCaches.class.getName() + ".lastUpdate";
    private static final String MAINTENANCE_UPDATE = "maintenance";
    private final Supplier<Cache<String, Long>> lastUpdateCacheRef = Lazy.supplier(() -> QuickReloadCaches.createCache(cacheFactory));
    private final EventPublisher eventPublisher;

    @Autowired
    public QuickReloadCaches(@ComponentImport EventPublisher eventPublisher, @ComponentImport CacheFactory cacheFactory) {
        this.eventPublisher = eventPublisher;
    }

    private static Cache<String, Long> createCache(CacheFactory cacheFactory) {
        CacheSettings cacheSettings = new CacheSettingsBuilder().remote().replicateViaCopy().replicateAsynchronously().build();
        return cacheFactory.getCache(CACHE_NAME, null, cacheSettings);
    }

    public boolean hasUpdates(long contentId, long since) {
        Long lastUpdate = (Long)this.lastUpdateCache().get((Object)QuickReloadCaches.cacheKey(contentId));
        if (lastUpdate != null) {
            return lastUpdate > since;
        }
        return true;
    }

    public boolean hasAccessModeUpdate(long since) {
        Long lastUpdate = (Long)this.lastUpdateCache().get((Object)MAINTENANCE_UPDATE);
        if (lastUpdate != null) {
            return lastUpdate > since;
        }
        return true;
    }

    public void updateLastUpdate(long contentId, long timestamp) {
        this.updateActivity(QuickReloadCaches.cacheKey(contentId), timestamp);
    }

    @EventListener
    public void onAccessModeChanged(ClusterAccessModeEvent event) {
        this.updateActivity(MAINTENANCE_UPDATE, event.getTimestamp());
    }

    @EventListener
    public void onBannerChanged(ClusterMaintenanceBannerEvent event) {
        this.updateActivity(MAINTENANCE_UPDATE, event.getTimestamp());
    }

    @EventListener
    public void onBannerChanged(ClusterEventWrapper eventWrapper) {
        if (eventWrapper.getEvent() instanceof ClusterMaintenanceBannerEvent) {
            this.onBannerChanged((ClusterMaintenanceBannerEvent)eventWrapper.getEvent());
        }
    }

    @EventListener
    public void onPageUpdate(PageUpdateEvent evt) {
        this.updateLastUpdate(evt.getPage().getId(), evt.getTimestamp());
    }

    @EventListener
    public void onComment(CommentEvent evt) {
        ContentEntityObject owner = evt.getComment().getContainer();
        Objects.requireNonNull(owner, "comments must have a container");
        this.updateLastUpdate(owner.getId(), evt.getTimestamp());
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.lastUpdateCache();
        this.eventPublisher.register((Object)this);
    }

    private Cache<String, Long> lastUpdateCache() {
        return (Cache)this.lastUpdateCacheRef.get();
    }

    private static String cacheKey(long contentId) {
        return String.valueOf(contentId);
    }

    private void updateActivity(String activity, long timestamp) {
        Long lastUpdate = (Long)this.lastUpdateCache().get((Object)activity, () -> timestamp);
        if (lastUpdate < timestamp) {
            this.lastUpdateCache().put((Object)activity, (Object)timestamp);
        }
    }
}

