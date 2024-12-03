/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.jira.event.ClearCacheEvent
 *  com.atlassian.plugins.whitelist.events.ClearWhitelistCacheEvent
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.whitelist.core.caching;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.ClearCacheEvent;
import com.atlassian.plugins.whitelist.events.ClearWhitelistCacheEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class JiraClearCacheEventAdapter
implements InitializingBean,
DisposableBean {
    private final EventPublisher eventPublisher;

    public JiraClearCacheEventAdapter(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void destroy() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onClearCacheEvent(ClearCacheEvent event) {
        this.eventPublisher.publish((Object)new ClearWhitelistCacheEvent());
    }
}

