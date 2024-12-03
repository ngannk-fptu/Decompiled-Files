/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.event.ClearHttpCacheEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistDisabledEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistEnabledEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleAddedEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleChangedEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleRemovedEvent
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.whitelist.core.caching;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.event.ClearHttpCacheEvent;
import com.atlassian.plugins.whitelist.core.applinks.ApplicationLinkRestrictivenessChangeEvent;
import com.atlassian.plugins.whitelist.events.WhitelistDisabledEvent;
import com.atlassian.plugins.whitelist.events.WhitelistEnabledEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleAddedEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleChangedEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleRemovedEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class GadgetsClearHttpCacheAdapter
implements InitializingBean,
DisposableBean {
    private final EventPublisher eventPublisher;

    public GadgetsClearHttpCacheAdapter(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onWhitelistEnabled(WhitelistEnabledEvent event) {
        this.publishClearCacheEvent();
    }

    @EventListener
    public void onWhitelistDisabled(WhitelistDisabledEvent event) {
        this.publishClearCacheEvent();
    }

    @EventListener
    public void onWhitelistRuleAdded(WhitelistRuleAddedEvent event) {
        this.publishClearCacheEvent();
    }

    @EventListener
    public void onWhitelistRuleChanged(WhitelistRuleChangedEvent event) {
        this.publishClearCacheEvent();
    }

    @EventListener
    public void onWhitelistRuleRemoved(WhitelistRuleRemovedEvent event) {
        this.publishClearCacheEvent();
    }

    @EventListener
    public void onWhitelistRestrictivenessChange(ApplicationLinkRestrictivenessChangeEvent event) {
        this.publishClearCacheEvent();
    }

    private void publishClearCacheEvent() {
        this.eventPublisher.publish((Object)ClearHttpCacheEvent.INSTANCE);
    }
}

