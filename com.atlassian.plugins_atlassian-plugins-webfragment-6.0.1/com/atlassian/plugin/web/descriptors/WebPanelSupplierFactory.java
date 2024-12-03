/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.model.EmbeddedTemplateWebPanel;
import com.atlassian.plugin.web.model.ResourceTemplateWebPanel;
import com.atlassian.plugin.web.model.WebPanel;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.Iterator;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

class WebPanelSupplierFactory {
    private final WebPanelModuleDescriptor webPanelModuleDescriptor;
    private final HostContainer hostContainer;
    private final ModuleFactory moduleFactory;

    public WebPanelSupplierFactory(WebPanelModuleDescriptor webPanelModuleDescriptor, HostContainer hostContainer, ModuleFactory moduleFactory) {
        this.webPanelModuleDescriptor = webPanelModuleDescriptor;
        this.hostContainer = hostContainer;
        this.moduleFactory = moduleFactory;
    }

    public Supplier<WebPanel> build(String moduleClassName) {
        if (moduleClassName != null) {
            return () -> (WebPanel)this.moduleFactory.createModule(moduleClassName, (ModuleDescriptor)this.webPanelModuleDescriptor);
        }
        ResourceDescriptor resource = this.getRequiredViewResource();
        String filename = resource.getLocation();
        if (StringUtils.isNotEmpty((CharSequence)filename)) {
            return () -> {
                ResourceTemplateWebPanel panel = (ResourceTemplateWebPanel)this.hostContainer.create(ResourceTemplateWebPanel.class);
                panel.setResourceFilename(filename);
                panel.setResourceType(this.getRequiredResourceType(resource));
                panel.setPlugin(this.webPanelModuleDescriptor.getPlugin());
                return panel;
            };
        }
        String body = (String)Preconditions.checkNotNull((Object)resource.getContent());
        return () -> {
            EmbeddedTemplateWebPanel panel = (EmbeddedTemplateWebPanel)this.hostContainer.create(EmbeddedTemplateWebPanel.class);
            panel.setTemplateBody(body);
            panel.setResourceType(this.getRequiredResourceType(resource));
            panel.setPlugin(this.webPanelModuleDescriptor.getPlugin());
            return panel;
        };
    }

    private ResourceDescriptor getRequiredViewResource() throws PluginParseException {
        Iterable resources = this.webPanelModuleDescriptor.getResourceDescriptors().stream().filter(resourceDescriptor -> "view".equals(resourceDescriptor.getName())).collect(Collectors.toList());
        Iterator iterator = resources.iterator();
        if (!iterator.hasNext()) {
            throw new PluginParseException("Required resource with name 'view' does not exist.");
        }
        return (ResourceDescriptor)iterator.next();
    }

    private String getRequiredResourceType(ResourceDescriptor resource) {
        String type = resource.getType();
        if (StringUtils.isEmpty((CharSequence)type)) {
            throw new PluginParseException("Resource element is lacking a type attribute.");
        }
        return type;
    }
}

