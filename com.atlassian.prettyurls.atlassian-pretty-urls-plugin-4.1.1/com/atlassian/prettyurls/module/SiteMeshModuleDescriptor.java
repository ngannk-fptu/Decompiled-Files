/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.prettyurls.module;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.prettyurls.internal.util.UrlUtils;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteMeshModuleDescriptor
extends AbstractModuleDescriptor<Object> {
    private static final Logger log = LoggerFactory.getLogger(SiteMeshModuleDescriptor.class);
    private String path;

    public SiteMeshModuleDescriptor(@ComponentImport ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        String pathAttribute = element.attributeValue("path", "").trim();
        if (pathAttribute.isEmpty()) {
            log.error("You are required to have a path entry to get SiteMesh decoration.  Ignoring this module...");
        } else if (pathAttribute.equals("/")) {
            log.error("You cannot specify '{}' as a top level path.  Ignoring this module...", (Object)pathAttribute);
        } else {
            this.path = UrlUtils.startWithSlash(pathAttribute);
        }
    }

    public String getPath() {
        return this.path;
    }

    public Object getModule() {
        throw new UnsupportedOperationException("Not implemented this way");
    }
}

