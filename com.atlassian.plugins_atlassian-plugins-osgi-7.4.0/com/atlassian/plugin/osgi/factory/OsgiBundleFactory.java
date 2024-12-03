/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.factories.AbstractPluginFactory
 *  com.atlassian.plugin.impl.UnloadablePlugin
 *  com.atlassian.plugin.parsers.DescriptorParser
 *  com.atlassian.plugin.parsers.DescriptorParserFactory
 *  com.atlassian.plugin.parsers.XmlDescriptorParserFactory
 *  com.atlassian.plugin.parsers.XmlDescriptorParserUtils
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.factories.AbstractPluginFactory;
import com.atlassian.plugin.impl.UnloadablePlugin;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.factory.OsgiBundlePlugin;
import com.atlassian.plugin.osgi.factory.OsgiChainedModuleDescriptorFactoryCreator;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.DescriptorParserFactory;
import com.atlassian.plugin.parsers.XmlDescriptorParserFactory;
import com.atlassian.plugin.parsers.XmlDescriptorParserUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OsgiBundleFactory
extends AbstractPluginFactory {
    private static final Logger log = LoggerFactory.getLogger(OsgiBundleFactory.class);
    private static final Predicate<Integer> IS_PLUGIN_2_OR_HIGHER = input -> input != null && input >= 2;
    private final OsgiContainerManager osgiContainerManager;
    private final String pluginDescriptorFileName;
    private final OsgiChainedModuleDescriptorFactoryCreator osgiChainedModuleDescriptorFactoryCreator;

    public OsgiBundleFactory(OsgiContainerManager osgi) {
        this("atlassian-plugin.xml", osgi);
    }

    public OsgiBundleFactory(String pluginDescriptorFileName, OsgiContainerManager osgi) {
        super((DescriptorParserFactory)new XmlDescriptorParserFactory(), (Set)ImmutableSet.of());
        this.pluginDescriptorFileName = (String)Preconditions.checkNotNull((Object)pluginDescriptorFileName);
        this.osgiContainerManager = (OsgiContainerManager)Preconditions.checkNotNull((Object)osgi, (Object)"The osgi container is required");
        this.osgiChainedModuleDescriptorFactoryCreator = new OsgiChainedModuleDescriptorFactoryCreator(this.osgiContainerManager::getServiceTracker);
    }

    protected InputStream getDescriptorInputStream(PluginArtifact pluginArtifact) {
        return pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);
    }

    protected Predicate<Integer> isValidPluginsVersion() {
        return IS_PLUGIN_2_OR_HIGHER;
    }

    public String canCreate(PluginArtifact pluginArtifact) {
        Attributes attrs;
        Preconditions.checkNotNull((Object)pluginArtifact, (Object)"The plugin artifact is required");
        boolean isPlugin = this.hasDescriptor((PluginArtifact)Preconditions.checkNotNull((Object)pluginArtifact));
        boolean hasSpring = pluginArtifact.containsSpringContext();
        boolean isTransformless = this.getPluginKeyFromManifest((PluginArtifact)Preconditions.checkNotNull((Object)pluginArtifact)) != null;
        Manifest mf = OsgiHeaderUtil.getManifest(pluginArtifact);
        String key = null;
        if (!isTransformless && !isPlugin && mf != null && (attrs = mf.getMainAttributes()).containsKey(new Attributes.Name("Bundle-SymbolicName"))) {
            key = OsgiHeaderUtil.getPluginKey(mf);
        }
        if (key == null && isTransformless && !hasSpring) {
            key = isPlugin ? this.getPluginKeyFromDescriptor((PluginArtifact)Preconditions.checkNotNull((Object)pluginArtifact)) : this.getPluginKeyFromManifest(pluginArtifact);
        }
        return key;
    }

    public Plugin create(PluginArtifact pluginArtifact, ModuleDescriptorFactory moduleDescriptorFactory) {
        OsgiBundlePlugin plugin;
        Preconditions.checkNotNull((Object)pluginArtifact, (Object)"The plugin artifact is required");
        Preconditions.checkNotNull((Object)moduleDescriptorFactory, (Object)"The module descriptor factory is required");
        String pluginKey = this.canCreate(pluginArtifact);
        if (null == pluginKey) {
            log.warn("Unable to load plugin from '{}'", (Object)pluginArtifact);
            return new UnloadablePlugin("PluginArtifact has no manifest or is not a bundle: '" + pluginArtifact + "'");
        }
        try (InputStream pluginDescriptor = pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);){
            plugin = new OsgiBundlePlugin(this.osgiContainerManager, pluginKey, pluginArtifact);
            if (pluginDescriptor != null) {
                ModuleDescriptorFactory combinedFactory = this.osgiChainedModuleDescriptorFactoryCreator.create(arg_0 -> ((PluginArtifact)pluginArtifact).doesResourceExist(arg_0), moduleDescriptorFactory);
                DescriptorParser parser = this.descriptorParserFactory.getInstance(pluginDescriptor, this.applications);
                plugin = parser.configurePlugin(combinedFactory, (Plugin)plugin);
            }
        }
        catch (IOException ex) {
            log.error("Unable to load plugin: {}", (Object)pluginArtifact.toFile(), (Object)ex);
            plugin = new UnloadablePlugin("Unable to load plugin: " + ex.getMessage());
        }
        return plugin;
    }

    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        if (plugin instanceof OsgiBundlePlugin) {
            ModuleDescriptorFactory combinedFactory = this.osgiChainedModuleDescriptorFactoryCreator.create((String name) -> false, moduleDescriptorFactory);
            return XmlDescriptorParserUtils.addModule((ModuleDescriptorFactory)combinedFactory, (Plugin)plugin, (Element)module);
        }
        return null;
    }

    private String getPluginKeyFromManifest(PluginArtifact pluginArtifact) {
        Manifest mf = OsgiHeaderUtil.getManifest(pluginArtifact);
        if (mf != null) {
            String key = mf.getMainAttributes().getValue("Atlassian-Plugin-Key");
            String version = mf.getMainAttributes().getValue("Bundle-Version");
            if (key != null) {
                if (version != null) {
                    return key;
                }
                log.warn("Found plugin key '{}' in the manifest but no bundle version, so it can't be loaded as an OsgiPlugin", (Object)key);
            }
        }
        return null;
    }
}

