/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet.download.plugin;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadStrategy;
import com.atlassian.plugin.servlet.download.plugin.DownloadStrategyModuleDescriptor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluggableDownloadStrategy
implements DownloadStrategy {
    private static final Logger log = LoggerFactory.getLogger(PluggableDownloadStrategy.class);
    private final Map<String, DownloadStrategy> strategies = new ConcurrentHashMap<String, DownloadStrategy>();

    public PluggableDownloadStrategy(PluginEventManager pluginEventManager) {
        pluginEventManager.register((Object)this);
    }

    @Override
    public boolean matches(String urlPath) {
        for (DownloadStrategy strategy : this.strategies.values()) {
            if (!strategy.matches(urlPath)) continue;
            log.debug("Matched plugin download strategy: {}", (Object)strategy.getClass().getName());
            return true;
        }
        return false;
    }

    @Override
    public void serveFile(HttpServletRequest request, HttpServletResponse response) throws DownloadException {
        for (DownloadStrategy strategy : this.strategies.values()) {
            if (!strategy.matches(request.getRequestURI().toLowerCase())) continue;
            strategy.serveFile(request, response);
            return;
        }
        throw new DownloadException("Found plugin download strategy during matching but not when trying to serve. Enable debug logging for more information.");
    }

    public void register(String key, DownloadStrategy strategy) {
        if (this.strategies.containsKey(key)) {
            log.warn("Replacing existing download strategy with module key: {}", (Object)key);
        }
        this.strategies.put(key, strategy);
    }

    public void unregister(String key) {
        this.strategies.remove(key);
    }

    @PluginEventListener
    public void pluginModuleEnabled(PluginModuleEnabledEvent event) {
        ModuleDescriptor module = event.getModule();
        if (!(module instanceof DownloadStrategyModuleDescriptor)) {
            return;
        }
        this.register(module.getCompleteKey(), (DownloadStrategy)module.getModule());
    }

    @PluginEventListener
    public void pluginModuleDisabled(PluginModuleDisabledEvent event) {
        ModuleDescriptor module = event.getModule();
        if (!(module instanceof DownloadStrategyModuleDescriptor)) {
            return;
        }
        this.unregister(module.getCompleteKey());
    }
}

