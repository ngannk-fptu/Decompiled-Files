/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.AbstractPluginFactory
 *  com.atlassian.plugin.impl.UnloadablePlugin
 *  com.atlassian.plugin.parsers.DescriptorParser
 *  com.atlassian.plugin.parsers.DescriptorParserFactory
 *  com.google.common.base.Preconditions
 *  org.apache.commons.io.IOUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.AbstractPluginFactory;
import com.atlassian.plugin.impl.UnloadablePlugin;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.factory.OsgiChainedModuleDescriptorFactoryCreator;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.plugin.osgi.factory.OsgiPluginXmlDescriptorParserFactory;
import com.atlassian.plugin.osgi.factory.transform.PluginTransformationException;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.DescriptorParserFactory;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.io.IOUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RemotablePluginFactory
extends AbstractPluginFactory {
    private static final Logger log = LoggerFactory.getLogger(RemotablePluginFactory.class);
    private static final Predicate<Integer> IS_PLUGINS_3 = input -> input == 3;
    private final OsgiContainerManager osgi;
    private final String pluginDescriptorFileName;
    private final PluginEventManager pluginEventManager;
    private final OsgiChainedModuleDescriptorFactoryCreator osgiChainedModuleDescriptorFactoryCreator;

    public RemotablePluginFactory(String pluginDescriptorFileName, Set<Application> applications, OsgiContainerManager osgi, PluginEventManager pluginEventManager) {
        super((DescriptorParserFactory)new OsgiPluginXmlDescriptorParserFactory(), applications);
        this.pluginDescriptorFileName = (String)Preconditions.checkNotNull((Object)pluginDescriptorFileName, (Object)"Plugin descriptor is required");
        this.osgi = (OsgiContainerManager)Preconditions.checkNotNull((Object)osgi, (Object)"The OSGi container is required");
        this.pluginEventManager = (PluginEventManager)Preconditions.checkNotNull((Object)pluginEventManager, (Object)"The plugin event manager is required");
        this.osgiChainedModuleDescriptorFactoryCreator = new OsgiChainedModuleDescriptorFactoryCreator(osgi::getServiceTracker);
    }

    protected InputStream getDescriptorInputStream(PluginArtifact pluginArtifact) {
        return pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);
    }

    protected Predicate<Integer> isValidPluginsVersion() {
        return IS_PLUGINS_3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Plugin create(PluginArtifact pluginArtifact, ModuleDescriptorFactory moduleDescriptorFactory) {
        Plugin plugin;
        Preconditions.checkNotNull((Object)pluginArtifact, (Object)"The plugin deployment unit is required");
        Preconditions.checkNotNull((Object)moduleDescriptorFactory, (Object)"The module descriptor factory is required");
        InputStream pluginDescriptor = null;
        try {
            pluginDescriptor = pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);
            if (pluginDescriptor == null) {
                throw new PluginParseException("Attempt to create Remotable plugin without a plugin descriptor!");
            }
            ModuleDescriptorFactory combinedFactory = this.getChainedModuleDescriptorFactory(moduleDescriptorFactory, pluginArtifact);
            DescriptorParser parser = this.descriptorParserFactory.getInstance(pluginDescriptor, this.applications);
            String pluginKey = parser.getKey();
            OsgiPlugin osgiPlugin = new OsgiPlugin(pluginKey, this.osgi, pluginArtifact, pluginArtifact, this.pluginEventManager);
            plugin = parser.configurePlugin(combinedFactory, (Plugin)osgiPlugin);
        }
        catch (PluginTransformationException ex) {
            Plugin plugin2;
            try {
                plugin2 = this.reportUnloadablePlugin(pluginArtifact.toFile(), ex);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(pluginDescriptor);
                throw throwable;
            }
            IOUtils.closeQuietly((InputStream)pluginDescriptor);
            return plugin2;
        }
        IOUtils.closeQuietly((InputStream)pluginDescriptor);
        return plugin;
    }

    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        return null;
    }

    private ModuleDescriptorFactory getChainedModuleDescriptorFactory(ModuleDescriptorFactory originalFactory, PluginArtifact pluginArtifact) {
        return this.osgiChainedModuleDescriptorFactoryCreator.create(arg_0 -> ((PluginArtifact)pluginArtifact).doesResourceExist(arg_0), originalFactory);
    }

    private Plugin reportUnloadablePlugin(File file, Exception e) {
        String msg = "Unable to load plugin: " + file;
        log.error(msg, (Throwable)e);
        UnloadablePlugin plugin = new UnloadablePlugin();
        plugin.setErrorText("Unable to load plugin: " + e.getMessage());
        return plugin;
    }
}

