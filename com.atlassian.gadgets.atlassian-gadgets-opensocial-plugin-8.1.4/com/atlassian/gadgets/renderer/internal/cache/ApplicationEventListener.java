/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.event.ClearHttpCacheEvent
 *  com.atlassian.gadgets.event.ClearSpecCacheEvent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.gadgets.renderer.internal.cache;

import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent;
import com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.event.ClearHttpCacheEvent;
import com.atlassian.gadgets.event.ClearSpecCacheEvent;
import com.atlassian.gadgets.renderer.internal.cache.ClearableCacheProvider;
import com.atlassian.gadgets.renderer.internal.guice.InjectorProvider;
import com.atlassian.gadgets.renderer.internal.local.LocalGadgetSpecFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.InitializingBean;

public class ApplicationEventListener
implements InitializingBean {
    private final EventPublisher eventPublisher;
    private final ClearableCacheProvider cacheProvider;
    private final InjectorProvider injectorProvider;

    public ApplicationEventListener(@ComponentImport EventPublisher eventPublisher, ClearableCacheProvider cacheProvider, InjectorProvider injectorProvider) {
        this.eventPublisher = eventPublisher;
        this.cacheProvider = cacheProvider;
        this.injectorProvider = injectorProvider;
    }

    @EventListener
    public void onClearCache(ClearHttpCacheEvent clearShindigCacheEvent) {
        this.cacheProvider.clear();
    }

    @EventListener
    public void onCreate(ApplicationLinkAddedEvent applicationLinkEvent) {
        this.cacheProvider.clear();
    }

    @EventListener
    public void onDelete(ApplicationLinkDeletedEvent applicationLinkEvent) {
        this.cacheProvider.clear();
    }

    @EventListener
    public void onUpdateId(ApplicationLinksIDChangedEvent applicationLinkEvent) {
        this.cacheProvider.clear();
    }

    @EventListener
    public void onClearSpecCacheEvent(ClearSpecCacheEvent clearSpecCacheEvent) {
        ((LocalGadgetSpecFactory)this.injectorProvider.get().getInstance(LocalGadgetSpecFactory.class)).clearCache();
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }
}

