/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.templaterenderer.plugins;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.templaterenderer.TemplateContextFactory;
import com.atlassian.templaterenderer.plugins.TemplateContextItemModuleDescriptor;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class TemplateContextFactoryImpl
implements TemplateContextFactory,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(TemplateContextFactoryImpl.class);
    private final PluginModuleTracker<Object, TemplateContextItemModuleDescriptor> templateContextItemTracker;

    public TemplateContextFactoryImpl(PluginAccessor pluginAccessor, PluginEventManager eventManager) {
        this.templateContextItemTracker = new DefaultPluginModuleTracker(pluginAccessor, eventManager, TemplateContextItemModuleDescriptor.class);
    }

    @Override
    public Map<String, Object> createContext(String pluginKey, Map<String, Object> contextParams) {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("context", context);
        for (TemplateContextItemModuleDescriptor desc : this.templateContextItemTracker.getModuleDescriptors()) {
            if (!desc.isGlobal() && !desc.getPluginKey().equals(pluginKey)) continue;
            try {
                context.put(desc.getContextKey(), desc.getModule());
            }
            catch (RuntimeException re) {
                log.error("Error loading module for " + desc.getPluginKey() + ":" + desc.getKey(), (Throwable)re);
            }
        }
        context.putAll(contextParams);
        return context;
    }

    public void destroy() {
        this.templateContextItemTracker.close();
    }
}

