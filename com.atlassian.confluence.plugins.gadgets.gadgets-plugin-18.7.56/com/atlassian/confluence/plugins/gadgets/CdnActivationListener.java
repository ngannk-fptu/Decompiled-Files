/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent
 *  com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.event.ClearSpecCacheEvent
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.gadgets;

import com.atlassian.confluence.event.events.admin.SiteDarkFeatureDisabledEvent;
import com.atlassian.confluence.event.events.admin.SiteDarkFeatureEnabledEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.event.ClearSpecCacheEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class CdnActivationListener
implements InitializingBean,
DisposableBean {
    private final EventPublisher eventPublisher;

    public CdnActivationListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void handleCdnEnabled(SiteDarkFeatureEnabledEvent event) {
        this.handleStateChange(event.getFeatureKey());
    }

    @EventListener
    public void handleCdnDisabled(SiteDarkFeatureDisabledEvent event) {
        this.handleStateChange(event.getFeatureKey());
    }

    private void handleStateChange(String featureKey) {
        if ("atlassian.cdn.static.assets".equals(featureKey)) {
            this.eventPublisher.publish((Object)new ClearSpecCacheEvent());
        }
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

