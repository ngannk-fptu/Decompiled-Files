/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginParseException
 *  com.google.common.base.Preconditions
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 */
package com.atlassian.plugin.factories;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.classloader.PluginClassLoader;
import com.atlassian.plugin.factories.AbstractPluginFactory;
import com.atlassian.plugin.impl.DefaultDynamicPlugin;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.XmlDescriptorParserFactory;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.function.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public final class LegacyDynamicPluginFactory
extends AbstractPluginFactory {
    private static final Predicate<Integer> IS_PLUGIN_1 = input -> input != null && input <= 1;
    private final String pluginDescriptorFileName;
    private final File tempDirectory;

    public LegacyDynamicPluginFactory(String pluginDescriptorFileName) {
        this(pluginDescriptorFileName, new File(System.getProperty("java.io.tmpdir")), new XmlDescriptorParserFactory());
    }

    public LegacyDynamicPluginFactory(String pluginDescriptorFileName, File tempDirectory) {
        this(pluginDescriptorFileName, tempDirectory, new XmlDescriptorParserFactory());
    }

    public LegacyDynamicPluginFactory(String pluginDescriptorFileName, File tempDirectory, XmlDescriptorParserFactory xmlDescriptorParserFactory) {
        super(xmlDescriptorParserFactory, Collections.emptySet());
        this.tempDirectory = (File)Preconditions.checkNotNull((Object)tempDirectory);
        this.pluginDescriptorFileName = (String)Preconditions.checkNotNull((Object)pluginDescriptorFileName);
        Preconditions.checkState((boolean)StringUtils.isNotBlank((CharSequence)pluginDescriptorFileName), (Object)"Plugin descriptor name cannot be null or blank");
    }

    @Override
    protected InputStream getDescriptorInputStream(PluginArtifact pluginArtifact) {
        return pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);
    }

    @Override
    protected Predicate<Integer> isValidPluginsVersion() {
        return IS_PLUGIN_1;
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Plugin create(PluginArtifact pluginArtifact, ModuleDescriptorFactory moduleDescriptorFactory) {
        Preconditions.checkNotNull((Object)pluginArtifact, (Object)"The deployment unit must not be null");
        Preconditions.checkNotNull((Object)moduleDescriptorFactory, (Object)"The module descriptor factory must not be null");
        File file = pluginArtifact.toFile();
        Plugin plugin = null;
        InputStream pluginDescriptor = null;
        PluginClassLoader loader = null;
        try {
            pluginDescriptor = pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);
            if (pluginDescriptor == null) {
                throw new PluginParseException("No descriptor found in classloader for : " + file);
            }
            DescriptorParser parser = this.descriptorParserFactory.getInstance(pluginDescriptor, Collections.emptySet());
            loader = new PluginClassLoader(file, Thread.currentThread().getContextClassLoader(), this.tempDirectory);
            plugin = parser.configurePlugin(moduleDescriptorFactory, this.createPlugin(pluginArtifact, loader));
        }
        catch (PluginParseException e) {
            try {
                if (loader == null) throw e;
                loader.close();
                throw e;
                catch (RuntimeException e2) {
                    if (loader == null) throw new PluginParseException((Throwable)e2);
                    loader.close();
                    throw new PluginParseException((Throwable)e2);
                }
                catch (Error e3) {
                    if (loader == null) throw e3;
                    loader.close();
                    throw e3;
                }
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(pluginDescriptor);
                throw throwable;
            }
        }
        IOUtils.closeQuietly((InputStream)pluginDescriptor);
        return plugin;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        if (plugin instanceof DefaultDynamicPlugin) {
            ModuleDescriptor<?> moduleDescriptor;
            InputStream pluginDescriptor = null;
            try {
                PluginArtifact pluginArtifact = plugin.getPluginArtifact();
                pluginDescriptor = pluginArtifact.getResourceAsStream(this.pluginDescriptorFileName);
                DescriptorParser parser = this.descriptorParserFactory.getInstance(pluginDescriptor, Collections.emptySet());
                moduleDescriptor = parser.addModule(moduleDescriptorFactory, plugin, module);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(pluginDescriptor);
                throw throwable;
            }
            IOUtils.closeQuietly((InputStream)pluginDescriptor);
            return moduleDescriptor;
        }
        return null;
    }

    protected Plugin createPlugin(PluginArtifact pluginArtifact, PluginClassLoader loader) {
        return new DefaultDynamicPlugin(pluginArtifact, loader);
    }
}

