/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ejb.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.ejb.config.JndiLookupBeanDefinitionParser;
import org.springframework.ejb.config.LocalStatelessSessionBeanDefinitionParser;
import org.springframework.ejb.config.RemoteStatelessSessionBeanDefinitionParser;

public class JeeNamespaceHandler
extends NamespaceHandlerSupport {
    @Override
    public void init() {
        this.registerBeanDefinitionParser("jndi-lookup", new JndiLookupBeanDefinitionParser());
        this.registerBeanDefinitionParser("local-slsb", new LocalStatelessSessionBeanDefinitionParser());
        this.registerBeanDefinitionParser("remote-slsb", new RemoteStatelessSessionBeanDefinitionParser());
    }
}

