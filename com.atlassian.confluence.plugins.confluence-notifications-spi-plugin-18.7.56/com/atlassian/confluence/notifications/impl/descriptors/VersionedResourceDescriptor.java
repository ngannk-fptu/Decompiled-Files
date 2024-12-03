/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  org.dom4j.Element
 *  org.dom4j.io.OutputFormat
 *  org.dom4j.io.XMLWriter
 */
package com.atlassian.confluence.notifications.impl.descriptors;

import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.impl.VersionedResourceTransformer;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class VersionedResourceDescriptor
extends WebResourceModuleDescriptor {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forClass(VersionedResourceDescriptor.class);
    private final PluginController pluginController;
    private List<WebResourceModuleDescriptor> webResourceModuleDescriptors;

    public VersionedResourceDescriptor(ModuleFactory moduleFactory, PluginController pluginController) {
        super(moduleFactory, null);
        this.pluginController = pluginController;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        Pair<Element, Iterable<Element>> webResourceDescriptors = VersionedResourceTransformer.parse(element, () -> plugin).transform();
        ImmutableList.Builder webResourceModuleDescriptorBuilder = ImmutableList.builder();
        for (Element webResourceElement : (Iterable)webResourceDescriptors.right()) {
            WebResourceModuleDescriptor webResourceModuleDescriptor = new WebResourceModuleDescriptor(this.moduleFactory, null);
            webResourceModuleDescriptor.init(plugin, webResourceElement);
            plugin.addModuleDescriptor((ModuleDescriptor)webResourceModuleDescriptor);
            webResourceModuleDescriptorBuilder.add((Object)webResourceModuleDescriptor);
        }
        super.init(plugin, (Element)webResourceDescriptors.left());
        this.webResourceModuleDescriptors = webResourceModuleDescriptorBuilder.build();
        log.onlyTrace("versioned resources:\n%s\n", new LazyToString(webResourceDescriptors));
    }

    public void enabled() {
        for (WebResourceModuleDescriptor webResourceModuleDescriptor : this.webResourceModuleDescriptors) {
            webResourceModuleDescriptor.enabled();
        }
        super.enabled();
    }

    public void disabled() {
        super.disabled();
        for (WebResourceModuleDescriptor webResourceModuleDescriptor : Lists.reverse(this.webResourceModuleDescriptors)) {
            webResourceModuleDescriptor.disabled();
        }
    }

    private static class LazyToString {
        private final Pair<Element, Iterable<Element>> webResourceDescriptors;

        public LazyToString(Pair<Element, Iterable<Element>> webResourceDescriptors) {
            this.webResourceDescriptors = webResourceDescriptors;
        }

        public String toString() {
            OutputFormat format = OutputFormat.createPrettyPrint();
            StringWriter out = new StringWriter();
            XMLWriter writer = new XMLWriter((Writer)out, format);
            try {
                for (Element e : (Iterable)this.webResourceDescriptors.right()) {
                    writer.write(e);
                }
                writer.write((Element)this.webResourceDescriptors.left());
                writer.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return out.toString();
        }
    }
}

