/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package com.atlassian.spring.extension;

import com.atlassian.spring.extension.registration.BeanRegistrationNamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class AtlassianExtensionNamespaceHandler
extends NamespaceHandlerSupport {
    public void init() {
        this.registerBeanDefinitionDecorator("registration", new BeanRegistrationNamespaceHandler());
    }
}

