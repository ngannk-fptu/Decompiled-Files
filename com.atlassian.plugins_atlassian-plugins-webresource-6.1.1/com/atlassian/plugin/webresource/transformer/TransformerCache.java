/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  io.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.webresource.transformer.ContentTransformerModuleDescriptor;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformerModuleDescriptor;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerModuleDescriptor;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformerCache {
    private final ResettableLazyReference<Map<String, Object>> lazyReferenceTransformerCache;

    public TransformerCache(PluginEventManager pluginEventManager, final PluginAccessor pluginAccessor) {
        this.lazyReferenceTransformerCache = new ResettableLazyReference<Map<String, Object>>(){

            protected Map<String, Object> create() {
                HashMap<String, Object> keysToDescriptors = new HashMap<String, Object>();
                List contentTransformerDescriptor = pluginAccessor.getEnabledModuleDescriptorsByClass(ContentTransformerModuleDescriptor.class);
                for (Object descriptor : contentTransformerDescriptor) {
                    String aliasKey;
                    if (!keysToDescriptors.containsKey(descriptor.getKey())) {
                        keysToDescriptors.put(descriptor.getKey(), descriptor);
                    }
                    if ((aliasKey = descriptor.getAliasKey()) == null || keysToDescriptors.containsKey(aliasKey)) continue;
                    keysToDescriptors.put(aliasKey, descriptor);
                }
                List descriptors = pluginAccessor.getEnabledModuleDescriptorsByClass(UrlReadingWebResourceTransformerModuleDescriptor.class);
                for (UrlReadingWebResourceTransformerModuleDescriptor descriptor : descriptors) {
                    String aliasKey;
                    if (!keysToDescriptors.containsKey(descriptor.getKey())) {
                        keysToDescriptors.put(descriptor.getKey(), (Object)descriptor);
                    }
                    if ((aliasKey = descriptor.getAliasKey()) == null || keysToDescriptors.containsKey(aliasKey)) continue;
                    keysToDescriptors.put(aliasKey, (Object)descriptor);
                }
                List deprecatedDescriptors = pluginAccessor.getEnabledModuleDescriptorsByClass(WebResourceTransformerModuleDescriptor.class);
                for (WebResourceTransformerModuleDescriptor descriptor : deprecatedDescriptors) {
                    if (keysToDescriptors.containsKey(descriptor.getKey())) continue;
                    keysToDescriptors.put(descriptor.getKey(), (Object)descriptor);
                }
                return keysToDescriptors;
            }
        };
        pluginEventManager.register((Object)this);
    }

    @PluginEventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        this.lazyReferenceTransformerCache.reset();
    }

    @PluginEventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        this.lazyReferenceTransformerCache.reset();
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.lazyReferenceTransformerCache.reset();
    }

    @PluginEventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.lazyReferenceTransformerCache.reset();
    }

    public Object getDescriptor(String transformerKey) {
        return ((Map)this.lazyReferenceTransformerCache.get()).get(transformerKey);
    }
}

