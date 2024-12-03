/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor.rpc;

import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.spring.container.ContainerManager;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public abstract class RpcModuleDescriptor
extends AbstractModuleDescriptor<Object> {
    protected String servicePath;
    protected String publishedInterface;
    protected boolean authenticated;
    private PluginModuleHolder<Object> rpcHandler;
    private String springBeanName;

    protected abstract void resetServerConfig();

    protected RpcModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        Element springBeanNameElement;
        super.init(plugin, element);
        this.servicePath = element.element("service-path").getTextTrim();
        Element auth = element.element("authenticate");
        if (auth != null) {
            this.authenticated = "true".equalsIgnoreCase(auth.getTextTrim());
        }
        if ((springBeanNameElement = element.element("springBeanName")) != null) {
            this.springBeanName = springBeanNameElement.getTextTrim();
        }
        this.rpcHandler = PluginModuleHolder.getInstanceWithDefaultFactory(this);
    }

    public Object getModule() {
        if (StringUtils.isNotBlank((CharSequence)this.springBeanName)) {
            return ContainerManager.getComponent((String)this.springBeanName);
        }
        return this.rpcHandler.getModule();
    }

    public void enabled() {
        super.enabled();
        this.rpcHandler.enabled(this.getModuleClass());
        this.resetServerConfig();
    }

    public void disabled() {
        this.resetServerConfig();
        this.rpcHandler.disabled();
        super.disabled();
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public String getServicePath() {
        return this.servicePath;
    }

    public Class getPublishedInterface() throws ClassNotFoundException {
        return this.plugin.loadClass(this.publishedInterface, this.getModuleClass());
    }
}

