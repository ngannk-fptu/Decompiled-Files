/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.opensymphony.xwork2.config.ConfigurationManager
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.impl.struts.MultipartUploadConfigurator;
import com.atlassian.confluence.plugin.descriptor.StrutsModuleDescriptor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import com.opensymphony.xwork2.config.ConfigurationManager;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class XWorkModuleDescriptor
extends StrutsModuleDescriptor {
    private static final Logger LOG = LoggerFactory.getLogger(XWorkModuleDescriptor.class);

    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        LOG.debug("<xwork> modules are deprecated, use <struts> modules instead: " + this.getCompleteKey());
        super.init(plugin, element);
    }

    public XWorkModuleDescriptor(ModuleFactory moduleFactory, EventPublisher eventPublisher, ConfigurationManager configurationManager, MultipartUploadConfigurator multipartUploadConfigurator) {
        super(moduleFactory, eventPublisher, configurationManager, multipartUploadConfigurator);
    }
}

