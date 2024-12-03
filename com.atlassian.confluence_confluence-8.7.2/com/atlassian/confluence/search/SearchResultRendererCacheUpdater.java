/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginUpgradedEvent
 */
package com.atlassian.confluence.search;

import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.search.SearchResultRendererCache;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginUpgradedEvent;

public class SearchResultRendererCacheUpdater {
    private SearchResultRendererCache searchResultRendererCache;

    @EventListener
    public void handleEvent(PluginEnabledEvent event) {
        this.handleInternal();
    }

    @EventListener
    public void handleEvent(PluginDisabledEvent event) {
        this.handleInternal();
    }

    @EventListener
    public void handleEvent(PluginUpgradedEvent event) {
        this.handleInternal();
    }

    @EventListener
    public void handleEvent(PluginFrameworkStartedEvent event) {
        this.handleInternal();
    }

    private void handleInternal() {
        this.searchResultRendererCache.updateCache(false);
    }

    public void setSearchResultRenderCache(SearchResultRendererCache cache) {
        this.searchResultRendererCache = cache;
    }
}

