/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionDecorator
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package com.atlassian.plugin.spring.pluginns;

import com.atlassian.plugin.spring.pluginns.PluginAvailableBeanDefinitionDecorator;
import com.atlassian.plugin.spring.pluginns.PluginContextClassLoaderStrategyBeanDefinitionDecorator;
import com.atlassian.plugin.spring.pluginns.PluginInterfaceBeanDefinitionDecorator;
import com.atlassian.plugin.spring.pluginns.PluginTrackBundleBeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class PluginNamespaceHandler
extends NamespaceHandlerSupport {
    public void init() {
        super.registerBeanDefinitionDecorator("interface", (BeanDefinitionDecorator)new PluginInterfaceBeanDefinitionDecorator());
        super.registerBeanDefinitionDecoratorForAttribute("available", (BeanDefinitionDecorator)new PluginAvailableBeanDefinitionDecorator());
        super.registerBeanDefinitionDecoratorForAttribute("contextClassLoader", (BeanDefinitionDecorator)new PluginContextClassLoaderStrategyBeanDefinitionDecorator());
        super.registerBeanDefinitionDecoratorForAttribute("trackBundle", (BeanDefinitionDecorator)new PluginTrackBundleBeanDefinitionDecorator());
        super.registerBeanDefinitionDecoratorForAttribute("ccls", (BeanDefinitionDecorator)new PluginContextClassLoaderStrategyBeanDefinitionDecorator());
    }
}

