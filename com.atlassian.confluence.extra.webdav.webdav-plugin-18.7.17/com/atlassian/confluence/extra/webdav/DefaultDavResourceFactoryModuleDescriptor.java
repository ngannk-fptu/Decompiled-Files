/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.confluence.extra.webdav.DavResourceFactoryModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.Nonnull;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.dom4j.Element;

public class DefaultDavResourceFactoryModuleDescriptor
extends AbstractModuleDescriptor<DavResourceFactory>
implements DavResourceFactoryModuleDescriptor {
    private String workspaceName;

    public DefaultDavResourceFactoryModuleDescriptor(@ComponentImport ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.workspaceName = element.attributeValue("workspace");
    }

    @Override
    public DavResourceFactory getModule() {
        return (DavResourceFactory)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    @Override
    public String getWorkspaceName() {
        return this.workspaceName;
    }
}

