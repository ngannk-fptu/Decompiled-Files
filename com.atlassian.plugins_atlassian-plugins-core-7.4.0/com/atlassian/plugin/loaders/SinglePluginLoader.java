/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.util.Assertions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.impl.StaticPlugin;
import com.atlassian.plugin.impl.UnloadablePlugin;
import com.atlassian.plugin.impl.UnloadablePluginFactory;
import com.atlassian.plugin.loaders.PluginLoader;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.DescriptorParserFactory;
import com.atlassian.plugin.parsers.XmlDescriptorParserFactory;
import com.atlassian.plugin.parsers.XmlDescriptorParserUtils;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.util.ClassLoaderUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SinglePluginLoader
implements PluginLoader {
    protected Collection<Plugin> plugins;
    private final String resource;
    private final URL url;
    private final DescriptorParserFactory descriptorParserFactory = new XmlDescriptorParserFactory();
    private static final Logger log = LoggerFactory.getLogger(SinglePluginLoader.class);

    public SinglePluginLoader(String resource) {
        this.resource = (String)Assertions.notNull((String)"resource", (Object)resource);
        this.url = null;
    }

    public SinglePluginLoader(URL url) {
        this.url = (URL)Assertions.notNull((String)"url", (Object)url);
        this.resource = null;
    }

    @Override
    public Iterable<Plugin> loadAllPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        if (this.plugins == null) {
            Plugin plugin;
            try {
                plugin = this.loadPlugin(moduleDescriptorFactory);
            }
            catch (RuntimeException ex) {
                String id = this.getIdentifier();
                log.error("Error loading plugin or descriptor: " + id, (Throwable)ex);
                plugin = new UnloadablePlugin(id + ": " + ex);
                plugin.setKey(id);
            }
            this.plugins = Collections.singleton(plugin);
        }
        return ImmutableList.copyOf(this.plugins);
    }

    @Override
    public boolean supportsRemoval() {
        return false;
    }

    @Override
    public boolean supportsAddition() {
        return false;
    }

    @Override
    public Iterable<Plugin> loadFoundPlugins(ModuleDescriptorFactory moduleDescriptorFactory) {
        throw new UnsupportedOperationException("This PluginLoader does not support addition.");
    }

    @Override
    public void removePlugin(Plugin plugin) {
        throw new PluginException("This PluginLoader does not support removal.");
    }

    @Override
    public boolean isDynamicPluginLoader() {
        return false;
    }

    @Override
    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        if (this.plugins.contains(plugin)) {
            return XmlDescriptorParserUtils.addModule(moduleDescriptorFactory, plugin, module);
        }
        return null;
    }

    protected Plugin loadPlugin(ModuleDescriptorFactory moduleDescriptorFactory) {
        Plugin plugin;
        InputStream source = this.getSource();
        if (source == null) {
            throw new PluginParseException("Invalid resource or inputstream specified to load plugins from.");
        }
        try {
            DescriptorParser parser = this.descriptorParserFactory.getInstance(source, (Set<Application>)ImmutableSet.of());
            plugin = parser.configurePlugin(moduleDescriptorFactory, this.getNewPlugin());
            if (plugin.getPluginsVersion() == 2) {
                UnloadablePlugin unloadablePlugin = UnloadablePluginFactory.createUnloadablePlugin(plugin);
                StringBuilder errorText = new StringBuilder("OSGi plugins cannot be deployed via the classpath, which is usually WEB-INF/lib.");
                if (this.resource != null) {
                    errorText.append("\n Resource is: ").append(this.resource);
                }
                if (this.url != null) {
                    errorText.append("\n URL is: ").append(this.url);
                }
                unloadablePlugin.setErrorText(errorText.toString());
                plugin = unloadablePlugin;
            }
        }
        catch (PluginParseException e) {
            throw new PluginParseException("Unable to load plugin resource: " + this.resource + " - " + e.getMessage(), (Throwable)e);
        }
        return plugin;
    }

    private String getIdentifier() {
        if (this.resource != null) {
            return this.resource;
        }
        if (this.url != null) {
            return this.url.getPath();
        }
        return null;
    }

    protected StaticPlugin getNewPlugin() {
        return new StaticPlugin();
    }

    protected InputStream getSource() {
        if (this.resource != null) {
            return ClassLoaderUtils.getResourceAsStream(this.resource, this.getClass());
        }
        if (this.url != null) {
            try {
                return this.url.openConnection().getInputStream();
            }
            catch (IOException e) {
                throw new PluginParseException((Throwable)e);
            }
        }
        throw new IllegalStateException("No defined method for getting an input stream.");
    }
}

