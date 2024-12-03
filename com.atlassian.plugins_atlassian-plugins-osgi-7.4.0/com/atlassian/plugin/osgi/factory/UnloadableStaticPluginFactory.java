/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.factories.AbstractPluginFactory
 *  com.atlassian.plugin.impl.UnloadablePlugin
 *  com.atlassian.plugin.parsers.DescriptorParser
 *  com.atlassian.plugin.parsers.DescriptorParserFactory
 *  com.atlassian.plugin.parsers.XmlDescriptorParserFactory
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.io.IOUtils
 *  org.dom4j.Element
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.factories.AbstractPluginFactory;
import com.atlassian.plugin.impl.UnloadablePlugin;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.DescriptorParserFactory;
import com.atlassian.plugin.parsers.XmlDescriptorParserFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.io.IOUtils;
import org.dom4j.Element;

public final class UnloadableStaticPluginFactory
extends AbstractPluginFactory {
    private static final Predicate<Integer> IS_PLUGINS_1 = input -> input != null && input == 1;
    private final String pluginDescriptorFileName;

    public UnloadableStaticPluginFactory(String pluginDescriptorFileName) {
        super((DescriptorParserFactory)new XmlDescriptorParserFactory(), (Set)ImmutableSet.of());
        this.pluginDescriptorFileName = pluginDescriptorFileName;
    }

    protected InputStream getDescriptorInputStream(PluginArtifact pluginArtifact) {
        return pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);
    }

    protected Predicate<Integer> isValidPluginsVersion() {
        return IS_PLUGINS_1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Plugin create(PluginArtifact pluginArtifact, ModuleDescriptorFactory moduleDescriptorFactory) {
        UnloadablePlugin plugin;
        Preconditions.checkNotNull((Object)pluginArtifact, (Object)"The plugin deployment unit is required");
        Preconditions.checkNotNull((Object)moduleDescriptorFactory, (Object)"The module descriptor factory is required");
        InputStream pluginDescriptor = null;
        try {
            pluginDescriptor = pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);
            if (pluginDescriptor == null) {
                throw new PluginParseException("No descriptor found in classloader for : " + pluginArtifact);
            }
            DescriptorParser parser = this.descriptorParserFactory.getInstance(pluginDescriptor, (Set)ImmutableSet.of());
            plugin = new UnloadablePlugin();
            try {
                parser.configurePlugin(moduleDescriptorFactory, (Plugin)plugin);
            }
            catch (Exception ex) {
                plugin.setKey(parser.getKey());
            }
            plugin.setErrorText("Unable to load the static '" + pluginArtifact + "' plugin from the plugins directory. Please copy this file into WEB-INF/lib and restart.");
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(pluginDescriptor);
            throw throwable;
        }
        IOUtils.closeQuietly((InputStream)pluginDescriptor);
        return plugin;
    }

    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        if (plugin instanceof UnloadablePlugin) {
            throw new PluginException("cannot create modules for an UnloadablePlugin");
        }
        return null;
    }
}

