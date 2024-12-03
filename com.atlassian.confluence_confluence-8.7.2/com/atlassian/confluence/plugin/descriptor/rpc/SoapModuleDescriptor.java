/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor.rpc;

import com.atlassian.confluence.plugin.descriptor.rpc.RpcModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Element;

public class SoapModuleDescriptor
extends RpcModuleDescriptor {
    private String serviceName;

    public SoapModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.publishedInterface = element.element("published-interface").getTextTrim();
        this.serviceName = element.element("service-name").getTextTrim();
    }

    @Override
    protected void resetServerConfig() {
    }

    public String getServiceName() {
        return this.serviceName;
    }
}

