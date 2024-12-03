/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.properties;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.troubleshooting.stp.properties.SupportDataXmlKeyResolver;
import com.atlassian.util.concurrent.CopyOnWriteMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PluginSupportDataXmlKeyResolver
implements SupportDataXmlKeyResolver {
    private static final String SUPPORT_DATA_XML = "support-data-xml";
    private static final Logger LOG = LoggerFactory.getLogger(PluginSupportDataXmlKeyResolver.class);
    private final Map<Plugin, Iterable<String>> xmlNamesResources = CopyOnWriteMap.builder().newHashMap();

    @Autowired
    public PluginSupportDataXmlKeyResolver(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        pluginEventManager.register((Object)this);
        this.resolveResources(pluginAccessor.getEnabledPlugins());
    }

    public static String getSupportInfoXmlTag() {
        return SUPPORT_DATA_XML;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Properties getKeyMappings() {
        Properties xmlNames = new Properties();
        for (Map.Entry<Plugin, Iterable<String>> pluginResources : this.xmlNamesResources.entrySet()) {
            for (String resource : pluginResources.getValue()) {
                InputStream stream = pluginResources.getKey().getClassLoader().getResourceAsStream(resource);
                if (stream == null) {
                    LOG.error("Error loading xml elements from resource file.");
                    continue;
                }
                try {
                    xmlNames.load(stream);
                }
                catch (IOException e) {
                    LOG.error("Error loading xml element names for use in application property files:", (Throwable)e);
                }
                finally {
                    IOUtils.closeQuietly((InputStream)stream);
                }
            }
        }
        return xmlNames;
    }

    @PluginEventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        this.xmlNamesResources.remove(event.getPlugin());
    }

    @PluginEventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        Plugin plugin = event.getPlugin();
        this.resolveResources(Collections.singleton(plugin));
    }

    private void resolveResources(Collection<Plugin> plugins) {
        for (Plugin plugin : plugins) {
            Collection descriptors = plugin.getResourceDescriptors().stream().filter(rd -> rd.getType() != null).filter(rd -> rd.getType().equals(SUPPORT_DATA_XML)).collect(Collectors.toList());
            if (descriptors.isEmpty()) continue;
            this.xmlNamesResources.put(plugin, descriptors.stream().map(ResourceDescriptor::getLocation).collect(Collectors.toSet()));
        }
    }
}

