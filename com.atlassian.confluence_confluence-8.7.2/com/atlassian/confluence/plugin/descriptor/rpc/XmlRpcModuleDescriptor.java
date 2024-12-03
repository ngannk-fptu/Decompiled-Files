/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugin.descriptor.rpc;

import com.atlassian.confluence.plugin.descriptor.rpc.RpcModuleDescriptor;
import com.atlassian.confluence.rpc.RpcServer;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class XmlRpcModuleDescriptor
extends RpcModuleDescriptor {
    private static final Logger log = LoggerFactory.getLogger(XmlRpcModuleDescriptor.class);
    private final RpcServer xmlRpcServer;

    public XmlRpcModuleDescriptor(ModuleFactory moduleFactory, @Qualifier(value="xmlRpcServer") RpcServer xmlRpcServer) {
        super(moduleFactory);
        this.xmlRpcServer = xmlRpcServer;
    }

    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        if (this.authenticated) {
            this.publishedInterface = element.element("published-interface").getTextTrim();
        }
    }

    @Override
    protected void resetServerConfig() {
        if (this.xmlRpcServer == null) {
            log.error("Unable to reload XML-RPC server - no XML-RPC server component available");
        } else {
            this.xmlRpcServer.reloadConfiguration();
        }
    }
}

