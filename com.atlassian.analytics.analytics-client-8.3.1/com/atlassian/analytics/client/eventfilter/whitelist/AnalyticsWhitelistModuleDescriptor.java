/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.osgi.framework.Bundle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.eventfilter.whitelist;

import com.atlassian.analytics.client.eventfilter.parser.JsonListParser;
import com.atlassian.analytics.client.eventfilter.whitelist.FilteredEventAttributes;
import com.atlassian.analytics.client.eventfilter.whitelist.PluginWhitelistReader;
import com.atlassian.analytics.client.eventfilter.whitelist.Whitelist;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyticsWhitelistModuleDescriptor
extends AbstractModuleDescriptor<Whitelist> {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsWhitelistModuleDescriptor.class);
    private static final String GLOBAL_WHITELIST_PLUGIN_KEY = "com.atlassian.analytics.analytics-whitelist";
    private Whitelist whitelist;

    public AnalyticsWhitelistModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        super.init(plugin, element);
        String resourceName = element.attributeValue("resource");
        if (resourceName == null) {
            log.error("You are required to have a resource attribute to point to a whitelist file - ignoring this module.");
        } else {
            String whitelistId = plugin.getKey() + " (" + element.attributeValue("key") + ")";
            this.whitelist = plugin.getKey().equals(GLOBAL_WHITELIST_PLUGIN_KEY) ? this.readGlobalWhitelist(plugin, resourceName, whitelistId) : this.readPluginWhitelist(plugin, resourceName, whitelistId);
        }
    }

    private Whitelist readGlobalWhitelist(Plugin plugin, String resourceName, String whitelistId) {
        JsonListParser jsonListParser = new JsonListParser(arg_0 -> ((Plugin)plugin).getResourceAsStream(arg_0));
        Map<String, FilteredEventAttributes> attributes = jsonListParser.readJsonFilterList(resourceName);
        return new Whitelist(whitelistId, attributes, true);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Whitelist readPluginWhitelist(Plugin plugin, String resourceName, String whitelistId) {
        try (InputStream inputStream = this.getWhitelistResourceInputStream(plugin, resourceName);){
            if (inputStream != null) {
                Map<String, FilteredEventAttributes> eventAttributes = new PluginWhitelistReader().read(inputStream);
                Whitelist whitelist = new Whitelist(whitelistId, eventAttributes, false);
                return whitelist;
            }
            log.warn("No whitelist resource file found at [{}] in plugin [{}] (null InputStream)", (Object)resourceName, (Object)plugin.getKey());
            return new Whitelist(whitelistId, null, false);
        }
        catch (IOException ex) {
            log.error("Failed to read whitelist resource file [{}] in plugin [{}] due to: [{}]", new Object[]{resourceName, plugin.getKey(), ex.getMessage()});
        }
        return new Whitelist(whitelistId, null, false);
    }

    private InputStream getWhitelistResourceInputStream(Plugin plugin, String resourceName) throws IOException {
        if (plugin instanceof OsgiPlugin) {
            Bundle bundle = ((OsgiPlugin)plugin).getBundle();
            if (bundle != null) {
                URL entry = bundle.getEntry(resourceName);
                if (entry != null) {
                    return entry.openStream();
                }
                log.warn("No whitelist resource file found at [{}] in plugin [{}] (bundle entry not found)", (Object)resourceName, (Object)plugin.getKey());
            } else {
                log.warn("No whitelist resource file found at [{}] in plugin [{}] (null bundle)", (Object)resourceName, (Object)plugin.getKey());
            }
        } else {
            return plugin.getResourceAsStream(resourceName);
        }
        return null;
    }

    public Whitelist getModule() {
        Whitelist whitelist = this.getWhitelist();
        if (whitelist != null) {
            return whitelist;
        }
        throw new RuntimeException("Broken analytics whitelist configuration in module " + this.getCompleteKey() + ": see logs for errors");
    }

    public Whitelist getWhitelist() {
        return this.whitelist;
    }
}

