/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.failurecache.CacheRefreshService
 *  com.google.common.base.Preconditions
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.navlink.caching;

import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent;
import com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.failurecache.CacheRefreshService;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ApplicationLinkChangeListener
implements InitializingBean,
DisposableBean {
    private final EventPublisher eventPublisher;
    private final CacheRefreshService cacheRefreshService;

    public ApplicationLinkChangeListener(EventPublisher eventPublisher, CacheRefreshService cacheRefreshService) {
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
        this.cacheRefreshService = (CacheRefreshService)Preconditions.checkNotNull((Object)cacheRefreshService);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onApplicationLinkAdded(ApplicationLinkAddedEvent event) {
        this.clearCaches();
    }

    @EventListener
    public void onApplicationLinkDeleted(ApplicationLinkDeletedEvent event) {
        this.clearCaches();
    }

    @EventListener
    public void onApplicationLinkIdChanged(ApplicationLinksIDChangedEvent event) {
        this.clearCaches();
    }

    private void clearCaches() {
        this.cacheRefreshService.refreshAll(true);
    }
}

