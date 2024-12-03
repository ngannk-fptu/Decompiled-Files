/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.dom4j.XPath
 *  org.dom4j.xpath.DefaultXPath
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.notifications.impl.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.xpath.DefaultXPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AggregateModuleDescriptor<T>
extends AbstractModuleDescriptor<T> {
    private static final Logger log = LoggerFactory.getLogger(AggregateModuleDescriptor.class);
    protected final PluginController pluginController;
    private Iterable<ModuleDescriptor> aggregatedDescriptors;

    public AggregateModuleDescriptor(ModuleFactory moduleFactory, PluginController pluginController) {
        super(moduleFactory);
        this.pluginController = pluginController;
    }

    protected static XPath xpath(String xPathExpression) {
        return new DefaultXPath(xPathExpression);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element root) throws PluginParseException {
        super.init(plugin, root);
        Map<XPath, Function<Element, ModuleDescriptor>> descriptorFactories = this.getDescriptorFactories();
        ImmutableList.Builder aggregatedDescriptorsBuilder = ImmutableList.builder();
        for (Map.Entry<XPath, Function<Element, ModuleDescriptor>> descriptorFactory : descriptorFactories.entrySet()) {
            XPath descriptorPath = descriptorFactory.getKey();
            List descriptorCandidates = descriptorPath.selectNodes((Object)root);
            for (int position = 0; position < descriptorCandidates.size(); ++position) {
                Node descriptorCandidate = (Node)descriptorCandidates.get(position);
                if (descriptorCandidate == null || !(descriptorCandidate instanceof Element)) continue;
                Element descriptorElement = (Element)descriptorCandidate;
                ModuleDescriptor moduleDescriptor = (ModuleDescriptor)descriptorFactory.getValue().apply((Object)descriptorElement);
                String keyAttributeValue = descriptorElement.attributeValue("key");
                if (keyAttributeValue == null) {
                    descriptorElement.addAttribute("key", this.getKeyForDescriptorPath(descriptorPath, position));
                }
                moduleDescriptor.init(plugin, descriptorElement);
                plugin.addModuleDescriptor(moduleDescriptor);
                aggregatedDescriptorsBuilder.add((Object)moduleDescriptor);
            }
        }
        this.aggregatedDescriptors = aggregatedDescriptorsBuilder.build();
    }

    public void enabled() {
        super.enabled();
        for (ModuleDescriptor descriptor : this.aggregatedDescriptors) {
            log.debug("Enabling aggregate descriptor [" + descriptor.getClass().getSimpleName() + "] for [" + this.getCompleteKey() + "]");
            this.pluginController.enablePluginModule(descriptor.getCompleteKey());
        }
    }

    public void disabled() {
        for (ModuleDescriptor descriptor : this.aggregatedDescriptors) {
            log.debug("Disabling aggregate descriptor [" + descriptor.getClass().getSimpleName() + "] for [" + this.getCompleteKey() + "]");
            this.pluginController.disablePluginModule(descriptor.getCompleteKey());
        }
        super.disabled();
    }

    protected Map<XPath, Function<Element, ModuleDescriptor>> getDescriptorFactories() {
        return Collections.EMPTY_MAP;
    }

    protected String getKeyForDescriptorPath(XPath descriptorPath, int position) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.key);
        sb.append("-");
        sb.append(descriptorPath.getText());
        sb.append("-");
        sb.append(position);
        return sb.toString();
    }
}

