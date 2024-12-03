/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionDecorator
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package com.atlassian.spring.extension;

import com.atlassian.spring.extension.HostedOverrideBeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class HostedExtensionNamespaceHandler
extends NamespaceHandlerSupport {
    public void init() {
        super.registerBeanDefinitionDecoratorForAttribute("override", (BeanDefinitionDecorator)new HostedOverrideBeanDefinitionDecorator());
    }
}

