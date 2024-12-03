/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.PluginParseException
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.factories;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.factories.AbstractPluginFactory;
import com.atlassian.plugin.impl.XmlDynamicPlugin;
import com.atlassian.plugin.parsers.DescriptorParser;
import com.atlassian.plugin.parsers.XmlDescriptorParserFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Predicate;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public final class XmlDynamicPluginFactory
extends AbstractPluginFactory {
    private static final Logger log = LoggerFactory.getLogger(XmlDynamicPluginFactory.class);
    private static final Predicate<Integer> ALWAYS_TRUE = input -> true;

    public XmlDynamicPluginFactory(Application application) {
        this(Sets.newHashSet((Object[])new Application[]{application}));
    }

    public XmlDynamicPluginFactory(Set<Application> applications) {
        super(new XmlDescriptorParserFactory(), applications);
    }

    @Override
    protected InputStream getDescriptorInputStream(PluginArtifact pluginArtifact) {
        return pluginArtifact.getInputStream();
    }

    @Override
    protected Predicate<Integer> isValidPluginsVersion() {
        return ALWAYS_TRUE;
    }

    @Override
    public String canCreate(PluginArtifact pluginArtifact) {
        try {
            return super.canCreate(pluginArtifact);
        }
        catch (PluginParseException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DocumentException || cause instanceof SAXException) {
                log.debug("There was an error parsing the plugin descriptor for '{}'", (Object)pluginArtifact);
                log.debug("This is most probably because we parsed a jar, and the plugin is not an XML dynamic plugin. See the exception below for confirmation:", (Throwable)e);
                return null;
            }
            throw e;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Plugin create(PluginArtifact pluginArtifact, ModuleDescriptorFactory moduleDescriptorFactory) {
        Preconditions.checkNotNull((Object)pluginArtifact, (Object)"The plugin artifact must not be null");
        Preconditions.checkNotNull((Object)moduleDescriptorFactory, (Object)"The module descriptor factory must not be null");
        try (FileInputStream pluginDescriptor = new FileInputStream(pluginArtifact.toFile());){
            DescriptorParser parser = this.descriptorParserFactory.getInstance(pluginDescriptor, this.applications);
            Plugin plugin = parser.configurePlugin(moduleDescriptorFactory, new XmlDynamicPlugin(pluginArtifact));
            return plugin;
        }
        catch (IOException | RuntimeException e) {
            throw new PluginParseException((Throwable)e);
        }
    }

    public ModuleDescriptor<?> createModule(Plugin plugin, Element module, ModuleDescriptorFactory moduleDescriptorFactory) {
        if (plugin instanceof XmlDynamicPlugin) {
            throw new PluginException("cannot create modules for an XmlDynamicPlugin");
        }
        return null;
    }
}

