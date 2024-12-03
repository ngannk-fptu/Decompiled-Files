/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.loaders.PluginLoader
 *  com.atlassian.plugin.loaders.SinglePluginLoader
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.plugin.ExtraBundledPluginsFactory;
import com.atlassian.plugin.loaders.PluginLoader;
import com.atlassian.plugin.loaders.SinglePluginLoader;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginLoadersFactory {
    private static final Logger log = LoggerFactory.getLogger(PluginLoadersFactory.class);
    public static final String EXTRA_BUNDLED_PLUGINS_SYSTEM_PROPERTY = "confluence.plugins.extra.bundled.locations";
    private final Collection<String> pluginModuleXmlLocations;
    private final Collection<PluginLoader> configuredLoaders;
    private final ExtraBundledPluginsFactory extraBundledPluginsFactory;

    public static Iterable<PluginLoader> getLoaders(Collection<String> pluginModuleXmlLocations, Collection<PluginLoader> configuredLoaders, ExtraBundledPluginsFactory extraBundledPluginsFactory) {
        return new PluginLoadersFactory(pluginModuleXmlLocations, configuredLoaders, extraBundledPluginsFactory).get();
    }

    public PluginLoadersFactory(Collection<String> pluginModuleXmlLocations, Collection<PluginLoader> configuredLoaders, ExtraBundledPluginsFactory extraBundledPluginsFactory) {
        this.pluginModuleXmlLocations = pluginModuleXmlLocations;
        this.configuredLoaders = configuredLoaders;
        this.extraBundledPluginsFactory = extraBundledPluginsFactory;
    }

    public List<PluginLoader> get() {
        ArrayList<Object> loaders = new ArrayList<Object>();
        for (String pluginModuleXmlLocation : this.pluginModuleXmlLocations) {
            loaders.add(new SinglePluginLoader(pluginModuleXmlLocation));
        }
        loaders.addAll(this.configuredLoaders);
        for (String extraBundledLocation : this.getExtraLocationsForSystemProperty(EXTRA_BUNDLED_PLUGINS_SYSTEM_PROPERTY)) {
            File location = new File(extraBundledLocation);
            if (!location.exists() || !location.canRead()) {
                log.error("Unable to load extra plugins from " + extraBundledLocation + " specified in -Dconfluence.plugins.extra.bundled.locations. file not found or unreadable");
                continue;
            }
            log.info("Loading additional plugins from " + extraBundledLocation);
            loaders.add(this.extraBundledPluginsFactory.newPluginLoader(location));
        }
        return Lists.newArrayList((Iterable)Collections2.filter(loaders, (Predicate)Predicates.notNull()));
    }

    private Collection<String> getExtraLocationsForSystemProperty(String systemProperty) {
        return Collections2.filter(Arrays.asList(System.getProperty(systemProperty, "").split(",")), StringUtils::isNotBlank);
    }
}

