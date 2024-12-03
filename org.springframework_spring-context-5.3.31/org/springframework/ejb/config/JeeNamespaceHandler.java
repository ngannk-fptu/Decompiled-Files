/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.springframework.ejb.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.ejb.config.JndiLookupBeanDefinitionParser;
import org.springframework.ejb.config.LocalStatelessSessionBeanDefinitionParser;
import org.springframework.ejb.config.RemoteStatelessSessionBeanDefinitionParser;

public class JeeNamespaceHandler
extends NamespaceHandlerSupport {
    public void init() {
        this.registerBeanDefinitionParser("jndi-lookup", (BeanDefinitionParser)new JndiLookupBeanDefinitionParser());
        this.registerBeanDefinitionParser("local-slsb", (BeanDefinitionParser)new LocalStatelessSessionBeanDefinitionParser());
        this.registerBeanDefinitionParser("remote-slsb", (BeanDefinitionParser)new RemoteStatelessSessionBeanDefinitionParser());
    }
}

